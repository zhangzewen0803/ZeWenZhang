package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.dto.ProductTypeInfoDto;
import com.tahoecn.bo.model.entity.BoProductType;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业态表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProductTypeService extends IService<BoProductType> {

    @Cacheable(value = "productType",key = "#productTypeId")
    List<ProductTypeInfoDto> selectProductTypeInfoList(String productTypeId);

    /**
     * 获取业态code与排序
     *
     * @return
     */
    Map<String, Integer> getCodeSortMap();
}
