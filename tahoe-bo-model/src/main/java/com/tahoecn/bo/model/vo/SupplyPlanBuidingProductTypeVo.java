package com.tahoecn.bo.model.vo;

import java.util.List;

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
public class SupplyPlanBuidingProductTypeVo{

	@ApiModelProperty(value="阶段名称", name="stageCode")
    private String stageCode;
	
	@ApiModelProperty(value="分期Id", name="subProjectId")
    private String subProjectId;

	@ApiModelProperty(value="分期名称", name="subProjectName")
    private String subProjectName;
	
	@ApiModelProperty(value="供货计划年份信息List", name="supplyPlanYearInfoVo")
    private List<SupplyPlanYearInfoVo> supplyPlanYearInfoVo;
	
	@ApiModelProperty(value="供货计划分期楼栋List", name="supplyPlanLandBuildingVo")
    private List<SupplyPlanSubProjectBuildingVo> supplyPlanLandBuildingVo;

	@ApiModelProperty(value="供货计划业态关系List", name="supplyPlanBuildingProductTypeMapVo")
    private List<SupplyPlanBuildingProductTypeMapVo> supplyPlanBuildingProductTypeMapVo;

}
