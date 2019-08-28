package com.tahoecn.bo.common.enums;

/**
 * 字典项枚举
 *
 * @author panglx
 */
public enum DictItemEnum {
    HAVE_PROPERTY_RIGHT("1001", "有产权", 10000),
    NOT_HAVE_PROPERTY_RIGHT("1002", "无产权", 20000),
    REFINE("2001", "精装", 1000),
    NOT_REFINE("2002", "毛坯", 2000),
    FLAT_FLOOR("FLAT_FLOOR", "平层", 100),
    SKIP_FLOOR("SKIP_FLOOR", "跃层", 200),
    LOFT("LOFT", "跃层", 300),;

    private String key;

    private String value;

    private Integer sortNo;

    DictItemEnum(String key, String value, Integer sortNo) {
        this.key = key;
        this.value = value;
        this.sortNo = sortNo;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final DictItemEnum getByKey(String key) {
        for (DictItemEnum dictGroupEnum : values()) {
            if (dictGroupEnum.getKey().equals(key)) {
                return dictGroupEnum;
            }
        }
        return null;
    }
}
