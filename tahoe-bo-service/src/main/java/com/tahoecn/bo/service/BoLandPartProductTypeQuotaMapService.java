package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 地块业态指标数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoLandPartProductTypeQuotaMapService extends IService<BoLandPartProductTypeQuotaMap> {

    /**
     * 查询地块业态指标列表
     * @param landPartProductTypeIdList 地块业态关系Id列表
     * @return
     */
    List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaList(Collection<String> landPartProductTypeIdList);

    /**
     * 查询地块业态指标列表
     * @param projectQuotaExtendId 面积版本ID
     * @return
     */
    List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaList(String projectQuotaExtendId);

    /**
     * 查询地块业态指标列表
     * @param projectQuotaExtendId 面积版本ID
     * @return
     */
    List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaListByPriceVersionId(String priceVersionId);


}
