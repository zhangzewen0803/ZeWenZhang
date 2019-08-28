package com.tahoecn.bo.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 版本工具类
 *
 * @author panglx
 * @date 2019/5/27
 */
public class VersionUtils {

    /**
     * 小版本递增
     *
     * @param version 版本号
     * @return 递增后的版本号
     */
    @Deprecated
    public static final String smallVersionInc(String version) {
        if (StringUtils.isBlank(version)) {
            return "V1.0";
        }
        if (!version.matches("V\\d\\.\\d")) {
            return null;
        }
        return "V" + new BigDecimal(version.substring(1))
                .add(new BigDecimal("0.1"))
                .toString();
    }

    /**
     * 大版本递增，如传入V1.0则，返回V2.0
     *
     * @param version 版本号
     * @return 返回新版本，若版本号为空则返回V1.0
     */
    public static final String bigVersionInc(String version) {
        if (StringUtils.isBlank(version)) {
            return "V1.0";
        }
        if (!version.matches("V\\d+\\.\\d")) {
            return null;
        }
        return "V" + (Integer.valueOf(StringUtils.substringBetween(version, "V", ".")) + 1) + ".0";
    }

//    public static void main(String[] args) {
//        System.out.println(smallVersionInc("V1.1"));
//        System.out.println(bigVersionInc("V1.1"));
//        System.out.println(bigVersionInc("V9.1"));
//        System.out.println(bigVersionInc("V11.1"));
//    }
}
