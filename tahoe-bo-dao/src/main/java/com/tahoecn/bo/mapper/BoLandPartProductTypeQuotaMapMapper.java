package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.dto.BoLandPartProductTypeMapExtendDto;
import com.tahoecn.bo.model.dto.BoLandPartProductTypeQuotaMapExtendsDto;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 地块业态指标数据表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoLandPartProductTypeQuotaMapMapper extends BaseMapper<BoLandPartProductTypeQuotaMap> {

    void updateByLandPartProductTypeId(@Param("landPartProductTypeId") String landPartProductTypeId,@Param("isdel") int isdel);

    List<BoLandPartProductTypeQuotaMap> selectByTypeId(@Param("list") List<String> landPartProductTypeMapIds);

    List<BoLandPartProductTypeQuotaMapExtendsDto> selectTotalQuoteByProjectIdAndLandId(@Param("projectId") String projectId, @Param("landId") String landId);

    List<BoLandPartProductTypeMapExtendDto> selectLandPartByProIdAndLand(@Param("projectId") String projectId, @Param("landId") String landId);

    List<BoLandPartProductTypeQuotaMap> selectQuotaByProjectIdAndLandId(@Param("projectId") String projectId,@Param("landId") String landId);
}
