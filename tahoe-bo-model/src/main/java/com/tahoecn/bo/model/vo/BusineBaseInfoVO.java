package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 阶段信息响应对象
 *
 */
@ApiModel("阶段信息响应对象")
@Data
public class BusineBaseInfoVO{

	@ApiModelProperty(value="阶段Code", name="stageCode")
	private String stageCode;

	@ApiModelProperty(value="项目/分期Id", name="projectId")
    private String projectId;
    
}