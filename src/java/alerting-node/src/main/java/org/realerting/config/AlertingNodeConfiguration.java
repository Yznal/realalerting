package org.realerting.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.realerting.dto.Metric;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Mikhail Shadrin
 */
public class AlertingNodeConfiguration {
    private static AlertingNodeConfiguration configuration = null;

    public static AlertingNodeConfiguration getInstance() {
        if (configuration == null) {
            throw new RuntimeException("Trying to access not created configuration");
        }

        return configuration;
    }

    public static void initialize(String yamlConfigurationPath) {
        if (configuration != null) {
            throw new RuntimeException("Trying to rewrite existing configuration");
        }

        if (!yamlConfigurationPath.startsWith("./")) {
            yamlConfigurationPath = "./" + yamlConfigurationPath;
        }

        final File fileYamlConfiguration = new File(yamlConfigurationPath);
        if (!fileYamlConfiguration.exists()
            || !fileYamlConfiguration.isFile()
            || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + yamlConfigurationPath);
        }

        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            var alertingNodeYamlSection = objectMapper.readTree(fileYamlConfiguration).get("alerting-node");
            configuration = objectMapper.treeToValue(alertingNodeYamlSection, AlertingNodeConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonProperty("subscriber")
    private final AeronConnectionConfiguration subscriberConfiguration;
    @JsonProperty("publisher")
    private final AeronConnectionConfiguration publisherConfiguration;
    @JsonProperty
    private final List<Metric> metrics;

    private AlertingNodeConfiguration() {
        this(null, null, null);
    }

    private AlertingNodeConfiguration(AeronConnectionConfiguration subscriberConfiguration, AeronConnectionConfiguration publisherConfiguration, List<Metric> metrics) {
        this.subscriberConfiguration = subscriberConfiguration;
        this.publisherConfiguration = publisherConfiguration;
        this.metrics = metrics;
    }

    public AeronConnectionConfiguration getSubscriberConfiguration() {
        return subscriberConfiguration;
    }

    public AeronConnectionConfiguration getPublisherConfiguration() {
        return publisherConfiguration;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public static class AeronConnectionConfiguration {
        @JsonProperty
        private final String ip;
        @JsonProperty
        private final int port;
        @JsonProperty("stream-id")
        private final int streamId;

        private AeronConnectionConfiguration() {
            this(null, 0, 0);
        }

        private AeronConnectionConfiguration(String ip, int port, int streamId) {
            this.ip = ip;
            this.port = port;
            this.streamId = streamId;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public int getStreamId() {
            return streamId;
        }
    }

}
