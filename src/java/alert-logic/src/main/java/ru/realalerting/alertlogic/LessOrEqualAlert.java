package ru.realalerting.alertlogic;

public class LessOrEqualAlert extends AlertLogicBase {

    public LessOrEqualAlert(AlertInfo alertInfo) {
        super(alertInfo);
    }

    @Override
    public boolean calculateAlert(int metricId, long value, long timestamp) {
        return value <= alertInfo.getThreshold();
    }
}
