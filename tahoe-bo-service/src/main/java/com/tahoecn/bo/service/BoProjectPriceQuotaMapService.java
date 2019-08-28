package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectPriceQuotaMap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目分期价格指标数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectPriceQuotaMapService extends IService<BoProjectPriceQuotaMap> {

    /**
     * 查价格指标
     * @param projectPriceExtendId
     * @return
     */
    List<BoProjectPriceQuotaMap> getProjectPriceQuotaList(String projectPriceExtendId);

}
