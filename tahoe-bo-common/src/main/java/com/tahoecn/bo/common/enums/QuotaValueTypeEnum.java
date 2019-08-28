package com.tahoecn.bo.common.enums;

/**
 * 指标分组CODE枚举
 *
 * @author panglx
 */
public enum QuotaValueTypeEnum {
    NUMBER("NUMBER", "数值"),
    TEXT("TEXT", "文本"),
    DATE("DATE", "日期"),
    GROUP("GROUP", "取字典数据"),
    ;

    private String key;

    private String value;

    QuotaValueTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static QuotaValueTypeEnum getByKey(String key) {
        for (QuotaValueTypeEnum quotaValueTypeEnum : values()) {
            if (quotaValueTypeEnum.getKey().equals(key)) {
                return quotaValueTypeEnum;
            }
        }
        return null;
    }
}
