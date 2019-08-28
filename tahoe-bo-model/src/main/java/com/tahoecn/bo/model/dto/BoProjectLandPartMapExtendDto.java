package com.tahoecn.bo.model.dto;

import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import lombok.Data;

@Data
public class BoProjectLandPartMapExtendDto extends BoProjectLandPartMap {

    private String subRefProjectId;

    private String subRefProjectName;

}
