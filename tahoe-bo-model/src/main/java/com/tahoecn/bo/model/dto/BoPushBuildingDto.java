package com.tahoecn.bo.model.dto;

import java.util.List;

import lombok.Data;

/**
 * 楼栋Dto
 * @since 2019-06-10
 */
@Data
public class BoPushBuildingDto{

    /**
     * 楼栋ID
     */
    private String buildingId;

    /**
     * 楼栋名称
     */
    private String buildingName;
    
    /**
     * 业态信息
     */
    private List<BoPushProductDto> productInfo;

	@Override
	public String toString() {
		return "{\"buildingId\":\"" + buildingId + "\", \"buildingName\":\"" + buildingName + "\",\"productInfo\":"
				+ productInfo + "}";
	}
    
}
