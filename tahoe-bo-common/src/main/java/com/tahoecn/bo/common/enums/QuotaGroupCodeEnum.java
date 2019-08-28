package com.tahoecn.bo.common.enums;

/**
 * 指标分组CODE枚举
 *
 * @author panglx
 */
public enum QuotaGroupCodeEnum {
    PROJECT_AREA_PLAN("PROJECT_AREA_PLAN", "项目面积规划指标"),
    PROJECT_SUB_AREA_PLAN("PROJECT_SUB_AREA_PLAN", "分期面积规划指标"),
    LAND_PART_PRODUCT_TYPE("LAND_PART_PRODUCT_TYPE", "地块业态指标"),
    BUILDING_PRODUCT_TYPE("BUILDING_PRODUCT_TYPE", "楼栋业态指标"),
    BUILDING("BUILDING", "楼栋指标"),
    PROJECT_PRICE("PROJECT_PRICE", "项目/分期价格指标"),;

    private String key;

    private String value;

    QuotaGroupCodeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static QuotaGroupCodeEnum getByKey(String key){
        for (QuotaGroupCodeEnum quotaGroupCodeEnum:values()){
            if (quotaGroupCodeEnum.getKey().equals(key)){
                return quotaGroupCodeEnum;
            }
        }
        return null;
    }

}
