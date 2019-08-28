package com.tahoecn.bo.common.enums;

/**
 * 是否虚拟房间
 */
public enum IsVirtualRoomEnum {
    YES(1,"是"),
    NO(0,"否"),
    ;

    private int key;

    private String value;

    IsVirtualRoomEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
