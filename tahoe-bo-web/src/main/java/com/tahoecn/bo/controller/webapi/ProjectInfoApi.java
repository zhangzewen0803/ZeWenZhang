package com.tahoecn.bo.controller.webapi;


import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.vo.PartitionInfoVo;
import com.tahoecn.bo.model.vo.ProjectInfoVo;
import com.tahoecn.bo.model.vo.SubProjectInfoVo;
import com.tahoecn.bo.model.vo.VersionInfoVo;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.bo.service.MdmProjectInfoService;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目分期API", value = "项目分期API")
@RestController
@RequestMapping(value = "/api/project")
public class ProjectInfoApi extends TahoeBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectInfoApi.class);

    @Autowired
    private MdmProjectInfoService projectInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "查询项目信息接口", notes = "查询项目信息接口")
    @RequestMapping(value = "/getProjectInfo", method = RequestMethod.POST)
    public JSONResult<ProjectInfoVo> getProjectInfo(
            @ApiParam(name="projectId", value="项目id", required=false)@RequestParam(value = "projectId", defaultValue = "", required=false) String projectId,
            @ApiParam(name="versionId", value="版本id", required=false)@RequestParam(value = "versionId", defaultValue = "", required=false) String versionId) {

        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectInfoService.getProjectInfo(projectId, versionId, jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


    @ApiOperation(value = "更新项目信息", notes = "更新项目信息")
    @RequestMapping(value = "/updateProjectInfo", method = RequestMethod.POST)
    public JSONResult updateProjectInfo(@RequestBody ProjectUpdateInfoReqParam projectUpdateInfoReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectInfoService.updateProjectInfo(projectUpdateInfoReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询项目分期基础信息接口", notes = "查询项目分期基础信息接口")
    @RequestMapping(value = "/getSubProjectInfo", method = RequestMethod.POST)
    public JSONResult<SubProjectInfoVo> getSubProjectInfo(
            @ApiParam(name="subProjectId", value="项目分期id", required=false)@RequestParam(value = "subProjectId", defaultValue = "", required=false) String subProjectId,
            @ApiParam(name="versionId", value="版本id", required=false)@RequestParam(value = "versionId", defaultValue = "", required=false) String versionId) {

        JSONResult jsonResult = new JSONResult();

        try {

            jsonResult = projectInfoService.getSubProjectInfo(subProjectId, versionId, jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "更新项目分期信息", notes = "更新项目分期信息")
    @RequestMapping(value = "/updateSubProjectInfo", method = RequestMethod.POST)
    public JSONResult<SubProjectInfoVo> updateSubProjectInfo(@RequestBody SubProjectUpdateInfoReqParam subProjectUpdateInfoReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectInfoService.updateSubProjectInfo(subProjectUpdateInfoReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "分期分区删除(已废除)", notes = "分期分区删除(已废除)")
    @RequestMapping(value = "/deleteSubProjectPart", method = RequestMethod.POST)
    public JSONResult<SubProjectInfoVo> deleteSubProjectPart(@RequestBody PartitionInfoVo partitionInfoVo) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectInfoService.deleteSubProjectPart(partitionInfoVo, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "项目生成新版本", notes = "项目生成新版本")
    @RequestMapping(value = "/updateProjectToNewVersInfo", method = RequestMethod.POST)
    public JSONResult<SubProjectInfoVo> updateProjectToNewVersInfo(@RequestBody ProjectUpdateVersReqParam projectUpdateVersReqParam) {
        JSONResult jsonResult = new JSONResult();

        String key = RedisConstants.PROJECT_CREATE_VERSION_LOCK + projectUpdateVersReqParam.getProjectId();
        //禁止重复创建
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }

        try{

            jsonResult = projectInfoService.updateProjectToNewVersInfo(projectUpdateVersReqParam, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }finally {
            redisTemplate.delete(key);
        }

        return jsonResult;
    }

    @ApiOperation(value = "项目分期生成新版本", notes = "项目分期生成新版本")
    @RequestMapping(value = "/updateSubProjectToNewVersInfo", method = RequestMethod.POST)
    public JSONResult<SubProjectInfoVo> updateSubProjectToNewVersInfo(@RequestBody SubProjectUpdateVersReqParam subProjectUpdateVersReqParam) {
        JSONResult jsonResult = new JSONResult();

        String key = RedisConstants.SUB_PROJECT_CREATE_VERSION_LOCK + subProjectUpdateVersReqParam.getSubProjectId();
        //禁止重复创建
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }

        try{
            jsonResult = projectInfoService.updateSubProjectToNewVersInfo(subProjectUpdateVersReqParam, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }finally {
            redisTemplate.delete(key);
        }

        return jsonResult;
    }

    @ApiOperation(value = "项目OR分期获取版本信息", notes = "项目OR分期获取版本信息")
    @RequestMapping(value = "/getProjectVersionInfo", method = RequestMethod.POST)
    public JSONResult<VersionInfoVo> getProjectVersionInfo(@ApiParam(name="projectId", value="项目OR分期id", required=true)@RequestParam(value = "projectId", defaultValue = "", required=true) String projectId) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectInfoService.getProjectVersionInfo(projectId, jsonResult);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


    @ApiOperation(value = "项目OR分期修改审批状态", notes = "项目OR分期修改审批状态")
    @RequestMapping(value = "/projectLaunchApproval", method = RequestMethod.POST)
    public JSONResult<VersionInfoVo> projectLaunchApproval(@RequestBody ProjectUpdateVersReqParam projectUpdateVersReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectInfoService.updateVersionState(projectUpdateVersReqParam,  jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "项目OR分期修改审批状态(新)", notes = "项目OR分期修改审批状态(新)")
    @RequestMapping(value = "/projectToLaunchApproval", method = RequestMethod.POST)
    public JSONResult<VersionInfoVo> projectToLaunchApproval(@RequestBody ProjectLaunchAppReqParam projectLaunchAppReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectInfoService.updateVersionStatus(projectLaunchAppReqParam,  jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "项目OR分期进入审批前校验(新)", notes = "项目OR分期进入审批前校验(新)")
    @RequestMapping(value = "/validateBeforApproval", method = RequestMethod.POST)
    public JSONResult validateBeforApproval(@ApiParam(name="versionId", value="版本Id", required=true)@RequestParam(value = "versionId", defaultValue = "", required=true) String versionId) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectInfoService.validateBeforApproval(versionId, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

}
