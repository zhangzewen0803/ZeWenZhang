package com.tahoecn.bo.common.enums;

public enum PowerSpotEnum {

    Management_Overview("bo_mnu_1","经营概览"),
    Project_Index("bo_mnu_2_1","项目指标"),
    Project_Sub_Index("bo_mnu_2_2","分期指标"),
    Area_Manage("bo_mnu_3_1","面积管理"),
    Price_Manage("bo_mnu_3_2","价格管理"),
    ;

    private String key;

    private String value;

    PowerSpotEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final PowerSpotEnum getByKey(String key) {
        for (PowerSpotEnum powerSpotEnum : values()) {
            if (powerSpotEnum.getKey().equals(key)) {
                return powerSpotEnum;
            }
        }
        return null;
    }



}
