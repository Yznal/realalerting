package org.realerting.config;

import io.smallrye.config.ConfigMapping;
import org.realerting.dto.Metric;

import java.util.List;

@ConfigMapping(prefix = "alerting-node")
public interface AlertingNodeConfiguration {

    AeronConnection subscriber();

    AeronConnection publisher();

    List<Metric> metrics();

    public interface AeronConnection {
        String ip();

        int port();

        int streamId();
    }


}
