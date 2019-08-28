/**
 * 
 */
package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：DataPrivDetailVO
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年6月17日 上午10:42:07 
 * @version 1.0.0 
 */
@Data
@ApiModel(value="用户数据权限详情信息",description="用户数据权限详情信息")
public class DataPrivDetailVO{

	@ApiModelProperty(value="数据权限全局id", name="fdSid", required=true)	
	private String fdSid;
	
	@ApiModelProperty(value="数据权限名称", name="fdName", required=true)	
	private String fdName;
	
	@ApiModelProperty(value="数据范围名称", name="fdDataRangeName", required=true)	
	private String fdDataRangeName;
	
	@ApiModelProperty(value="数据权限code", name="fdCode", required=true)	
	private String fdCode;
	
	@ApiModelProperty(value="该组织架构下的数据权限", name="fdOrgSid", required=true)	
	private String fdOrgSid;
	
	@ApiModelProperty(value="数据权限类型", name="fdType", required=true)	
	private String fdType;
	
}
