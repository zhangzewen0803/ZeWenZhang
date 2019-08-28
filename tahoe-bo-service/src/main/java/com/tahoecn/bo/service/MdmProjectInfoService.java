package com.tahoecn.bo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.bo.ProjectInfoBo;
import com.tahoecn.bo.model.bo.SubProjectInfoBo;
import com.tahoecn.bo.model.dto.ProjectAndExendInfoDto;
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.model.vo.PartitionInfoVo;
import com.tahoecn.bo.model.vo.ProjectInfoVo;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.core.json.JSONResult;

import java.util.List;

/**
 * <p>
 * 同步MDM-项目分期表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface MdmProjectInfoService extends IService<MdmProjectInfo> {

    ProjectInfoBo getProjectByProjectId(String projectId, String version) throws Exception;

    SubProjectInfoBo getProjectBySubProjectId(String subProjectId, String version) throws Exception;

    JSONResult updateProjectInfo(ProjectUpdateInfoReqParam projectUpdateInfoReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult updateSubProjectInfo(SubProjectUpdateInfoReqParam subProjectUpdateInfoReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    ProjectAndExendInfoDto selectProjectExtendIdByProjectId(String projectId) throws Exception;

    /**
     * @Title: deleteAll
     * @Description: TODO(这里用一句话描述这个方法的作用) void
     * @author liyongxu
     * @date 2019年5月29日 下午5:30:08
     */
    void deleteAll();

    /**
     * @param jsonArray void
     * @Title: saveMdmProjectInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月29日 下午7:47:49
     */
    void saveMdmProjectInfo(JSONArray jsonArray);

    /**
     * @param result void
     * @Title: saveMdmProjectInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月30日 上午10:41:38
     */
    void saveMdmProjectInfo(JSONObject result);

    /**
     * @param dataSid void
     * @Title: deleteMdmProjectInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月30日 上午10:41:45
     */
    void deleteMdmProjectInfo(String dataSid);

    /**
     * @param result void
     * @Title: updateMdmProjectInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月30日 上午10:41:50
     */
    void updateMdmProjectInfo(JSONObject result);

    JSONResult updateProjectToNewVersInfo(ProjectUpdateVersReqParam projectUpdateVersReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult deleteSubProjectPart(PartitionInfoVo partitionInfoVo, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult updateSubProjectToNewVersInfo(SubProjectUpdateVersReqParam subProjectUpdateVersReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult getProjectVersionInfo(String subProjectId, JSONResult jsonResult) throws Exception;

    /**
     * @param projectId
     * @return List<MdmProjectInfo>
     * @Title: getProjectSubInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年6月22日 下午7:31:45
     */
    List<MdmProjectInfo> getProjectSubInfo(String projectId);

    /**
     * @param projectId
     * @return List<MdmProjectInfo>
     * @Title: getProjectInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年6月22日 下午7:44:48
     */
    MdmProjectInfo getProjectInfo(String projectId);

    JSONResult updateVersionState(ProjectUpdateVersReqParam projectUpdateVersReqParam, JSONResult jsonResult) throws Exception;

    /**
     * 为每个项目构造一版初始数据
     *
     * @param currentUserVO
     */
    void makeHistoryInit(CurrentUserVO currentUserVO);

    /**
     * 为指定ID构造一版初始数据
     *
     * @param currentUserVO
     * @param projectIds
     * @param landPartStartNo
     */
    void makeHistoryInit(CurrentUserVO currentUserVO, String projectIds, Integer landPartStartNo);

    JSONResult getProjectInfo(String projectId, String versionId, JSONResult jsonResult) throws Exception;

    JSONResult getSubProjectInfo(String subProjectId, String versionId, JSONResult jsonResult) throws Exception;

    JSONResult updateVersionStatus(ProjectLaunchAppReqParam projectLaunchAppReqParam, JSONResult jsonResult) throws Exception;

    JSONResult validateBeforApproval(String versionId, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;
}
