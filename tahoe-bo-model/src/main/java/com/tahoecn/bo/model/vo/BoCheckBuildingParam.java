package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 删除楼栋业态Vo
 * @since 2019-06-10
 */
@Data
@ApiModel(value="删除楼栋业态信息",description="删除楼栋业态信息")
public class BoCheckBuildingParam{

	@ApiModelProperty(value="楼栋Id", name="buildingId", required=true)
    private String buildingId;

	@ApiModelProperty(value="关系业态Id", name="productTypeId", required=true)
    private String productTypeId;
	
	@ApiModelProperty(value="删除楼栋业态标识(0是楼栋,1是业态)", name="type", required=true)
	private Integer type;

}
