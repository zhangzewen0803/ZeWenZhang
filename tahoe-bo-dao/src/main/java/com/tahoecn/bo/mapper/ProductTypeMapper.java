package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.dto.ProductTypeDto;
import com.tahoecn.bo.model.entity.ProductType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName：ProductTypeMapper
 * @Description：业态表Mapper
 * @Author zewenzhang
 * @Date 2019/8/29 14:42
 * @Version 1.0.0
 */
@Mapper
public interface ProductTypeMapper extends BaseMapper<ProductType> {

    List<ProductTypeDto> selectProductTypeList(@Param("productTypeId") String productTypeId);

    List<ProductTypeDto> selectTypeInfo();
}
