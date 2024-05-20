package ru.realalerting.alertlogic;

public class EqualAlert extends AlertLogicBase {

    public EqualAlert(AlertInfo alertInfo) {
        super(alertInfo);
    }

    @Override
    public boolean calculateAlert(int metricId, long value, long timestamp) {
        return value == alertInfo.getThreshold();
    }
}
