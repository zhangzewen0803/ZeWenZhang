package com.tahoecn.bo.common.enums;

/**
 * 经营概览 版本类型枚举
 *
 * @author panglx
 */
public enum ManageOverviewVersionTypeEnum {
    PRODUCT_PRICE("PRODUCT_PRICE", "货值"),
    AREA("AREA", "面积"),
    ;

    private String key;

    private String value;

    ManageOverviewVersionTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final ManageOverviewVersionTypeEnum getByKey(String key) {
        for (ManageOverviewVersionTypeEnum dictGroupEnum : values()) {
            if (dictGroupEnum.getKey().equals(key)) {
                return dictGroupEnum;
            }
        }
        return null;
    }
}
