package com.tahoecn.bo.common.enums;

/**
 * SALE-房间状态
 */
public enum SaleRoomStreamStatusEnum {
    SIGNING("签约", "签约"),
    PRE_BUY("认购", "认购"),
    UN_SALE("未售", "未售"),
    UN_OPEN("未开盘", "未开盘"),
;
    private String key;

    private String value;

    SaleRoomStreamStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final SaleRoomStreamStatusEnum getByKey(String key) {
        for (SaleRoomStreamStatusEnum stageCodeEnum : values()) {
            if (stageCodeEnum.getKey().equals(key)) {
                return stageCodeEnum;
            }
        }
        return null;
    }
    }
