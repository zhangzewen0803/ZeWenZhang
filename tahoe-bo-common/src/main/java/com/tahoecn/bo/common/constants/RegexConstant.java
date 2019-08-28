package com.tahoecn.bo.common.constants;

import java.util.regex.Pattern;

/**
 * @author panglx
 * @date 2019/6/28
 */
public class RegexConstant {

    /**
     * 某种楼栋名称正则
     */
    public static final String BUILDING_NAME_PATTERN_STRING = "^(\\d+)([#,栋,棟,幢,楼]|号楼)$";

    /**
     * 楼栋名称正则对象
     */
    public static final Pattern BUILDING_NAME_PATTERN = Pattern.compile(BUILDING_NAME_PATTERN_STRING);


}
