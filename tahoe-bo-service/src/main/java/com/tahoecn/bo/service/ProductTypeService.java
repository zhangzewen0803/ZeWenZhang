package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.dto.ProductTypeDto;
import com.tahoecn.bo.model.entity.ProductType;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @ClassName：ProductTypeService
 * @Description：业态表服务类
 * @Author zewenzhang
 * @Date 2019/8/29 14:40
 * @Version 1.0.0
 */
public interface ProductTypeService extends IService<ProductType> {

//    @Cacheable(value = "productType",key = "#productTypeId")
    List<ProductTypeDto> findProductTypeList(String productTypeId);

}
