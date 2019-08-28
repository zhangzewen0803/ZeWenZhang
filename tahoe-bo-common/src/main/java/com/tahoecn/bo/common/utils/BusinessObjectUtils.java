package com.tahoecn.bo.common.utils;

import com.tahoecn.bo.common.enums.QuotaValueTypeEnum;
import com.tahoecn.bo.model.entity.BoQuotaGroupMap;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 业务对象工具类
 *
 * @author panglx
 * @date 2019/6/15
 */
public class BusinessObjectUtils {

    private static final String SPACE = "";

    private static final String ZERO = "0";


    /**
     * 在BoQuotaGroupMap列表中根据code查找指定的对象
     *
     * @param list
     * @param code
     * @return
     */
    public static BoQuotaGroupMap getBoQuotaGroupMapByCode(List<BoQuotaGroupMap> list, String code) {
        for (BoQuotaGroupMap boQuotaGroupMap : list) {
            if (boQuotaGroupMap.getCode().equals(code)) {
                return boQuotaGroupMap;
            }
        }
        return null;
    }


    /**
     * 在BoQuotaGroupMap列表中根据code枚举查找指定的对象
     *
     * @param list  具有指标特性的数据列表
     * @param code  指标CODE
     * @param clazz 返回的对象类型
     * @return
     */
    public static <T> T getByCode(List list, String code, Class<T> clazz) {
        try {
            if (list == null || code == null) {
                return null;
            }
            Field codeField;
            codeField = clazz.getDeclaredField("quotaCode");
            codeField.setAccessible(true);
            for (Object obj : list) {
                String tmpCode = (String) codeField.get(obj);
                if (code.equals(tmpCode)) {
                    return (T) obj;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 指标值赋予默认值
     *
     * @param obj
     */
    public static void quotaValueSetDefaultForObject(String valueType, Object obj) {
        if (obj == null) {
            return;
        }
        try {
            Field quotaValueField = obj.getClass().getDeclaredField("quotaValue");
            quotaValueField.setAccessible(true);
            Object quotaValue = quotaValueField.get(obj);
            if (quotaValue != null) {
                return;
            }
            if (valueType == null) {
                Field valueTypeField = obj.getClass().getDeclaredField("valueType");
                valueTypeField.setAccessible(true);
                valueType = (String) valueTypeField.get(obj);
            }
            if (valueType == null) {
                quotaValueField.set(obj, SPACE);
                return;
            }
            if (QuotaValueTypeEnum.NUMBER.getKey().equals(valueType)) {
                quotaValueField.set(obj, ZERO);
            } else {
                quotaValueField.set(obj, SPACE);
            }
        } catch (Exception e) {
            return;
        }
    }



}
