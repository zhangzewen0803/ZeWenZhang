package com.tahoecn.bo.model.dto;


import lombok.Data;

@Data
public class SubProjectAndExendInfoDto {

    private String projectId;
    private String subProjectId;
    private String projectExtendId;
    private Integer versionStatus;
    private String version;
}
