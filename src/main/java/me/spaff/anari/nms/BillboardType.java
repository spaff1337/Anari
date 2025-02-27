package me.spaff.anari.nms;

public enum BillboardType {
    FIXED((byte)0),
    VERTICAL((byte)1),
    HORIZONTAL((byte)2),
    CENTER((byte)3);

    private byte type;

    BillboardType(byte type) {
        this.type = type;
    }

    public byte asByte() {
        return type;
    }
}