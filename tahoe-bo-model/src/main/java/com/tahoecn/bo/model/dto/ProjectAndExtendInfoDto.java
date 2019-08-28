package com.tahoecn.bo.model.dto;


import lombok.Data;

@Data
public class ProjectAndExtendInfoDto {

    private String projectId;

    private String versionId;

    private String versionName;

    private Integer versionStatus;

    private String projectName;

    private String versionProcessId;

    private String versionApprovaledDate;

}
