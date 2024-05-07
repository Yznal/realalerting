package ru.realalerting.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author Karbayev Saruar
 */
public class ConfigReader {
    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_IPC = "aeron:ipc";

    public static RealAlertingConfig readProducerFromFile(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode producerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("producer");
            String streamUri;
            boolean isIpc = producerYamlSection.get("is-ipc").asBoolean();
            if (isIpc) {
                streamUri = AERON_IPC;
            } else {
                String ip = producerYamlSection.get("ip").asText();
                String port = producerYamlSection.get("port").asText();
                streamUri = String.format(AERON_UDP_FORMAT, ip, port);
            }
            int streamId = producerYamlSection.get("stream-id").asInt();
            return new RealAlertingConfig(streamUri, streamId, isIpc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RealAlertingConfig readConsumerFromFile(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode consumerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("consumer");
            JsonNode streams = consumerYamlSection.get("streams").get(0);
            boolean isIpc = streams.get("is-ipc").asBoolean();
            String streamUri;
            if (isIpc) {
                streamUri = AERON_IPC;
            } else {
                String ip = streams.get("ip").asText();
                String port = streams.get("port").asText();
                streamUri = String.format(AERON_UDP_FORMAT, ip, port);
            }
            int streamId = streams.get("stream-id").asInt();
            return new RealAlertingConfig(streamUri, streamId, isIpc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<RealAlertingConfig> readManyConsumerFromFile(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode consumerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("consumer");
            ArrayList<RealAlertingConfig> streams = new ArrayList<>();
            for (JsonNode node: consumerYamlSection.get("streams")) {
                boolean isIpc = node.get("is-ipc").asBoolean();
                String streamUri;
                if (isIpc) {
                    streamUri = AERON_IPC;
                } else {
                    String ip = node.get("ip").asText();
                    String port = node.get("port").asText();
                    streamUri = String.format(AERON_UDP_FORMAT, ip, port);
                }
                streams.add(new RealAlertingConfig(streamUri, node.get("stream-id").intValue(), isIpc));
            }
            return streams;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MetricConsumerConfig readMetricSubcriberConfig(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode metricSubscriberYamlSection = objectMapper.readTree(fileYamlConfiguration).get("info");
            int retryCount = metricSubscriberYamlSection.get("retry-count").asInt();
            int maxFetchBytes = metricSubscriberYamlSection.get("max-fetch-bytes").asInt();
            MetricConsumerConfig config = new MetricConsumerConfig(retryCount, maxFetchBytes);
            return config;
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }
}
