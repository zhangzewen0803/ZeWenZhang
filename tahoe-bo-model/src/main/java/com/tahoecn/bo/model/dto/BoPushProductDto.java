package com.tahoecn.bo.model.dto;

import lombok.Data;

/**
 * 业态Dto
 * @since 2019-06-10
 */
@Data
public class BoPushProductDto{

    /**
     * 业态CODE
     */
    private String productTypeCode;

    /**
     * 业态名称
     */
    private String productTypeName;

	@Override
	public String toString() {
		return "{\"productTypeCode\":\"" + productTypeCode + "\" , \"productTypeName\":\""+ productTypeName + "\"}";
	}
    
}
