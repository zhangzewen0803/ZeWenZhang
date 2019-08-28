package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoGovPlanCard;
import com.tahoecn.bo.model.entity.BoGovPlanCardBuildingMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.List;

/**
 * <p>
 * 政府方案信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
public interface BoGovPlanCardService extends IService<BoGovPlanCard> {

    /**
     * 查询政府方案列表
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoGovPlanCard> getGovPlanCardList(String projectQuotaExtendId);

    /**
     * 新增政府方案
     *
     * @param boGovPlanCard                政府方案
     * @param boGovPlanCardBuildingMapList 政府方案楼栋关系
     * @return
     */
    boolean addGovPlanCardAndBuilding(BoGovPlanCard boGovPlanCard, List<BoGovPlanCardBuildingMap> boGovPlanCardBuildingMapList);


    /**
     * 更新政府方案
     *
     * @param boGovPlanCard
     * @param deleteList
     * @param insertList
     * @return
     */
    boolean updateGovPlanCardAndBuilding(BoGovPlanCard boGovPlanCard, List<BoGovPlanCardBuildingMap> deleteList, List<BoGovPlanCardBuildingMap> insertList);

    /**
     * 批量删除政府方案
     *
     * @param ids
     * @param currentUserVO
     * @return
     */
    boolean removeGovPlanCard(String[] ids, CurrentUserVO currentUserVO);

    /**
     * 查询某版本下是否已存在指定的政府方案号
     * @param projectQuotaExtendId
     * @param planCode
     * @return
     */
    boolean existsPlanCode(String projectQuotaExtendId,String planCode);

}
