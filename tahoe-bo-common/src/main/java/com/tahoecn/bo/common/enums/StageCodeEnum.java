package com.tahoecn.bo.common.enums;

/**
 * 项目/分期阶段
 * 2019-6-11 20:46:49 去掉经营评审会，将启动会改为经营决策会
 *
 * @author panglx
 */
public enum StageCodeEnum {
    //    STAGE_01("STAGE_01", "投决会",1),
//    STAGE_02("STAGE_02", "经营评审会",2),
//    STAGE_03("STAGE_03", "启动会",3),
//    STAGE_04("STAGE_04", "政府方案",4),
//    STAGE_05("STAGE_05", "工规证",5);
    STAGE_01("STAGE_01", "投决会", 1),
    STAGE_02("STAGE_02", "经营决策会", 2),
    STAGE_03("STAGE_03", "政府方案复函", 3),
    STAGE_04("STAGE_04", "工规证", 4);

    private String key;

    private String value;

    private int order;

    StageCodeEnum(String key, String value, int order) {
        this.key = key;
        this.value = value;
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getOrder() {
        return order;
    }

    public static final StageCodeEnum getByKey(String key) {
        for (StageCodeEnum stageCodeEnum : values()) {
            if (stageCodeEnum.getKey().equals(key)) {
                return stageCodeEnum;
            }
        }
        return null;
    }
}
