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
public class SupplyPlanProjectLandVo{

	@ApiModelProperty(value="项目/分期Id", name="projectId")
    private String projectId;
	
	@ApiModelProperty(value="项目/分期名称", name="projectName")
	private String projectName;

	@ApiModelProperty(value="地块ID", name="landId")
    private String landId;

	@ApiModelProperty(value=" 地块名称", name="landName")
    private String landName;

}
