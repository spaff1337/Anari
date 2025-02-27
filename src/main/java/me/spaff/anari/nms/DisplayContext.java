package me.spaff.anari.nms;

public enum DisplayContext {
    NONE((byte)0),
    THIRD_PERSON_LEFT_HAND((byte)1),
    THIRD_PERSON_RIGHT_HAND((byte)2),
    FIRST_PERSON_LEFT_HAND((byte)3),
    FIRST_PERSON_RIGHT_HAND((byte)4),
    HEAD((byte)5),
    GUI((byte)6),
    GROUND((byte)7),
    FIXED((byte)8);

    private byte type;

    DisplayContext(byte type) {
        this.type = type;
    }

    public byte asByte() {
        return type;
    }
}
