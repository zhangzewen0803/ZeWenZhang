package com.tahoecn.bo.common.enums;

public enum IsDevEnum {

    YES(1,"是全部开发"),
    NO(0,"否全部开发"),
    ;

    private int key;

    private String value;

    IsDevEnum(int key, String value) {
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
