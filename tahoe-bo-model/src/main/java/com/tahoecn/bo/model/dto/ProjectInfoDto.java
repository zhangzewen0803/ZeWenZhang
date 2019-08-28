package com.tahoecn.bo.model.dto;

import lombok.Data;

@Data
public class ProjectInfoDto {

    private String projectId;
    private String subProjectId;
    private String versionId;
    private String projectName;
    private String subProjectName;
    private String caseName;
    private String companyAreaName;
    private String companyCityName;
    private String cityId;
    private String cityName;
    private String projectAddress;
    private String gainStatusCode;
    private String picUrl;
    private String point;
    private String version;
    private Integer versionStatus;
    private String projectType;
    private String tradeModeCode;
    private String taxTypeCode;
    private String mergeTableTypeCode;

}
