package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tahoecn.bo.model.dto.BoProjectLandPartMapExtendDto;
import com.tahoecn.bo.model.dto.LandTotalAreaDto;
import com.tahoecn.bo.model.dto.ProjectLandToSubProjectDto;
import com.tahoecn.bo.model.dto.SubTotalAreaDto;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.model.vo.LandQuotaVo;
import com.tahoecn.bo.model.vo.LandSelInfoVo;
import com.tahoecn.bo.model.vo.SubLandQuotaVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目分期地块数据表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectLandPartMapMapper extends BaseMapper<BoProjectLandPartMap> {

    List<LandQuotaVo> selectLandInfoByProjectId(@Param("projectExtendId") String projectExtendId);

    List<LandSelInfoVo> selectCanUseLandByProject(Page<LandSelInfoVo> page, @Param("cityCompanyId") String cityCompanyId, @Param("landName") String landName);

    List<BoProjectLandPartMap> selectUseLandByLandId(@Param("landId") String landId);

    List<SubLandQuotaVo> selectLandInfoBySubProjectId(@Param("projectExtendId") String projectExtendId);

    List<ProjectLandToSubProjectDto> selectProjectLandBySubProjectId(@Param("subProjectId") String subProjectId);

    void deleteByLandIds(@Param("landIds") List<String> landIds,@Param("subProjectId") String subProjectId,@Param("isDel") int isDel);

    List<BoProjectLandPartMap> selectLastVersionLandByProExtId(@Param("projectId") String projectId);

    LandTotalAreaDto selectTotalProductInfoByProjectAndLandId(@Param("subProjectId")String subProjectId, @Param("landId") String landId);

    List<BoProjectLandPartMap> selectProductInfoByProjectAndLandId(@Param("subProjectId")String subProjectId,@Param("landId") String landId);

    List<BoProjectLandPartMap> selectLandInfoBySubProjectIdAndLandIds(@Param("subProjectId")String subProjectId,@Param("list") List<String> landIds);

    BoProjectLandPartMap selectProjectLandBySubProjectIdAndLandId(@Param("subProjectId")String subProjectId,@Param("landId") String landId);

    List<BoProjectLandPartMap> selectNotDevLandIdsBySubIds(@Param("list") List<String> subIds);

    SubTotalAreaDto selectTotalSubAreaBySubIdsAndLandId(@Param("list") List<MdmProjectInfo> subProjectInfos,@Param("landId")  String landId);

    SubTotalAreaDto selectSubTotalAreaBySubIdsAndLandId(@Param("list") List<String> subProjectInfos,@Param("landId")  String landId);

    List<BoProjectLandPartMap> selectByUseLandBySubProId(@Param("subProjectId") String subProjectId);

    List<BoProjectLandPartMap> selectByUseLandByProId(@Param("projectId") String projectId);

    List<BoProjectLandPartMap> selectLandByExtendId(@Param("extendId") String extendId);

    BoProjectLandPartMap selectLandInfoByVersionIdAndLandId(@Param("extendId") String versionId,@Param("landId") String landId);

    BoProjectLandPartMap selectBySubExtendId(@Param("extendId") String versionId, @Param("landId") String landId);

    BoProjectLandPartMapExtendDto selectProLandPartBySubExtIdAndLandId(@Param("extendId") String versionId,@Param("landId") String landId);

    BoProjectLandPartMap selectLandPartBySubExtIdAndLandId(@Param("extendId")String versionId, @Param("landId") String landId);
}
