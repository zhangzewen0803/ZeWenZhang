package com.tahoecn.bo.common.enums;

public enum BasicParamEnum {

    OBTAIN_STATUS_CODE_N("obtainStatusN", "拟获取"),
    OBTAIN_STATUS_CODE_G("obtainStatusG", "已获取"),


    TRADE_MODE_CODE_M("tradeModeCodeM", "我方操盘"),
    TRADE_MODE_CODE_D("tradeModeCodeD", "对方操盘"),
    TRADE_MODE_CODE_MD("tradeModeCodeMD", "我方管理对方操盘"),

    MERGE_TABLE_TYPE_CODE_T("mergeTableTypeCodeT", "并表"),
    MERGE_TABLE_TYPE_CODE_F("mergeTableTypeCodeF", "非并表"),

    TAX_TYPE_CODE_Y("taxTypeCodeY","一般征收"),
    TAX_TYPE_CODE_E("taxTypeCodeE","简单征收"),

    ;

    private String key;

    private String value;

    BasicParamEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final BasicParamEnum getByKey(String key) {
        for (BasicParamEnum basicParamEnum : values()) {
            if (basicParamEnum.getKey().equals(key)) {
                return basicParamEnum;
            }
        }
        return null;
    }

}
