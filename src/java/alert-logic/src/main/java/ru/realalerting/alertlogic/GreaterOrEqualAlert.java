package ru.realalerting.alertlogic;

public class GreaterOrEqualAlert extends AlertLogicBase {

    public GreaterOrEqualAlert(AlertInfo alertInfo) {
        super(alertInfo);
    }

    @Override
    public boolean calculateAlert(int metricId, long value, long timestamp) {
        return value >= alertInfo.getThreshold();
    }
}
