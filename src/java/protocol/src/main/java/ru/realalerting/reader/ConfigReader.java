package ru.realalerting.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.aeron.ChannelUriStringBuilder;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * @author Karbayev Saruar
 */
public class ConfigReader {
    private static final String AERON_UDP_FORMAT = "aeron:udp?endpoint=%s:%s";
    private static final String AERON_IPC = "aeron:ipc";
    private static final String AERON_MDC_PUBLICATION = "aeron:udp?control-mode=dynamic|control=%s:%s";
    private static final String AERON_MDC_SUBSCRIPTION = "aeron:udp?endpoint=%s:%s|control=%s:%s|control-mode=dynamic";

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
            String publicationType = producerYamlSection.get("publication-type").asText();
            String streamUri = switch (publicationType) {
                case "ipc" -> AERON_IPC;
                case "udp" -> {
                    String ip = producerYamlSection.get("ip").asText();
                    String port = producerYamlSection.get("port").asText();
                    yield String.format(AERON_UDP_FORMAT, ip, port);
                }
                case "mdc" -> {
                    String ip = producerYamlSection.get("ip").asText();
                    String port = producerYamlSection.get("port").asText();
                    yield String.format(AERON_MDC_PUBLICATION, ip, port);
                }
                default -> throw new IllegalStateException("Unexpected value: " + publicationType);
            };
            int streamId = producerYamlSection.get("stream-id").asInt();
            return new RealAlertingConfig(streamUri, streamId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RealAlertingConfig readSubscriberFromFile(String configPath) {
        final File fileYamlConfiguration = new File(configPath);
        if (!fileYamlConfiguration.exists()
                || !fileYamlConfiguration.isFile()
                || !fileYamlConfiguration.canRead()) {
            throw new RuntimeException("Can not access yaml configuration file by path: " + configPath);
        }
        var objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode consumerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("subscriber");
            JsonNode streams = consumerYamlSection.get("streams").get(0);
            String consumerType = streams.get("subscription-type").asText();
            String streamUri = switch (consumerType) {
                case "ipc" -> AERON_IPC;
                case "udp" -> {
                    String ip = streams.get("ip").asText();
                    String port = streams.get("port").asText();
                    yield String.format(AERON_UDP_FORMAT, ip, port);
                }
                case "mdc" -> {
                    String ip = streams.get("ip").asText();
                    String port = streams.get("port").asText();
                    String publicationIp = streams.get("publication-ip").asText();
                    String publicationPort = streams.get("publication-port").asText();
                    yield String.format(AERON_MDC_SUBSCRIPTION, ip, port, publicationIp, publicationPort);
                }
                default -> throw new IllegalStateException("Unexpected value: " + consumerType);
            };
            int streamId = streams.get("stream-id").asInt();
            return new RealAlertingConfig(streamUri, streamId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // https://github.com/real-logic/aeron-cookbook-code/blob/b77cb301731b29d239e18f2ae226f781b0c0b190/aeron-mdc/aeron-mdc-subscriber/src/main/java/com/aeroncookbook/aeron/mdc/MultiDestinationSubscriberAgent.java#L71
    private static String localHost(final String fallback)
    {
        try
        {
            final Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (interfaceEnumeration.hasMoreElements())
            {
                final var networkInterface = interfaceEnumeration.nextElement();

                if (networkInterface.getName().startsWith("eth0"))
                {
                    final Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
                    while (interfaceAddresses.hasMoreElements())
                    {
                        if (interfaceAddresses.nextElement() instanceof Inet4Address inet4Address)
                        {
                            System.out.println("detected ip4 address as " +  inet4Address.getHostAddress());
                            return inet4Address.getHostAddress();
                            // ServerRunning вернет 10.129.0.33
                            // можно проверить на VM ip запустив команду ip address
                        }
                    }
                }
            }
        }
        catch (final Exception e)
        {
            System.out.println("Failed to get address, using " + fallback);
        }
        return fallback;
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
            JsonNode consumerYamlSection = objectMapper.readTree(fileYamlConfiguration).get("subscriber");
            ArrayList<RealAlertingConfig> streams = new ArrayList<>();
            for (JsonNode node: consumerYamlSection.get("streams")) {
                String consumerType = node.get("subscription-type").asText();
                String streamUri= switch (consumerType) {
                    case "ipc" -> AERON_IPC;
                    case "udp" -> {
                        String ip = node.get("ip").asText();
                        String port = node.get("port").asText();
                        yield String.format(AERON_UDP_FORMAT, ip, port);
                    }
                    case "mdc" -> {
                        String ip = node.get("ip").asText();
                        String port = node.get("port").asText();
                        String publicationIp = node.get("publication-ip").asText();
                        String publicationPort = node.get("publication-port").asText();
                        yield String.format(AERON_MDC_SUBSCRIPTION, ip, port, publicationIp, publicationPort);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + consumerType);
                };
                streams.add(new RealAlertingConfig(streamUri, node.get("stream-id").intValue()));
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
