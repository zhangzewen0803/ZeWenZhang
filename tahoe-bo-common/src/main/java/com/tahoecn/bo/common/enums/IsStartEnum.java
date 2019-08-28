package com.tahoecn.bo.common.enums;

/**
 * 是否开工枚举
 */
public enum IsStartEnum {
    YES(1,"已开工"),
    NO(0,"未开工"),
    ;

    private int key;

    private String value;

    IsStartEnum(int key, String value) {
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
