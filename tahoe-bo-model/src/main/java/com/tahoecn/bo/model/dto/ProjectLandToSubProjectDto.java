package com.tahoecn.bo.model.dto;

import lombok.Data;

@Data
public class ProjectLandToSubProjectDto {

    private String proId;
    private String subProId;
    private String landPartId;
    private String landId;
    private String landName;
    private String landTotalMeasure;
    private String meterBuildMeasure;
    private String landConstructableMeasure;

}
