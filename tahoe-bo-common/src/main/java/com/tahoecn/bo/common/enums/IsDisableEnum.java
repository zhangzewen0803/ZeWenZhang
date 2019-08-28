package com.tahoecn.bo.common.enums;

/**
 * 是否删除枚举
 */
public enum IsDisableEnum {
    YES(1,"已禁用"),
    NO(0,"未禁用"),
    ;

    private int key;

    private String value;

    IsDisableEnum(int key, String value) {
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
