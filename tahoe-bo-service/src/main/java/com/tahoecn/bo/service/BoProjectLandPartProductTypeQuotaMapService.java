package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeQuotaMap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 项目分期地块业态指标数据表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
public interface BoProjectLandPartProductTypeQuotaMapService extends IService<BoProjectLandPartProductTypeQuotaMap> {

    List<BoProjectLandPartProductTypeQuotaMap> getListByProjectLandPartProductTypeIds(Collection<String> projectLandPartProductTypeIds);

}
