package ru.realalerting.alertlogic;

public class GreaterAlert extends AlertLogicBase {

    public GreaterAlert(AlertInfo alertInfo) {
        super(alertInfo);
    }

    @Override
    public boolean calculateAlert(int metricId, long value, long timestamp) {
        return value > alertInfo.getThreshold();
    }
}
