package com.tahoecn.bo.model.dto;

import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import lombok.Data;

@Data
public class BoLandPartProductTypeQuotaMapExtendsDto extends BoLandPartProductTypeQuotaMap {

    private String productCode;
    private String attrQuotaValue;

}
