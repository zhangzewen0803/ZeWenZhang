package com.tahoecn.bo.model.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value="用户信息",description="用户信息")
public class UserInfoVo {

    @ApiModelProperty(value="用户id", name="fdSid")
    private String fdSid;
    
    @ApiModelProperty(value="名字", name="fdName")
    private String fdName;

    @ApiModelProperty(value="用户名", name="fdUsername")
    private String fdUsername;
    
    @ApiModelProperty(value="所属组织名称", name="fdOrgName")
    private String fdOrgName;
    
    @ApiModelProperty(value="所属组织id", name="fdOrgId")
    private String fdOrgId;
    
    @ApiModelProperty(value="所属组织全路径", name="fdOrgNameTree")
    private String fdOrgNameTree;
    
    @ApiModelProperty(value="所属组织id全路径", name="fdOrgIdTree")
    private String fdOrgIdTree;
    
    @ApiModelProperty(value="显示顺序", name="fdOrder")
    private Integer fdOrder;
    
    @ApiModelProperty(value="数据是否锁定1锁定, -1不锁定", name="fdLock")
    private Integer fdLock;
    
    @ApiModelProperty(value="泰信id", name="fdTahoeMessageSid")
    private String fdTahoeMessageSid;
    
    @ApiModelProperty(value="头像大图", name="fdSmallPhotoUrl")
    private String fdTopPhotoUrl;
    
    @ApiModelProperty(value="头像小图", name="fdSmallPhotoUrl")
    private String fdSmallPhotoUrl;
    
    @ApiModelProperty(value="入职时间戳", name="fdEntryTime")
    private Long fdEntryTime;
    
    @ApiModelProperty(value="组织机构全路径相关信息", name="orgList")
    private List<OrgInfoVO> orgList;

}
