package ru.realalerting.alertsubscriber.clickhouse;

import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.agrona.concurrent.IdleStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import ru.realalerting.alertsubscriber.AlertConsumer;
import ru.realalerting.consumer.Consumer;
import ru.realalerting.protocol.RealAlertingDriverContext;
import ru.realalerting.reader.RealAlertingConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Karbayev Saruar
 */
public class ClickHouseSender extends AlertConsumer {
//    private ClickHouseNodes servers;
//    private ClickHouseClient client;
    public static final String TAB_SEPARATED_FORMAT = "TabSeparated";
    public static char TAB_VALUE_SPLITTER = '\t';
    private String insert = "INSERT INTO default.alerts (metric_id, value, timestamp) values (?, ?, ?)";
    private final Map<String, URI> preparedUris = new ConcurrentHashMap<>();
    private final HttpClient httpClient;
    private final ClickHouseProperties properties;
    private HttpEntity entity = null;
    private HttpPost httpPost;
    private HttpResponse response;

    public static class Alert {
        int metricId;
        long value;
        long timestamp;
    }

    static final EventTranslatorThreeArg<Alert, Integer, Long, Long>
            TRANSLATOR = (alert, sequence, metricId, timestamp, value) -> {
        alert.metricId = metricId;
        alert.timestamp = timestamp;
        alert.value = value;
    };

    private Disruptor<Alert> disruptor;
    StringBuilder sb = new StringBuilder();
    int batchSize = 0;
    int maxBatchSize = 200;
    final Executor executor = Executors.newSingleThreadExecutor();
    int bufferSize = 8192;
    private final Map<Alert, Integer> bufferMetric = new ConcurrentHashMap<>();
    final SimpleObjectPool pool = new SimpleObjectPool<>(10, 1000,
            () -> ByteBuffer.allocate(bufferSize),
            ByteBuffer::clear);
    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

    {
        disruptor = new Disruptor<>(
                Alert::new,
                bufferSize,
                DaemonThreadFactory.INSTANCE);


        disruptor.handleEventsWith((alert, sequence, endOfBatch) -> {
            buffer.putInt(alert.metricId);
            buffer.putChar(TAB_VALUE_SPLITTER);
            buffer.putLong(alert.value);
            buffer.putChar(TAB_VALUE_SPLITTER);
            buffer.putLong(alert.timestamp);
            buffer.putChar(TAB_VALUE_SPLITTER);
            batchSize++;
            if (batchSize == maxBatchSize || sb.length() > 100) {
                executor.execute(() -> {
                    SendToClickhouse();
                    pool.offer(buffer);
                });
                buffer = (ByteBuffer) pool.borrow();
            }
        });
        disruptor.start();
    }

    public ClickHouseSender(RealAlertingDriverContext aeronContext, RealAlertingConfig connectInfo,
                            IdleStrategy idleStrategy, int alertId, String url, HttpClient httpClient, ClickHouseProperties properties) {
        super(aeronContext, connectInfo, idleStrategy, alertId);
//        servers = ClickHouseNodes.of(url);
//        client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP);
        this.httpClient = httpClient;
//        properties.setUser("default");
//        properties.setPassword("fdsavcxz");
        this.properties = properties;
        URI uri = buildRequestUri(insert);
        httpPost = new HttpPost(uri);
    }

    public Consumer getSubscription() {
        return this.consumer;
    }

    private void SendToClickhouse() {
        InsertEntity insertEntity = InsertEntity.of(buffer);
        try {
            sendInsert(insertEntity, insert);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        buffer.clear();
    }

    private void sendInsert(HttpEntity content, String sql) throws Exception {
        try {
            httpPost.setEntity(content);
            response = httpClient.execute(httpPost);
            entity = response.getEntity(); // TODO error 400 bad request, возможно нужно еще добавлять separator между метриками
            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                String chMessage;
                try {
                    chMessage = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    chMessage = "error while read response " + e.getMessage();
                }
                throw new RuntimeException();
//                throw ClickHouseExceptionSpecifier.specify(chMessage, properties.getHost(), properties.getPort());
            }
//        } catch (ClickHouseException e) {
//            throw e;
        } catch (Exception e) {
            throw e;
//            throw ClickHouseExceptionSpecifier.specify(e, properties.getHost(), properties.getPort());
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }

    private URI buildRequestUri(String sql) {
        URI result = preparedUris.get(sql);
        if (result == null) {
            try {
                List<NameValuePair> queryParams = getUrlQueryParams(
                        sql
                );
                result = new URIBuilder()
                        .setScheme(properties.getSsl() ? "https" : "http")
                        .setHost(properties.getHost())
                        .setPort(properties.getPort())
                        .setPath("/")
                        .setParameters(queryParams)
                        .build();
            } catch (URISyntaxException e) {
                throw new IllegalStateException("illegal configuration of db", e);
            }
            preparedUris.put(sql, result);
        }
        return result;
    }

    private List<NameValuePair> getUrlQueryParams(
            String sql
    ) {
        List<NameValuePair> result = new ArrayList<NameValuePair>();

        if (sql != null) {
            result.add(new BasicNameValuePair("query", sql));
        }

        if (null != properties.getUser()) {
            result.add(new BasicNameValuePair("user", properties.getUser()));
        }

        if (null != properties.getPassword()) {
            result.add(new BasicNameValuePair("password", properties.getPassword()));
        }

        return result;
    }

    @Override
    public void onAlert(int metricId, long value, long timestamp) {
        System.out.println(value);
        disruptor.publishEvent(TRANSLATOR, metricId, timestamp, value);
    }
}
