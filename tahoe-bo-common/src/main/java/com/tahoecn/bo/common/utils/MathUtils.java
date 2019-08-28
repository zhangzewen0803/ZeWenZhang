package com.tahoecn.bo.common.utils;

import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;

import java.math.BigDecimal;

/**
 * 数学 工具类
 *
 * @author panglx
 * @date 2019/6/26
 */
public class MathUtils {

    private static final Log log = LogFactory.get();

    /**
     * 新 BigDecimal 对象，非数值字符串按0处理
     *
     * @param str
     * @return
     */
    public static BigDecimal newBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (Exception e) {
            return new BigDecimal("0");
        }
    }

    /**
     * 字符串数值相加，非数值字符串按0处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal bigDecimalAdd(String a, String b) {
        return newBigDecimal(a).add(newBigDecimal(b));
    }


    /**
     * 如果参数为null则返回值为0的BigDecimal对象
     *
     * @param bigDecimal
     * @return
     */
    public static BigDecimal nullDefaultZero(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

    /**
     * 是否为 0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isZero(BigDecimal bigDecimal) {
        return bigDecimal == null ? true : bigDecimal.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 是否不为 0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isNotZero(BigDecimal bigDecimal) {
        return !isZero(bigDecimal);
    }

    /**
     * 是否都不为 0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isNoneZero(BigDecimal... bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        for (BigDecimal n : bigDecimal) {
            if (isZero(n)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否存在任意一个不为 0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean existNotZero(BigDecimal... bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        for (BigDecimal n : bigDecimal) {
            if (isNotZero(n)) {
                return true;
            }
        }
        return false;
    }

    public static int compare(Integer a, Integer b) {
        if (a == null) {
            a = 0;
        }
        if (b == null) {
            b = 0;
        }
        return a - b;
    }
}
