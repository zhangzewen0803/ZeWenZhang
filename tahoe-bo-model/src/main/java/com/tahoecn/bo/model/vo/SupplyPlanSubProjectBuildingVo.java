package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：SupplyPlanProjectInfoVo
 * @Description：供货计划项目/分期信息Vo
 * @author liyongxu 
 * @date 2019年8月9日 上午9:54:19 
 * @version 1.0.0 
 */
@Data
public class SupplyPlanSubProjectBuildingVo{

	@ApiModelProperty(value="分期Id", name="subProjectId")
    private String subProjectId;

	@ApiModelProperty(value="分期名称", name="subProjectName")
    private String subProjectName;

	@ApiModelProperty(value="楼栋ID", name="buildingfId")
    private String buildingfId;

	@ApiModelProperty(value="楼栋名称", name="buildingfName")
    private String buildingfName;

}
