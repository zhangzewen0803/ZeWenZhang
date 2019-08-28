package com.tahoecn.bo.common.enums;

/**
 * 项目/分期阶段
 */
public enum LevelTypeEnum {
    PROJECT("PROJECT", "项目"),
    PROJECT_SUB("PROJECT_SUB", "分期");

    private String key;

    private String value;

    LevelTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final LevelTypeEnum getByKey(String key) {
        for (LevelTypeEnum stageCodeEnum : values()) {
            if (stageCodeEnum.getKey().equals(key)) {
                return stageCodeEnum;
            }
        }
        return null;
    }
    }
