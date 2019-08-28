package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.ProductTypeInfoDto;
import com.tahoecn.bo.model.entity.BoProductType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 业态表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProductTypeMapper extends BaseMapper<BoProductType> {

    List<ProductTypeInfoDto> selectTypeInfo();

    List<ProductTypeInfoDto> selectByProductTypeId(@Param("productTypeId") String productTypeId);
}
