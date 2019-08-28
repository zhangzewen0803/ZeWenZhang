package com.tahoecn.bo.common.enums;

/**
 * 是否删除枚举
 */
public enum IsDeleteEnum {
    YES(1,"已删除"),
    NO(0,"未删除"),
    ;

    private int key;

    private String value;

    IsDeleteEnum(int key, String value) {
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
