package com.tahoecn.bo.model.vo.reqvo;

import com.tahoecn.bo.model.vo.PartitionInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value="项目分期修改基本信息",description="项目分期修改基本信息")
public class SubProjectUpdateInfoReqParam {

    @ApiModelProperty(name="subProjectId", value="项目分期id", required=true)
    private String subProjectId;
    @ApiModelProperty(name="versionId", value="版本id", required=true)
    private String versionId;

    @ApiModelProperty(name="projectType", value="项目类型", required=false)
    private String projectType;

    @ApiModelProperty(name="tradeModeCode", value="操盘方式", required=false)
    private String tradeModeCode;

    @ApiModelProperty(name="mergeTableTypeCode", value="并表方式", required=false)
    private String mergeTableTypeCode;

    @ApiModelProperty(name="taxTypeCode", value="计税方式", required=false)
    private String taxTypeCode;

    @ApiModelProperty(name="obtainStatusCode", value="获取状态Code", required=false)
    private String obtainStatusCode;

    @ApiModelProperty(name="subProjectPic", value="分期总图", required=false)
    private String subProjectPic;

    @ApiModelProperty(value="分区Details", name="partitions")
    private List<PartitionInfoVo> partitions;

}
