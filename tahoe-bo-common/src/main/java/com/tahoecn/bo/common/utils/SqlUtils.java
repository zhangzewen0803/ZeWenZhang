package com.tahoecn.bo.common.utils;

/**
 * SQL 工具类
 */
public class SqlUtils {

    /**
     * 安全过滤 替换单引号，需要储存单引号的字段不可以用此方法
     * @param val
     * @return
     */
    public static String safeFilter(String val){
        return val.replace("'","");
    }
}
