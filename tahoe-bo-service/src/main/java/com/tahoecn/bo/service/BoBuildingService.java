package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoBuilding;
import com.tahoecn.bo.model.vo.CurrentUserVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 楼栋信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingService extends IService<BoBuilding> {

    /**
     * 查楼栋
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoBuilding> getBuildingList(String projectQuotaExtendId);

    /**
     * 查楼栋
     *
     * @param ids
     * @return
     */
    List<BoBuilding> getBuildingList(Collection<String> ids);

    /**
     * 查询指定楼栋名称在当前版本的项目/分期中是否曾存在使用记录，如果存在则返回记录
     * 包含已删除的、已禁用的、未审批通过的
     * @param projectQuotaExtendId
     * @param names
     * @return
     */
    List<BoBuilding> getAllBuildingList(String projectQuotaExtendId, Collection<String> names);

    /**
     * 查询指定楼栋名称在当前版本的项目/分期中是否曾存在使用记录，如果存在则返回记录，key为楼栋名称
     * 包含已删除的、已禁用的、未审批通过的
     * @param projectQuotaExtendId
     * @param names
     * @return
     */
    Map<String,BoBuilding> getAllBuildingNameMap(String projectQuotaExtendId, Collection<String> names);

	/**
	 * @Title: deleteBuildingByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年7月15日 下午1:47:42 
	*/
	void deleteBuildingByBulId(String buildingId, CurrentUserVO currentUserVO);

}
