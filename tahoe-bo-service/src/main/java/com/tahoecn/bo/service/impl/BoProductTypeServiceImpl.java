package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.mapper.BoProductTypeMapper;
import com.tahoecn.bo.model.dto.ProductTypeInfoDto;
import com.tahoecn.bo.model.entity.BoProductType;
import com.tahoecn.bo.service.BoProductTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业态表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProductTypeServiceImpl extends ServiceImpl<BoProductTypeMapper, BoProductType> implements BoProductTypeService {

    @Autowired
    private BoProductTypeMapper productTypeMapper;

    @Override
    public List<ProductTypeInfoDto> selectProductTypeInfoList(String productTypeId) {
        List<ProductTypeInfoDto> typeList = new ArrayList<ProductTypeInfoDto>();
        if (StringUtils.isNotBlank(productTypeId)) {
            typeList = productTypeMapper.selectByProductTypeId(productTypeId);
        } else {
            typeList = productTypeMapper.selectTypeInfo();
        }

        return typeList;
    }

    /**
     * TODO +缓存
     * @return
     */
    @Override
    public Map<String, Integer> getCodeSortMap() {
        List<BoProductType> list = list();
        Map<String, Integer> map = new HashMap<>();
        list.stream().forEach(x -> map.put(x.getCode(), x.getSortNo() == null ? 0 : x.getSortNo()));
        return map;
    }
}
