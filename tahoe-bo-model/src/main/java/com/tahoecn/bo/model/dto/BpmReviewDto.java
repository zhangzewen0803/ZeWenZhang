package com.tahoecn.bo.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BpmReviewDto {

	@ApiModelProperty(value="标题(弃用)", name="docSubject", required=false)
    private String docSubject;
	
	@ApiModelProperty(value="版本描述(弃用)", name="versionDesc", required=false)
	private String versionDesc;
	
	@ApiModelProperty(value="内容", name="docContent", required=true)
	private String docContent;

	@ApiModelProperty(value="类型(项目：PROJECT,分期：SUBPROJECT,面积：AREA,价格：PRICE)", name="type", required=true)
    private String type;
	
	@ApiModelProperty(value="版本Id", name="versionId", required=true)
    private String versionId;
	
	@ApiModelProperty(value="项目分期Id(表单参数)", name="projectId", required=true)
    private String projectId;
}
