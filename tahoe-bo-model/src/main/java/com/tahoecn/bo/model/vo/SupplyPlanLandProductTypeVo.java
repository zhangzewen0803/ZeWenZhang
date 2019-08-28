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
public class SupplyPlanLandProductTypeVo{

	@ApiModelProperty(value="项目Id", name="projectId")
	private String projectId;
	
	@ApiModelProperty(value="项目名称", name="projectName")
    private String projectName;

	@ApiModelProperty(value="供货计划年份信息List", name="supplyPlanYearInfoVo")
    private List<SupplyPlanYearInfoVo> supplyPlanYearInfoVo;
	
	@ApiModelProperty(value="供货计划项目地块List", name="supplyPlanProjectLandVo")
    private List<SupplyPlanProjectLandVo> supplyPlanProjectLandVo;

	@ApiModelProperty(value="供货计划业态关系List", name="supplyPlanLandProductTypeMapVo")
    private List<SupplyPlanLandProductTypeMapVo> supplyPlanLandProductTypeMapVo;

}
