//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tahoecn.bo.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tahoecn.core.util.StrUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonUtil() {
    }

    public static <T> T convertJsonToBean(String jsonString, Class<T> beanType) {
        if(StrUtil.isNotBlank(jsonString) && beanType != null) {
            try {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper.readValue(jsonString, beanType);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        return null;
    }

    public static Map<String, Object> convertJsonToMap(String jsonString) {
        if(StrUtil.isNotBlank(jsonString)) {
            try {
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return (Map)mapper.readValue(jsonString, Map.class);
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    public static String convertObjectToJson(Object bean) {
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Collection<?> convertJsonToList(String jsonString, Class<?> collectionClass, Class... elementClasses) {
        try {
            JavaType e = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            return (Collection)mapper.readValue(jsonString, e);
        } catch (JsonParseException var4) {
            var4.printStackTrace();
        } catch (JsonMappingException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return null;
    }
}
