package org.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConnectInfo {
    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_IPC = "aeron:ipc";
    private static final Logger LOG = LoggerFactory.getLogger(ConnectInfo.class);

    private String uri;
    private int streamId;
    private IdleStrategy idle;

    public ConnectInfo(String uri, int streamId, String idleString) {
        this.uri = uri;
        this.streamId = streamId;
        IdleStrategy idle;
        switch (idleString) {
            case ("SleepingIdleStrategy"):
                idle = new SleepingIdleStrategy();
                break;
            default:
                idle = new SleepingIdleStrategy();
                break;
        }
        this.idle = idle;
    }

    public ConnectInfo(String uri, int streamId) {
        this.uri = uri;
        this.streamId = streamId;
        this.idle = new SleepingIdleStrategy();
    }

    public String getUri() {
        return uri;
    }

    public int getStreamId() {
        return streamId;
    }

    public IdleStrategy getIdle() {
        return idle;
    }

    public static ConnectInfo readProducerFromFile(String configPath) throws IOException {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode producerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("producer");
            String idleString = producerYamlSection.get("idle-strategy").asText();
            String ip = producerYamlSection.get("ip").asText();
            String port = producerYamlSection.get("port").asText();
            String streamUri = String.format(AERON_UDP_FORMAT, ip, port);
//            String streamUri = AERON_IPC;
            int streamId = producerYamlSection.get("stream-id").asInt();
            return new ConnectInfo(streamUri, streamId, idleString);
        } catch (IOException e) {
            LOG.warn("Failed to open config file: {}", configPath);
            throw e;
        }
    }

    public static ArrayList<ConnectInfo> readConsumerFromFile(String configPath) throws IOException {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode producerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("consumer");
            String idleString = producerYamlSection.get("idle-strategy").asText();
            ArrayList<ConnectInfo> streams = new ArrayList<>();
            for (JsonNode node: producerYamlSection.get("streams")) {
                String uri = String.format(AERON_UDP_FORMAT, node.get("ip").asText(), node.get("port").intValue());
                streams.add(new ConnectInfo(uri, node.get("stream-id").intValue(), idleString));
            }
            return streams;
        } catch (IOException e) {
            LOG.warn("Failed to open config file: {}", configPath);
            throw e;
        }
    }
}
