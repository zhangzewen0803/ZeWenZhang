package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.dto.ProjectAndExtendInfoDto;
import com.tahoecn.bo.model.dto.ProjectInfoDto;
import com.tahoecn.bo.model.entity.BoProjectExtend;
import com.tahoecn.bo.model.vo.reqvo.ProjectUpdateVersReqParam;

/**
 * <p>
 * 项目基础信息扩展表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectExtendMapper extends BaseMapper<BoProjectExtend> {

	BoProjectExtend selectByProjectId(@Param("projectId") String projectId,@Param("version") String version, @Param("isAdopt") String isAdopt);

	/**
	 * @Title: selectByApproveId
	 * @Description: 根据流程id查询数据 用于更新流程状态
	 * @param wfId
	 * @return BoProjectExtend
	 * @author liyongxu
	 * @date 2019年6月5日 下午3:01:10
	 */
	BoProjectExtend selectByApproveId(@Param("approveId") String approveId);

	List<ProjectAndExtendInfoDto> selectVersionInfoByProjectId(@Param("projectId") String projectId);

	ProjectAndExtendInfoDto selectVersionByProjectId(@Param("projectUpdateVersReqParam") ProjectUpdateVersReqParam projectUpdateVersReqParam);

	ProjectInfoDto selectProjectByVersionId(@Param("versionId") String versionId);

	ProjectInfoDto selectSubProjectByVersionId(@Param("versionId") String versionId);

	List<String> selectSubPicurlsByProjectId(@Param("projectId") String projectId);

	List<String> selectSubProIdsByVersionId(@Param("versionId") String versionId);

	int updateForClearApproveData(BoProjectExtend boProjectExtend);
	
}
