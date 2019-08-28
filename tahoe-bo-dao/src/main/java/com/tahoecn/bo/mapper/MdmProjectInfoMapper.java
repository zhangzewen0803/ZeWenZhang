package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.model.bo.MdmProjectInfoBo;
import com.tahoecn.bo.model.dto.*;
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.model.vo.reqvo.ProjectUpdateInfoReqParam;
import com.tahoecn.bo.model.vo.reqvo.SubProjectUpdateInfoReqParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 同步MDM-项目分期表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface MdmProjectInfoMapper extends BaseMapper<MdmProjectInfo> {

    ProjectInfoDto selectProjectByProjectId(@Param("projectId") String projectId, @Param("version") String version );

    SubProjectInfoDto selectProjectBySubProjectId(@Param("subProjectId") String subProjectId,@Param("version") String version);

    void updateProjectInfoById(@Param("projectUpdateInfoReqParam") ProjectUpdateInfoReqParam projectUpdateInfoReqParam,@Param("extId") String extId,@Param("userVO") CurrentUserVO userVO);

    void updateSubProjectInfoById(@Param("subProjectUpdateInfoReqParam") SubProjectUpdateInfoReqParam subProjectUpdateInfoReqParam, @Param("extId") String extId,@Param("userVO") CurrentUserVO userVO);

    ProjectAndExendInfoDto selectProjectExtendIdByProjectId(@Param("projectId") String projectId);

    SubProjectAndExendInfoDto selectSubProjectExtendIdBySubProjectId(@Param("subProjectId") String subProjectId);

	/**
	 * @Title: deleteAll 
	 * @Description: TODO(这里用一句话描述这个方法的作用) void
	 * @author liyongxu
	 * @date 2019年5月29日 下午5:32:04 
	*/
	@Update("truncate table mdm_project_info")
	void deleteAll();

	/**
	 * @Title: insertMdmProjectInfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mdmProjectInfoList void
	 * @author liyongxu
	 * @date 2019年5月29日 下午8:36:07 
	*/
	void insertMdmProjectInfoList(List<MdmProjectInfoBo> mdmProjectInfoList);

	/**
	 * @Title: deleteBySid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param dataSid void
	 * @author liyongxu
	 * @date 2019年5月30日 上午10:45:14 
	*/
	void deleteBySid(@Param(value = "fdSid")String dataSid);

	/**
	 * @Title: selectBySid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param string void
	 * @author liyongxu
	 * @return 
	 * @date 2019年5月30日 上午11:13:51 
	*/
	MdmProjectInfo selectBySid(@Param(value = "sid")String sid);

	List<MdmProjectInfo> selectSubProjectBySubId(@Param(value = "subId") String subProjectId);

	ProjectAndExtendInfoDto selectProjectByExtendId(@Param(value = "extendId") String extendId);

	SubProjectAndExtendInfoDto selectSubProjectByExtendId(@Param(value = "extendId") String extendId);

	/**
	 * @Title: selectProjectSubInfo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectId
	 * @return List<MdmProjectInfo>
	 * @author liyongxu
	 * @date 2019年6月22日 下午7:33:07 
	*/
	List<MdmProjectInfo> selectProjectSubInfo(@Param(value = "projectId")String projectId);
}
