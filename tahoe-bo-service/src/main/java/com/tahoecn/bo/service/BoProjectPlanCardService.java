package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoProjectPlanCard;
import com.tahoecn.bo.model.entity.BoProjectPlanCardBuildingMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.List;

/**
 * <p>
 * 工规证表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
public interface BoProjectPlanCardService extends IService<BoProjectPlanCard> {

    /**
     * 查询工规证列表
     * @param projectQuotaExtendId
     * @return
     */
    List<BoProjectPlanCard> getProjectPlanCardList(String projectQuotaExtendId);

    /**
     * 新增工规证
     * @param boProjectPlanCard
     * @param boProjectPlanCardBuildingMapList
     * @return
     */
    boolean addProjectPlanCard(BoProjectPlanCard boProjectPlanCard, List<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingMapList);

    /**
     * 更新工规证及楼栋
     * @param boProjectPlanCard
     * @param deleteList
     * @param insertList
     * @return
     */
    boolean updateProjectPlanCardAndBuilding(BoProjectPlanCard boProjectPlanCard, List<BoProjectPlanCardBuildingMap> deleteList, List<BoProjectPlanCardBuildingMap> insertList);

    /**
     * 删除，批量
     * @param ids
     * @param currentUserVO
     * @return
     */
    boolean removeProjectPlanCard(String[] ids, CurrentUserVO currentUserVO);

    /**
     * 指定版本是否存在指定的工规证号
     * @param projectQuotaExtendId
     * @param planCode
     * @return
     */
    boolean existsPlanCode(String projectQuotaExtendId,String planCode);



}
