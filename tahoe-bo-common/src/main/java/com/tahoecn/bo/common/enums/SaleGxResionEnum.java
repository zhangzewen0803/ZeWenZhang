package com.tahoecn.bo.common.enums;

/**
 * SALE-更新原因枚举
 */
public enum SaleGxResionEnum {
    IMPORT_ROOM("导入房源信息", "导入房源信息"),
    IMPORT_PRE_SALE_AREA("导入预售面积信息", "导入预售面积信息"),
    IMPORT_REAL_AREA("导入实测面积信息", "导入实测面积信息"),
    PRICE_FIRST("首次定价", "首次定价"),
    CHANGE_PRICE_BOTTOM("调底价", "调底价"),
    CHANGE_PRICE_TABLE("调表价", "调表价"),
    RETURN_ROOM("退房", "退房"),
    CHANGE_ROOM("换房", "换房"),
    CHANGE_OTHER("其他变更", "其他变更"),
    SPECIAL_DISCOUNT("特批折扣", "特批折扣"),
    SIGNING("签约", "签约"),
;
    private String key;

    private String value;

    SaleGxResionEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final SaleGxResionEnum getByKey(String key) {
        for (SaleGxResionEnum stageCodeEnum : values()) {
            if (stageCodeEnum.getKey().equals(key)) {
                return stageCodeEnum;
            }
        }
        return null;
    }
    }
