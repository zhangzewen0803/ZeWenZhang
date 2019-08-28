package com.tahoecn.bo.model.bo;

import com.tahoecn.bo.model.dto.ProjectInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class ProjectInfoBo extends ProjectInfoDto {

    private List<String> pics;

}
