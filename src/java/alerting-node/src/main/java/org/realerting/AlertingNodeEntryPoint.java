package org.realerting;

import org.realerting.config.AlertingNodeConfiguration;
import org.realerting.config.AlertingNodeContext;
import org.slf4j.Logger;

import java.util.Scanner;


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

        AlertingNodeContext context = AlertingNodeContext.getInstance();
        try {
            context.start();

            var scanner = new Scanner(System.in);
            var input = scanner.next();
            while (!input.equals("quit")) {
                input = scanner.next();
            }

            log.info("AlertingNodeEntryPoint. Finished work");
        } catch (Exception e) {
            log.error("AlertingNodeEntryPoint. Caught exception: ", e);
        } finally {
            context.close();
        }
    }
}
