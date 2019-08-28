package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeQuotaMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.List;

/**
 * <p>
 * 楼栋业态指标数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingProductTypeQuotaMapService extends IService<BoBuildingProductTypeQuotaMap> {

    /**
     * 查询楼栋业态指标
     *
     * @param buildingProductTypeIdList
     * @return
     */
    List<BoBuildingProductTypeQuotaMap> getBuildingProductTypeQuotaList(List<String> buildingProductTypeIdList);

    /**
     * 查询楼栋业态指标
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuildingProductTypeQuotaMap> getBuildingProductTypeQuotaList(String projectQuotaExtendId);

    /**
     * 查已删除的楼栋业态指标
     * @param buildingProductTypeIdList
     * @return
     */
    List<BoBuildingProductTypeQuotaMap> getDeletedBuildingProductTypeQuotaList(List<String> buildingProductTypeIdList);

	/**
	 * @Title: deletedBuildingProductTypeQuotaByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月15日 下午2:27:52 
	*/
	void deletedBuildingProductTypeQuotaByBulId(String buildingId, CurrentUserVO currentUserVO);

	/**
	 * @Title: deletedBuildingProductTypeQuotaByProductTypeId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productTypeId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月15日 下午2:31:43 
	*/
	void deletedBuildingProductTypeQuotaByProductTypeId(String productTypeId, CurrentUserVO currentUserVO);

}
