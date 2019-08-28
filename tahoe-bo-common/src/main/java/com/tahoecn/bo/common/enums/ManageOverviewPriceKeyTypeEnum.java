package com.tahoecn.bo.common.enums;


/**
 * 区分货值形态的KEY枚举，可完整表示，可前缀表示
 */
public enum  ManageOverviewPriceKeyTypeEnum {
    PROJECT("PROJECT_OBJ","项目"),
    PROJECT_SUB("PROJECT_SUB_OBJ","分期"),
    PRODUCT_TYPE("PRODUCT_TYPE_OBJ","业态"),
    LAND_PART("LAND_PART_OBJ","地块"),
    ;

    private String key;

    private String value;

    ManageOverviewPriceKeyTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
