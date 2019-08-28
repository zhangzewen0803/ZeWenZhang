package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectPlanCardBuildingMap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 工规证楼栋关系表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
public interface BoProjectPlanCardBuildingMapService extends IService<BoProjectPlanCardBuildingMap> {

    /**
     * 查询工规证楼栋
     * @param projectPlanCardIdList
     * @return
     */
    List<BoProjectPlanCardBuildingMap> getBoProjectPlanCardBuildingList(List<String> projectPlanCardIdList);

    /**
     * 查询工规证楼栋
     * @param projectPlanCardId
     * @return
     */
    List<BoProjectPlanCardBuildingMap> getBoProjectPlanCardBuildingList(String projectPlanCardId);

}
