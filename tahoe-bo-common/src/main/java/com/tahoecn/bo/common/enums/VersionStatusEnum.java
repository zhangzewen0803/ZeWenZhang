package com.tahoecn.bo.common.enums;

/**
 * 版本状态枚举
 *
 * @author panglx
 * @date 2019/5/27
 */
public enum VersionStatusEnum {
    //未编制，仅前端判断阶段状态使用
    NONE(-1, "未编制"),
    CREATING(0, "编制中"),
    CHECKING(1, "审批中"),
    PASSED(2, "审批通过"),
    REJECTED(3, "审批驳回"),
    DROPPED(4, "已废弃"),
    DROP_REJECTED(5, "废弃已驳回");;

    private int key;

    private String value;

    VersionStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static VersionStatusEnum getByKey(int key) {
        for (VersionStatusEnum versionStatusEnum : values()) {
            if (versionStatusEnum.getKey() == key) {
                return versionStatusEnum;
            }
        }
        return null;
    }

    public static String getValueByKey(int key) {
        for (VersionStatusEnum versionStatusEnum : values()) {
            if (versionStatusEnum.getKey() == key) {
                return versionStatusEnum.getValue();
            }
        }
        return null;
    }
}
