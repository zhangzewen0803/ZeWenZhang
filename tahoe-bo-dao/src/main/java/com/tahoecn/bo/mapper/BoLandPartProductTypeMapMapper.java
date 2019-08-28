package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.BoLandPartProductTypeMapExtendDto;
import com.tahoecn.bo.model.dto.SubProTotalQuoteValueDto;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.vo.LandBasicInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 地块业态关系表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoLandPartProductTypeMapMapper extends BaseMapper<BoLandPartProductTypeMap> {

    List<BoLandPartProductTypeMap> selectProductTypeByLandId(@Param("landId") String landId);

    List<SubProTotalQuoteValueDto> selectTotalQuoteByProjectId(@Param("projectId") String projectId);

    List<LandBasicInfoVo> selectLandInfoByProId(@Param("projectId") String projectId);

    List<BoLandPartProductTypeMap> selectLandProductByProIdAndLandId(@Param("projectId") String projectId,@Param("landId") String landId);

    List<String> selectIsDevLandBySubIds(@Param("list") List<String> subIds);

    List<LandBasicInfoVo> selectLandInfoByProIdAndLandIds(@Param("projectId") String parentSid,@Param("list") List<String> landIds);

    List<BoLandPartProductTypeMapExtendDto> selectProductByProIdAndLandId(@Param("projectId") String projectId,@Param("landId") String landId);
}
