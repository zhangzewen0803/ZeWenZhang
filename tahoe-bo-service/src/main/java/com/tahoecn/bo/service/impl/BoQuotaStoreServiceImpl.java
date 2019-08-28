package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.mapper.BoQuotaStoreMapper;
import com.tahoecn.bo.model.entity.BoQuotaStore;
import com.tahoecn.bo.service.BoQuotaStoreService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 指标库表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoQuotaStoreServiceImpl extends ServiceImpl<BoQuotaStoreMapper, BoQuotaStore> implements BoQuotaStoreService {

    @Override
    @Cacheable(value = "BoQuotaStoreServiceImpl",key = "'getCodeValueTypeMap'")
    public Map<String, String> getCodeValueTypeMap() {
        List<BoQuotaStore> list = list();
        Map<String,String> map = new HashMap<>();
        list.stream().forEach(x->map.put(x.getCode(),x.getValueType()));
        return map;
    }
}
