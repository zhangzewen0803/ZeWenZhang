package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.BoLandPartProductTypeMapExtendDto;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 项目分期地块业态关系表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
public interface BoProjectLandPartProductTypeMapMapper extends BaseMapper<BoProjectLandPartProductTypeMap> {

    List<BoLandPartProductTypeMapExtendDto> selectLandProductByProjectId(@Param("projectId") String projectId, @Param("landId") String landId);

    List<BoProjectLandPartProductTypeMap> selectProductBySubIdAndLandId(@Param("subProjectId")String subProjectId,@Param("landId") String landId);

    List<BoLandPartProductTypeMap> selectbyProjectQuotaExtendId(@Param("list") Set<String> landSet);

    BoProjectLandPartMap selectProjectLandInfoBySubIdAndLandId(@Param("subProjectId") String subProjectId,@Param("landId") String landId);

    void updateIsDeleteBatch(@Param("list") List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList);
}
