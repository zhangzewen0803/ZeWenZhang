package com.tahoecn.bo.common.enums;

/**
 * 业态枚举
 *
 * @author panglx
 */
public enum ProductTypeEnum {
    HOUSE("1001", "住宅"),
    POLICY_HOUSE("1001002", "政策房"),
    SALE_BUSINESS("1003002001001", "销售商业"),
    COMPLETE("1004", "配套"),
    CHANGE_FLOOR("1004005", "架空层\\避难层\\转换层"),
    CAT_PARKING_PLATE("1002", "车位"),
    LITTLE_HIGH_FLOOR("1001001003001", "小高层"),;

    private String key;

    private String value;

    ProductTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final ProductTypeEnum getByKey(String key) {
        for (ProductTypeEnum dictGroupEnum : values()) {
            if (dictGroupEnum.getKey().equals(key)) {
                return dictGroupEnum;
            }
        }
        return null;
    }

    public static final boolean isCarParkingPlate(String code){
            return code.startsWith(CAT_PARKING_PLATE.getKey());
    }
}
