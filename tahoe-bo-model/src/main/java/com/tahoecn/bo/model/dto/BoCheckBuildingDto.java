package com.tahoecn.bo.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 楼栋Dto
 * @since 2019-06-10
 */
@Data
@ApiModel(value="检查楼栋业态信息",description="检查楼栋业态信息")
public class BoCheckBuildingDto{

	@ApiModelProperty(value="楼栋ID", name="buildingId", required=true)
    private String buildingId;

	@ApiModelProperty(value="业态CODE", name="productTypeCode", required=true)
    private String productTypeCode;

	@Override
	public String toString() {
		return "{\"buildingId\":\"" + buildingId + "\", \"productTypeCode\":\"" + productTypeCode + "\" }";
	}
    
}
