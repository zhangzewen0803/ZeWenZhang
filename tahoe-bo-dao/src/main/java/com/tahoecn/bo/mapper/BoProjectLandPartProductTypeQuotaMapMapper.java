package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.BoProjectLandPartProductTypeQuotaMapExtendsDto;
import com.tahoecn.bo.model.dto.ProductAttrQuotaDto;
import com.tahoecn.bo.model.dto.ProjectProductTypeQuotaDto;
import com.tahoecn.bo.model.dto.SubProTotalQuoteValueDto;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeQuotaMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目分期地块业态指标数据表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
public interface BoProjectLandPartProductTypeQuotaMapMapper extends BaseMapper<BoProjectLandPartProductTypeQuotaMap> {

    List<ProjectProductTypeQuotaDto> selectSubProjectQuotaValuesByProjectId(@Param("list") List<String> subProIds);

    List<ProjectProductTypeQuotaDto> selectTotalQuotaBySubProjectAndLandIds(@Param("list") List<String> subProjectIds,@Param("landId") String landId);

    List<BoLandPartProductTypeQuotaMap> selectLandQuoteByProductIds(@Param("list") List<String> landProductIds);

    List<BoProjectLandPartProductTypeQuotaMap> selectByTypeQuoteIds(@Param("list") List<String> landPartProductTypeMapIds);

    List<ProjectProductTypeQuotaDto> selectTotalQuotaBySubProjectIdsAndLandId(@Param("list") List<String> subIds,@Param("landId") String landId);

    List<SubProTotalQuoteValueDto> selectTotalQuoteByLandIds(@Param("list") List<String> notDevLandPartIds);

    List<BoProjectLandPartProductTypeQuotaMapExtendsDto> selectTotalQuoteBySubProjectIdsAndLandId(@Param("list") List<String> subIds,@Param("landId") String landId);

    List<ProjectProductTypeQuotaDto> selectTotalQuoteByLandId(@Param("landId") String landId);

    void updateIsDeleteBatch(@Param("list") List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList);

    List<ProductAttrQuotaDto> selectQuoteByProductIds(@Param("list") List<String> landProductToProjectIds);

    List<BoProjectLandPartProductTypeQuotaMap> selectQuotaByLandId(@Param("landId") String landId);

    List<BoProjectLandPartProductTypeQuotaMap> selectQuotaBySubIdsAndLandId(@Param("list") List<String> subProjectIds,@Param("landId") String landId);

    List<BoProjectLandPartProductTypeQuotaMapExtendsDto> selectQuotaByLandIdAndExtendId(@Param("extendId") String versionId, @Param("landId") String landId);
}
