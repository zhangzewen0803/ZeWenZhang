package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.mapper.ProductTypeMapper;
import com.tahoecn.bo.model.dto.ProductTypeDto;
import com.tahoecn.bo.model.entity.ProductType;
import com.tahoecn.bo.service.ProductTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName：ProductTypeServiceImpl
 * @Description：业态表实现类
 * @Author zewenzhang
 * @Date 2019/8/29 14:40
 * @Version 1.0.0
 */
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper,ProductType> implements ProductTypeService {

    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Override
    public List<ProductTypeDto> findProductTypeList(String productTypeId) {

        List<ProductTypeDto> typeList = new ArrayList<ProductTypeDto>();

        if (StringUtils.isNotBlank(productTypeId)) {
            typeList = productTypeMapper.selectProductTypeList(productTypeId);
            for (ProductTypeDto productTypeDto : typeList) {
                System.out.println(productTypeDto);
            }
        } else {
            typeList = productTypeMapper.selectTypeInfo();
        }
        return typeList;
    }
}
