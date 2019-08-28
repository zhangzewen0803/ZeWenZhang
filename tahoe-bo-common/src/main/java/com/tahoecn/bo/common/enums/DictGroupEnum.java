package com.tahoecn.bo.common.enums;

/**
 * 字典组CODE枚举
 * @author panglx
 */
public enum DictGroupEnum {
    PROPERTY_RIGHT("PROPERTY_RIGHT", "产权属性"),
    REFINED_DECORATION("REFINED_DECORATION", "精装属性"),
    FLOOR_HIGH("FLOOR_HIGH", "层高属性"),;

    private String key;

    private String value;

    DictGroupEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final DictGroupEnum getByKey(String key) {
        for (DictGroupEnum dictGroupEnum : values()) {
            if (dictGroupEnum.getKey().equals(key)) {
                return dictGroupEnum;
            }
        }
        return null;
    }
}
