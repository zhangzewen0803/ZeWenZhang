package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoQuotaStore;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 指标库表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoQuotaStoreService extends IService<BoQuotaStore> {

    Map<String,String> getCodeValueTypeMap();

}
