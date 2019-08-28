package com.tahoecn.bo.model.dto;

import lombok.Data;

@Data
public class SubProjectAndExtendInfoDto extends ProjectAndExtendInfoDto{

    private String subProjectId;

    private String subProjectName;

}
