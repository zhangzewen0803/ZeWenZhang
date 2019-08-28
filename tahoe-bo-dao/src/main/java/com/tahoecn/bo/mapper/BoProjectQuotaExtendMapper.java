package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.*;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目分期扩展信息表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectQuotaExtendMapper extends BaseMapper<BoProjectQuotaExtend> {

    List<ProjectProductTypeQuotaDto> selectProductQuotaByProjectAndLandIds(@Param("projectId") String proId,
                                                                           @Param("list") List<ProjectLandToSubProjectDto> projectLandToSubProjectList);

    List<SubProjectUseLandsDto> selectUseLandsBySubProjectId(@Param("subProjectId") String subProjectId);

    List<ProjectProductTypeQuotaDto> selectProductQuotaByProjectAndLandId(@Param("subProjectId") String subProjectId, @Param("landId") String landId);

    List<SubProjectLandQuoteDto> selectLandQuoteBySubIdAndLandId(@Param("subProjectId") String subProjectId,@Param("landId") String landId);

    List<ProjectProductTypeQuotaDto> selectUseLandQuoteBySubIdAndLandId(@Param("subProjectId")String subProjectId,@Param("landId") String landId);

    BoProjectQuotaExtend selectByApproveId(@Param("approveId") String approveId);

    List<BoLandPartProductTypeQuotaMap> selectQuoteLandBySubIdAndLandId(@Param("subProjectId") String subProjectId,@Param("landId") String landId);

    List<BoLandPartProductTypeQuotaMap> selectSubQuoteBySubIds(@Param("list") List<String> subIds,@Param("landId") String landId);

    List<BoProjectLandPartMap> selectQuotaExtendByProjectAndLandId(@Param("projectId") String projectId, @Param("landId") String landId);

    int updateForClearApproveData(BoProjectQuotaExtend boProjectQuotaExtend);

    List<SubProjectToAdoptPolicyDto> selectAdoptStageBySubProIds(@Param("list") List<String> subProIds,@Param("stageCode") String stageCode);

    BoProjectQuotaExtend selectNewInfoByProjectId(@Param("projectId")String projectId,@Param("stageCode") String stageCode);

    List<String> selectAdoptStageOneBySubProId(@Param("projectId")String projectId,@Param("stageCodeOne") String stageCodeOne);

    List<String> selectAdoptStageOtherBySubProId(@Param("projectId")String projectId,@Param("stageCodeOther") String stageCodeOther);

	/**
	 * @Title: getAreaVersionInfos 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectPriceExtendId void
	 * @author liyongxu
	 * @date 2019年8月12日 上午10:51:06 
	*/
	BoProjectQuotaExtend getAreaVersionInfos(@Param("projectPriceExtendId")String projectPriceExtendId);
}
