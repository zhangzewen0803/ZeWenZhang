package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 楼栋业态关系表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingProductTypeMapService extends IService<BoBuildingProductTypeMap> {

    /**
     * 查询楼栋业态列表
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuildingProductTypeMap> getBuildingProductTypeList(String projectQuotaExtendId);

    /**
     * 查询楼栋业态列表
     *
     * @param buildingIdList
     * @return
     */
    List<BoBuildingProductTypeMap> getBuildingProductTypeListByBuildingIdList(Collection<String> buildingIdList);

    /**
     * 查询楼栋业态列表 根据价格版本ID
     *
     * @param projectPriceExtendId
     * @return
     */
    List<BoBuildingProductTypeMap> getBuildingProductTypeListByPriceVersionId(String projectPriceExtendId);

    /**
     * 查询当前版本所在分期的所有版本所有阶段已删除的楼栋与业态关系
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuildingProductTypeMap> getDeletedBuildingProductTypeList(String projectQuotaExtendId);


    /**
     * 增删改楼栋业态数据
     * @param param
     * @return
     */
    boolean saveOrDeleteOrUpdateBuildingProductType(Map<String, Object> param);

    /**
     * 通过审批ID查询当前版本的楼栋业态数据信息，仅“营销系统”使用此接口返回数据
     * @param approveId 审批ID
     * @return
     */
    BoPushProjectSubDto getBuildingProductInfoListByApproveId(String approveId);

    BoPushProjectSubDto getBuildingProductInfoListByVersionId(String versionId);
    
	/**
	 * @Title: getBulSubListByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @date 2019年7月12日 上午11:16:24 
	*/
    List<BoBuildingProductTypeMap> getBuildingProductTypeListByBulId(String buildingId);

	/**
	 * @Title: getBulSubListBySubId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param subId
	 * @return List<BoBuildingProductTypeMap>
	 * @author liyongxu
	 * @date 2019年7月12日 上午11:50:27 
	*/
	List<BoBuildingProductTypeMap> getBuildingProductTypeListByProductId(String productId);

	/**
	 * @Title: deleteBuildingProductTypeByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月12日 下午5:12:58 
	*/
	void deleteBuildingProductTypeByBulId(String buildingId, CurrentUserVO currentUserVO);

	/**
	 * @Title: deleteBuildingProductTypeBySubId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param subId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月12日 下午5:13:15 
	*/
	void deleteBuildingProductTypeByProductId(String productId, CurrentUserVO currentUserVO);

}
