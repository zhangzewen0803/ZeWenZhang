package com.tahoecn.bo.model.dto;

import com.tahoecn.bo.model.entity.BoBuildingSpeed;
import lombok.Data;

@Data
public class BoBuildingSpeedExtendDto extends BoBuildingSpeed {

    private String originId;
    private String buildName;

}
