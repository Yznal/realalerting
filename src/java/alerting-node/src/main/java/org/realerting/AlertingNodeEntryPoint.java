package org.realerting;

import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.AlertingNodeContext;
import org.slf4j.Logger;


/**
 * @author Mikhail Shadrin
 */
public class AlertingNodeEntryPoint {
    private static final Logger log = AlertingNodeContext.getLogger();

    public static void main(String[] args) {
        log.info("AlertingNodeEntryPoint. Starting alerting node");
        var yamlConfigurationPath = args[0];
        AlertingNodeConfiguration.initialize(yamlConfigurationPath);
        AlertingNodeContext.initialize();

        try (AlertingNodeContext context = AlertingNodeContext.getInstance()) {
            context.start();
        } catch (Exception e) {
            log.error("AlertingNodeEntryPoint. Caught exception: ", e);
        } finally {
            log.info("AlertingNodeEntryPoint. Stopped alerting node");
        }
    }
}
