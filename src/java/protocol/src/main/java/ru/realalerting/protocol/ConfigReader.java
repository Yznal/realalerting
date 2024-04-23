package ru.realalerting.protocol;

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
            return new ConnectInfo(streamUri, streamId, isIpc);
        } catch (IOException e) {
            throw e;
        }
    }

    public static ConnectInfo readConsumerFromFile(String configPath) throws IOException {
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
            return new ConnectInfo(streamUri, streamId, isIpc);
        } catch (IOException e) {
            throw e;
        }
    }

    public static ArrayList<ConnectInfo> readManyConsumerFromFile(String configPath) throws IOException {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode consumerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("consumer");
            ArrayList<ConnectInfo> streams = new ArrayList<>();
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
                streams.add(new ConnectInfo(streamUri, node.get("stream-id").intValue(), isIpc));
            }
            return streams;
        } catch (IOException e) {
            throw e;
        }
    }
}
