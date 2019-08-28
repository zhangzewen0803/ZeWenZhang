package com.tahoecn.bo.common.enums;

/**
 * 审批关联表枚举
 * @author panglx
 */
@Deprecated
public enum ApproveRefTableEnum {
    BO_PROJECT_QUOTA_EXTEND("BO_PROJECT_QUOTA_EXTEND","项目分期指标信息扩展表"),
    BO_PROJECT_EXTEND("BO_PROJECT_EXTEND","项目分期基础信息扩展表"),
    BO_PROJECT_PRICE_EXTEND("BO_PROJECT_PRICE_EXTEND","项目分期价格信息扩展表"),
    ;

    private String key;

    private String value;

    ApproveRefTableEnum(String key, String value) {
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
