package com.tahoecn.bo.common.enums;

/**
 * 更新楼栋业态指标Mini版使用，更改基准
 *
 * @author panglx
 */
public enum UpdateBuildingBaseEnum {
    BUILDING("BUILDING", "楼栋"),
    PRODUCT_TYPE("PRODUCT_TYPE", "业态"),
    ;

    private String key;

    private String value;

    UpdateBuildingBaseEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static UpdateBuildingBaseEnum getByKey(String key) {
        for (UpdateBuildingBaseEnum quotaValueTypeEnum : values()) {
            if (quotaValueTypeEnum.getKey().equals(key)) {
                return quotaValueTypeEnum;
            }
        }
        return null;
    }
}
