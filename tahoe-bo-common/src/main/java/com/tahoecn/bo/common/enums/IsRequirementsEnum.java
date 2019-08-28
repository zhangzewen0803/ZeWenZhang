package com.tahoecn.bo.common.enums;

/**
 * 是否达形象进度
 */
public enum IsRequirementsEnum {
    YES(1,"已达"),
    NO(0,"未达"),
    ;

    private int key;

    private String value;

    IsRequirementsEnum(int key, String value) {
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
