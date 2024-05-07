package ru.realalerting.alertnode;

import ru.realalerting.alertlogic.AlertLogicBase;
import ru.realalerting.producer.AlertProducer;

public class AlertNode {
    private final AlertLogicBase alertLogic;
    private final AlertProducer alertProducer;

    public AlertNode(AlertLogicBase alertLogic, AlertProducer alertProducer) {
        this.alertLogic = alertLogic;
        this.alertProducer = alertProducer;
    }

    public boolean sendAlert(int metricId, long value, long timestamp) {
        return alertProducer.sendAlert(alertLogic, metricId, value, timestamp);
    }

    public void start() throws Exception {
        alertProducer.start();
    }

    public boolean isRunning() {
        return alertProducer.isRunning();
    }

}
