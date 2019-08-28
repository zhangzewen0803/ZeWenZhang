package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value="工程进度填报",description="工程进度填报")
public class ProjectSpeedVo {

    @ApiModelProperty(value="工程进度填报Id", name="id")
    private String id;

    @ApiModelProperty(value="楼栋Id", name="buildId")
    private String buildId;

    @ApiModelProperty(value="楼栋名称", name="buildName")
    private String buildName;

    @ApiModelProperty(value="是否开工（0-否；1-是）", name="isStart")
    private Integer isStart;

    @ApiModelProperty(value="开工时间", name="isStart")
    private String startTime;

    @ApiModelProperty(value="预售形象进度要求", name="requirements")
    private String requirements;

    @ApiModelProperty(value="当前进度（0-未施工；1-施工中: 2-结构施工完毕）", name="currentStart")
    private Integer currentStart;

    @ApiModelProperty(value="完工层数", name="completeNumber")
    private String completeNumber;

    @ApiModelProperty(value="是否达到预售形象进度（0-否；1-是）", name="isRequirements")
    private Integer isRequirements;

    @ApiModelProperty(value="是否取得预售证（0-否；1-是）", name="isSale")
    private Integer isSale;

    @ApiModelProperty(value="是否达到按揭放款进度（0-否；1-是）", name="isLoan")
    private Integer isLoan;

}
