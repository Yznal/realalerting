package org.realerting.config;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import org.jetbrains.annotations.Contract;
import org.realerting.service.MetricAlertPublisher;
import org.realerting.service.MetricsClient;
import org.realerting.service.MetricsSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Mikhail Shadrin
 */
public class AlertingNodeContext implements AutoCloseable {
    private static AlertingNodeContext context = null;
    private static final Logger GLOBAL_LOGGER = LoggerFactory.getLogger("AlertingNode");

    public static void initialize() {
        var configuration = AlertingNodeConfiguration.getInstance();
        var mediaDriver_ = MediaDriver.launchEmbedded();
        var aeron_ = Aeron.connect(new Aeron.Context().aeronDirectoryName(mediaDriver_.aeronDirectoryName()));
        var publisher_ = new MetricAlertPublisher(aeron_, configuration.getPublisherConfiguration());
        var metricsClient_ = new MetricsClient(configuration.getMetrics(), publisher_);
        var subscriber_ = new MetricsSubscriber(aeron_, metricsClient_, configuration.getSubscriberConfiguration());

        context = new AlertingNodeContext(mediaDriver_, aeron_, subscriber_, metricsClient_, publisher_);
    }

    public void start() {
        subscriber.start();
        publisher.start();
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

    private final MetricsClient metricsClient;


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
        mediaDriver.close();
        aeron.close();
    }
}
