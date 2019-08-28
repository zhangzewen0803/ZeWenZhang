package com.tahoecn.bo.common.enums;

/**
 * 组织机构枚举
 * <p>
 * 以_开头的对象为内部自定义，方便处理相似于组织机构的逻辑，在组织机构中不真实存在
 *
 * @author panglx
 * @since 2018年03月19日 11:10
 **/
public enum OrgTypeCodeEnum {
    PLATE("ORG2", "板块"),
    AREA("ORG3", "区域"),
    CITY("ORG4", "城市"),
    UC_LAND_VIRTUAL_DIR("ORG4-1", "地产项目虚拟文件夹"),
    UC_LAND_PROJECT("ORG5-1", "地产项目"),
    UC_LAND_PROJECT_SUB("ORG5-2", "地产分期"),
    PRODUCT_TYPE("_PRODUCT_TYPE", "业态"),//真实组织机构不存在
    LAND_PART("_LAND_PART", "地块"),//真实组织机构不存在
    ;

    OrgTypeCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;

    private String name;

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
