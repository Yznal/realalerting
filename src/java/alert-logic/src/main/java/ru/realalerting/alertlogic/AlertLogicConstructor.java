package ru.realalerting.alertlogic;

public class AlertLogicConstructor {
    public static AlertLogicBase createAlertLogicBase(int alertLogicId, AlertInfo alertInfo) {
        return switch (alertLogicId) {
            case AlertLogicIds.GREATER_ALERT_ID -> {
                yield new GreaterAlert(alertInfo);
            }
            case AlertLogicIds.GREATER_OR_EQUAL_ALERT_ID -> {
                yield new GreaterOrEqualAlert(alertInfo);
            }
            case AlertLogicIds.LESS_ALERT_ID -> {
                yield new LessAlert(alertInfo);
            }
            case AlertLogicIds.LESS_OR_EQUAL_ALERT_ID -> {
                yield new LessOrEqualAlert(alertInfo);
            }
            case AlertLogicIds.EQUAL_ALERT_ID -> {
                yield new EqualAlert(alertInfo);
            }
            default -> throw new IllegalStateException("Invalid alertLogic id: " + alertLogicId);
        };
    }
}
