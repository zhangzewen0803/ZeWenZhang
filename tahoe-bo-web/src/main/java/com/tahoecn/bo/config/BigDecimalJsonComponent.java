package com.tahoecn.bo.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal 序列化配置
 * @author panglx
 */
@JsonComponent
public class BigDecimalJsonComponent {

    public static class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal decimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (decimal!=null) {
                int scale = decimal.scale();
                if (scale == 0){
                    jsonGenerator.writeString(decimal.setScale(2, RoundingMode.HALF_UP).toString());
                }else {
                    jsonGenerator.writeString(decimal.toString());
                }
            }
        }
    }
}