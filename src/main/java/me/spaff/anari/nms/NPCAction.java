package me.spaff.anari.nms;

public enum NPCAction {
    SWING_MAIN_HAND(0),
    WAKE_UP(2),
    SWING_OFF_HAND(3),
    CRITICAL_HIT(4),
    MAGIC_CRITICAL_HIT(5);

    private final int actionId;

    NPCAction(int actionId) {
        this.actionId = actionId;
    }

    public int getActionId() {
        return actionId;
    }
}