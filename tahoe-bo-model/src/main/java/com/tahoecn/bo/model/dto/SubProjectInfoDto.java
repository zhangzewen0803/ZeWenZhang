package com.tahoecn.bo.model.dto;

import com.tahoecn.bo.model.vo.PartitionInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubProjectInfoDto {

    private String projectId;
    private String subProjectId;
    private String extendId;
    private String projectType;
    private String tradeModeCode;
    private String mergeTableTypeCode;
    private String taxTypeCode;
    private String obtainStatusCode;
    private String subProjectPic;
    private String projectName;
    private String subProjectName;
    private String version;
    private String versionState;
    private String processId;


}
