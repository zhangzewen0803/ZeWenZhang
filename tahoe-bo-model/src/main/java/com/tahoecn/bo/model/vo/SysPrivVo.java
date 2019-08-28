package com.tahoecn.bo.model.vo;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="用户系统权限信息",description="用户系统权限信息")
public class SysPrivVo {

	@ApiModelProperty(value="权限ID", name="sysPrivId")
	private String sysPrivId;
	
    @ApiModelProperty(value="权限类型（MENU-菜单；TAB-选项卡；BUTTON-按钮；）", name="permissionType")
    private String permissionType;

    @ApiModelProperty(value="权限名称", name="name")
    private String name;

    @ApiModelProperty(value="权限编码", name="code")
    private String code;

    @ApiModelProperty(value="权限父级ID", name="parentId")
    private String parentId;

    @ApiModelProperty(value="权限地址", name="permissionUrl")
    private String permissionUrl;

    @ApiModelProperty(value="是否删除（0-否；1-是）", name="isDelete")
    private Integer isDelete;

    @ApiModelProperty(value="是否禁用（0-否；1-是）", name="isDisable")
    private Integer isDisable;

    @ApiModelProperty(value="创建时间", name="createTime")
    private LocalDateTime createTime;
    
}
