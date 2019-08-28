package com.tahoecn.bo.model.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value="用户详情信息",description="用户详情信息")
public class UsreDetailsRespVo {

	@ApiModelProperty(value="用户信息", name="userInfo")
	private UserInfoVo userInfo;

	@ApiModelProperty(value="系统权限数据", name="userSysPrivList")
	private List<SysPrivVo> userSysPrivList;

	@ApiModelProperty(value="项目权限数据", name="projectPrivJson")
	private List<PowerProjectInfoVo> projectPrivJson;

}
