package com.tahoecn.bo.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 检查楼栋业态信息
 * @since 2019-06-10
 */
@Data
@ApiModel(value="检查楼栋业态信息响应数据",description="检查楼栋业态信息响应数据")
public class BoCheckBuildingResultDto{

	@ApiModelProperty(value="楼栋ID", name="buildingId")
    private String buildingId;
    
	@ApiModelProperty(value="业态CODE", name="productTypeCode")
    private String productTypeCode;
    
	@ApiModelProperty(value="能否删除1能删", name="isDelete")
    private String  isDelete;

}
