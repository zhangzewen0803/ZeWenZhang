package com.tahoecn.bo.model.dto;


import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeQuotaMap;
import lombok.Data;

@Data
public class BoProjectLandPartProductTypeQuotaMapExtendsDto extends BoProjectLandPartProductTypeQuotaMap {

    private String productCode;

    private String attrQuotaValue;

    private String sumQuoteValue;

}
