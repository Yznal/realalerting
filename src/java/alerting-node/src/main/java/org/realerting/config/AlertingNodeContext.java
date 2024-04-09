package org.realerting.config;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import org.jetbrains.annotations.Contract;
import org.realerting.service.MetricAlertPublisher;
import org.realerting.service.MetricsClient;
import org.realerting.service.MetricsSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


/**
 * @author Mikhail Shadrin
 */
public class AlertingNodeContext implements AutoCloseable {
    private static AlertingNodeContext context = null;
    private static final Logger GLOBAL_LOGGER = LoggerFactory.getLogger("AlertingNode");

    public static void initialize() {
        var configuration = AlertingNodeConfiguration.getInstance();
        var publisherConfiguration = configuration.getPublisherConfiguration();
        var subscriberConfiguration = configuration.getSubscriberConfiguration();

        Aeron aeron_ = null;
        MediaDriver mediaDriver_ = null;
        if (publisherConfiguration.isIpcEnabled() || subscriberConfiguration.isIpcEnabled()) {
            try {
                aeron_ = Aeron.connect(new Aeron.Context()); // для работы с IPC необходим внешний медиа драйвер, настройки по умолчанию
            } catch (Exception e) {
                GLOBAL_LOGGER.warn("Пытались подцепиться к внешнему media driver, но поймали ошибку. " +
                    "Будем запускать встроенный", e);
            }
        }

        if (isNull(aeron_)) { // мы либо не пытались создать aeron до этого вообще, либо не смогли
            mediaDriver_ = MediaDriver.launchEmbedded(); // с чистой совестью запускаем встроенный медиа драйвер
            aeron_ = Aeron.connect(new Aeron.Context().aeronDirectoryName(mediaDriver_.aeronDirectoryName()));
        }

        var publisher_ = new MetricAlertPublisher(aeron_, publisherConfiguration);
        var metricsClient_ = new MetricsClient(configuration.getMetrics(), publisher_);
        var subscriber_ = new MetricsSubscriber(aeron_, metricsClient_, subscriberConfiguration);

        context = new AlertingNodeContext(mediaDriver_, aeron_, subscriber_, metricsClient_, publisher_);
    }

    public void start() {
        Thread.ofVirtual().start(publisher::start);
        subscriber.start();
        subscriber.run();
    }

    @Contract("->!null")
    public static AlertingNodeContext getInstance() {
        if (context == null) {
            throw new RuntimeException("Trying to access not created context");
        }

        return context;
    }

    public static Logger getLogger() {
        return GLOBAL_LOGGER;
    }

    private final MediaDriver mediaDriver;
    private final Aeron aeron;
    private final MetricsSubscriber subscriber;
    private final MetricAlertPublisher publisher;

    @SuppressWarnings("unused")
    final MetricsClient metricsClient;


    private AlertingNodeContext(MediaDriver mediaDriver, Aeron aeron, MetricsSubscriber subscriber,
                                MetricsClient metricsClient, MetricAlertPublisher publisher) {
        this.mediaDriver = mediaDriver;
        this.aeron = aeron;
        this.subscriber = subscriber;
        this.metricsClient = metricsClient;
        this.publisher = publisher;
    }

    public Aeron getAeron() {
        return aeron;
    }

    public boolean isRunning() {
        return subscriber.isRunning() && publisher.isRunning();
    }

    @Override
    public void close() {
        subscriber.close();
        publisher.close();
        if (nonNull(mediaDriver)) {
            mediaDriver.close();
        }
        aeron.close();
    }
}
