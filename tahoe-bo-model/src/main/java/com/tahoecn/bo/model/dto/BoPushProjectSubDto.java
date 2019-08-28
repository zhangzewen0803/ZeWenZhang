package com.tahoecn.bo.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 分期Dto
 * @since 2019-06-10
 */
@Data
public class BoPushProjectSubDto{

    /**
     * 分期ID
     */
    private String projectSubId;

    /**
     * 楼栋信息
     */
    private List<BoPushBuildingDto> buildingList;

	@Override
	public String toString() {
		return "[{\"projectSubId\":\"" + projectSubId + "\", \"buildingList\":" + buildingList + "}]";
	}
    
}
