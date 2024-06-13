package ru.realalerting.alertlogic;

public abstract class AlertLogicBase {

    protected AlertInfo alertInfo;

    public AlertLogicBase(AlertInfo alertInfo) {
        this.alertInfo = alertInfo;
    }

    public abstract boolean calculateAlert(int metricId, long value, long timestamp);

    public AlertInfo getAlertInfo() {
        return alertInfo;
    }
}
