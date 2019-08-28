package com.tahoecn.bo.service;

        import com.baomidou.mybatisplus.extension.service.IService;
        import com.tahoecn.bo.model.entity.BoGovPlanCardBuildingMap;

        import java.util.List;

/**
 * <p>
 * 政府方案楼栋关系表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
public interface BoGovPlanCardBuildingMapService extends IService<BoGovPlanCardBuildingMap> {

    /**
     * 查询政府方案关联楼栋列表
     *
     * @param govPlanCardIdList
     * @return
     */
    List<BoGovPlanCardBuildingMap> getGovPlanCardBuildingList(List<String> govPlanCardIdList);

    /**
     * 查询政府方案关联楼栋列表
     *
     * @param govPlanCardId
     * @return
     */
    List<BoGovPlanCardBuildingMap> getGovPlanCardBuildingList(String govPlanCardId);


}
