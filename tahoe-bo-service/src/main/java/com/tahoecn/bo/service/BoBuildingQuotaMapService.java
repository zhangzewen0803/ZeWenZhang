package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoBuildingQuotaMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.List;

/**
 * <p>
 * 楼栋指标数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingQuotaMapService extends IService<BoBuildingQuotaMap> {

    /**
     * 查询楼栋指标
     *
     * @param buildingIdList
     * @return
     */
    List<BoBuildingQuotaMap> getBuildingQuotaList(List<String> buildingIdList);


    /**
     * 查询楼栋指标
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuildingQuotaMap> getBuildingQuotaList(String projectQuotaExtendId);


    /**
     * 查询已删除楼栋指标
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuildingQuotaMap> getDeletedBuildingQuotaList(String projectQuotaExtendId);


	/**
	 * @Title: deleteBuildingQuotaByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月15日 下午2:26:09 
	*/
	void deleteBuildingQuotaByBulId(String buildingId, CurrentUserVO currentUserVO);

}
