package ru.realalerting.alertlogic;

public class LessAlert extends AlertLogicBase {

    public LessAlert(AlertInfo alertInfo) {
        super(alertInfo);
    }

    @Override
    public boolean calculateAlert(int metricId, long value, long timestamp) {
        return value < alertInfo.getThreshold();
    }
}
