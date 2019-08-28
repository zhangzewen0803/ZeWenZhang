package com.tahoecn.bo.model.vo;

import com.tahoecn.bo.model.dto.ProjectAndExtendInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="版本信息",description="版本信息")
public class VersionInfoVo {

    @ApiModelProperty(value="版本号", name="versions")
    private List<ProjectAndExtendInfoDto> versions;

    @ApiModelProperty(value="是否可以生成新版本（0：否  1：是）", name="isNew")
    private String isNew;

    @ApiModelProperty(value="版本信息", name="versionDetailsInfo")
    private List<VersionDetailsInfoVo> versionDetailsInfo;

    @Data
    @ApiModel(value="版本信息详情",description="版本信息")
    public static class VersionDetailsInfoVo {

        @ApiModelProperty(value="版本号", name="versionState")
        private Integer versionState;

        @ApiModelProperty(value="版本号属性", name="versionAttr")
        private String versionAttr;

    }


}
