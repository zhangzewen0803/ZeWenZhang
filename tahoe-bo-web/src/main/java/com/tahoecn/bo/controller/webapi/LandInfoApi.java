package com.tahoecn.bo.controller.webapi;


import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.bo.service.BoProjectLandPartMapService;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "地块API", value = "地块API")
@RestController
@RequestMapping(value = "/api/land")
public class LandInfoApi extends TahoeBaseController {

    private static final Logger logger = LoggerFactory.getLogger(LandInfoApi.class);

    @Autowired
    private BoProjectLandPartMapService projectLandPartMapService;

    @ApiOperation(value = "查询项目下宗地信息接口", notes = "查询项目下宗地信息接口")
    @RequestMapping(value = "/getLandByProject", method = RequestMethod.POST)
    public JSONResult<LandQuotaVo> getLandByProject(@ApiParam(name="projectId", value="项目id", required=true)@RequestParam(value = "projectId", defaultValue = "", required=true) String projectId,
                                                    @ApiParam(name="version", value="版本号", required=false)@RequestParam(value = "version", defaultValue = "", required=false) String version,
                                                    @ApiParam(name="isAdopt", value="是否审批通过(有参数为最新审批通过，没有为最新审批未通过)", required=false)@RequestParam(value = "isAdopt", defaultValue = "", required=false) String isAdopt) {
        JSONResult jsonResult = new JSONResult();

        try {
            jsonResult = projectLandPartMapService.selectLandInfoByProjectId(projectId, version, isAdopt, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询项目可使用的宗地信息接口", notes = "查询项目可使用的宗地信息接口")
    @RequestMapping(value = "/getLandIsUseByProject", method = RequestMethod.POST)
    public JSONResult<LandSelInfoVo> getLandIsUseByProject(@ApiParam(name="projectId", value="项目id", required=true)@RequestParam(value = "projectId", defaultValue = "", required=true) String projectId,
                                                           @ApiParam(name="landName", value="地块名称", required=false)@RequestParam(value = "landName", defaultValue = "", required=false) String landName,
                                                           @ApiParam(name="pageNo", value="当前页码", required=true)@RequestParam(value = "pageNo", defaultValue = Constants.PAGE_NUM, required=true) Integer pageNo,
                                                           @ApiParam(name="pageSize", value="分页大小", required=true)@RequestParam(value = "pageSize", defaultValue = Constants.PAGE_SIZE, required=true) Integer pageSize) {
        JSONResult jsonResult = new JSONResult();

        try {
            jsonResult = projectLandPartMapService.selectCanUseLandInfoByProjectId(projectId, landName, pageNo, pageSize, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "添加项目下宗地信息接口", notes = "查询组织架构项目分期信息列表")
    @RequestMapping(value = "/addLandToProject", method = RequestMethod.POST)
    public JSONResult addLandToProject(@RequestBody ProjectAddLandReqParam projectAddLandReqParam) {
        JSONResult jsonResult = new JSONResult();
        try {
            jsonResult = projectLandPartMapService.addLandToProject(projectAddLandReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "删除项目已选择的宗地信息接口", notes = "删除项目已选择的宗地信息接口")
    @RequestMapping(value = "/deleteLandToProject", method = RequestMethod.POST)
    public JSONResult deleteLandToProject(@RequestBody ProjectAddLandReqParam projectAddLandReqParam) {
        JSONResult jsonResult = new JSONResult();
        try {
            jsonResult = projectLandPartMapService.deleteLandToProject(projectAddLandReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


    @ApiOperation(value = "查询分期地块信息接口", notes = "查询分期地块信息接口")
    @RequestMapping(value = "/getLandBySubProject", method = RequestMethod.POST)
    public JSONResult<LandSelInfoVo> getLandBySubProject(@ApiParam(name="subProjectId", value="项目分期id", required=true)@RequestParam(value = "subProjectId", defaultValue = "", required=true) String subProjectId,
                                                         @ApiParam(name="version", value="版本号", required=false)@RequestParam(value = "version", defaultValue = "", required=false) String version,
                                                         @ApiParam(name="isAdopt", value="是否审批通过(有参数为最新审批通过，没有为最新审批未通过)", required=false)@RequestParam(value = "isAdopt", defaultValue = "", required=false) String isAdopt) {
        JSONResult jsonResult = new JSONResult();

        try {
            jsonResult = projectLandPartMapService.selectLandInfoBySubProjectId(subProjectId, version, isAdopt, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询分期可使用的地块信息", notes = "查询分期可使用的地块信息")
    @RequestMapping(value = "/getLandIsUseBySubProject", method = RequestMethod.POST)
    public JSONResult<List<LandBasicInfoVo>> getLandIsUseBySubProject(@ApiParam(name="subProjectId", value="项目分期id", required=true)@RequestParam(value = "subProjectId", defaultValue = "", required=true) String subProjectId) {

        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.selectCanUseLandBySubProject(subProjectId, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "添加分期下地快信息接口", notes = "添加分期下地快信息接口")
    @RequestMapping(value = "/addLandToSubProject", method = RequestMethod.POST)
    public JSONResult addLandToSubProject(@RequestBody SubProjectAddLandReqParam subProjectAddLandReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            projectLandPartMapService.addLandToSubProject(subProjectAddLandReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


    @ApiOperation(value = "删除分期下地块信息", notes = "删除分期下地块信息")
    @RequestMapping(value = "/deleteLandToSubProject", method = RequestMethod.POST)
    public JSONResult deleteLandToSubProject(@RequestBody SubProjectDelLandReqParam subProjectDelLandReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.deleteLandToSubProject(subProjectDelLandReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "更新分期下地块信息", notes = "更新分期下地块信息")
    @RequestMapping(value = "/updateLandToSubProject", method = RequestMethod.POST)
    public JSONResult updateLandToSubProject(@RequestBody SubProjectAddLandReqParam subProjectAddLandReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.updateLandToSubProject(subProjectAddLandReqParam, getCurrentUser(),  jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询指定分期下地块信息详情", notes = "查询指定分期下地块信息详情")
    @RequestMapping(value = "/selectLandDetailsToSubProject", method = RequestMethod.POST)
    public JSONResult<SubProjectLandDetailsVo> selectLandDetailsToSubProject(@ApiParam(name="subProjectId", value="项目分期id", required=true)@RequestParam(value = "subProjectId", defaultValue = "", required=true) String subProjectId,
                                                                             @ApiParam(name="landId", value="地块id", required=true)@RequestParam(value = "landId", defaultValue = "", required=true) String landId) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.selectLandDetailsToSubProject(subProjectId, landId, jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询指定可添加地块信息详情", notes = "查询指定可添加地块信息详情")
    @RequestMapping(value = "/selectCanUseLandDetailsToSubProject", method = RequestMethod.POST)
    public JSONResult<SubProjectLandDetailsVo> selectCanUseLandDetailsToSubProject(@ApiParam(name="subProjectId", value="项目分期id", required=true)@RequestParam(value = "subProjectId", defaultValue = "", required=true) String subProjectId,
                                                                                   @ApiParam(name="landId", value="地块id", required=true)@RequestParam(value = "landId", defaultValue = "", required=true) String landId) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.selectCanUseLandDetailsToSubProject(subProjectId, landId, jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }



    @ApiOperation(value = "查询项目下宗地信息接口(新)", notes = "查询项目下宗地信息接口(新)")
    @RequestMapping(value = "/getLandInfoByProject", method = RequestMethod.POST)
    public JSONResult<LandQuotaVo> getLandInfoByProject(@ApiParam(name="versionId", value="版本id", required=true)@RequestParam(value = "versionId", defaultValue = "", required=true) String versionId) {
        JSONResult jsonResult = new JSONResult();

        try {

            jsonResult = projectLandPartMapService.getLandInfoByProject(versionId, jsonResult);

        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "添加项目下宗地信息接口(新)", notes = "添加项目下宗地信息接口(新)")
    @RequestMapping(value = "/addLandInfoToProject", method = RequestMethod.POST)
    public JSONResult addLandInfoToProject(@RequestBody ReqParamProjectAddLandInfo projectAddLandInfo) {
        JSONResult jsonResult = new JSONResult();
        try {
            jsonResult = projectLandPartMapService.addLandInfoToProject(projectAddLandInfo, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "删除项目已选择的宗地信息接口(新)", notes = "删除项目已选择的宗地信息接口(新)")
    @RequestMapping(value = "/deleteLandInfoToProject", method = RequestMethod.POST)
    public JSONResult deleteLandInfoToProject(@RequestBody ProjectDeleteLandReqParam projectDeleteLandReqParam) {
        JSONResult jsonResult = new JSONResult();
        try {
            jsonResult = projectLandPartMapService.deleteLandInfoToProject(projectDeleteLandReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询分期地块信息接口(新)", notes = "查询分期地块信息接口(新)")
    @RequestMapping(value = "/getLandInfoBySubPro", method = RequestMethod.POST)
    public JSONResult<LandSelInfoVo> getLandInfoBySubPro(@ApiParam(name="versionId", value="版本id", required=true)@RequestParam(value = "versionId", defaultValue = "", required=true) String versionId) {
        JSONResult jsonResult = new JSONResult();

        try {
            jsonResult = projectLandPartMapService.getLandInfoBySubPro(versionId, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询分期下地块信息详情(新)", notes = "查询分期下地块信息详情(新)")
    @RequestMapping(value = "/getLandInfoDetailsToSubProject", method = RequestMethod.POST)
    public JSONResult<LandToSubProInfoVo> getLandInfoDetailsToSubProject(@ApiParam(name="versionId", value="版本id", required=true)@RequestParam(value = "versionId", defaultValue = "", required=true) String versionId,
                                                                         @ApiParam(name="landId", value="地块id", required=true)@RequestParam(value = "landId", defaultValue = "", required=true) String landId) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.getLandInfoDetailsToSubProject(versionId, landId, jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询分期可使用的地块信息(新)", notes = "查询分期可使用的地块信息(新)")
    @RequestMapping(value = "/getLandInfoCanUseBySubProject", method = RequestMethod.POST)
    public JSONResult<List<LandBasicInfoVo>> getLandInfoCanUseBySubProject(@ApiParam(name="versionId", value="版本id", required=true)@RequestParam(value = "versionId", defaultValue = "", required=true) String versionId) {

        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.getLandInfoCanUseBySubProject(versionId, jsonResult);
        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "查询指定可添加地块信息详情(新)", notes = "查询指定可添加地块信息详情(新)")
    @RequestMapping(value = "/getCanUseLandDetailsToSubProject", method = RequestMethod.POST)
    public JSONResult<LandToSubProInfoVo> getCanUseLandDetailsToSubProject(@ApiParam(name="subProjectId", value="分期id", required=true)@RequestParam(value = "subProjectId", defaultValue = "", required=true) String subProjectId,
                                                                           @ApiParam(name="landId", value="地块id", required=true)@RequestParam(value = "landId", defaultValue = "", required=true) String landId) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.getCanUseLandDetailsToSubProject(subProjectId, landId, jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "添加分期下地快信息接口(新)", notes = "添加分期下地快信息接口(新)")
    @RequestMapping(value = "/addLandInfoToSubProject", method = RequestMethod.POST)
    public JSONResult addLandInfoToSubProject(@RequestBody LandAddSubProInfoBody landAddSubProInfoBody) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectLandPartMapService.addLandInfoToSubProject(landAddSubProInfoBody, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  getLandIsUseBySubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "更新分期下地块信息(新)", notes = "更新分期下地块信息(新)")
    @RequestMapping(value = "/updateLandInfoToSubProject", method = RequestMethod.POST)
    public JSONResult updateLandInfoToSubProject(@RequestBody LandAddSubProInfoBody landAddSubProInfoBody) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = projectLandPartMapService.updateLandInfoToSubProject(landAddSubProInfoBody, getCurrentUser(),  jsonResult);
        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "删除分期下地块信息(新)", notes = "删除分期下地块信息(新)")
    @RequestMapping(value = "/deleteLandInfoToSubProject", method = RequestMethod.POST)
    public JSONResult deleteLandInfoToSubProject(@RequestBody ProjectDeleteLandReqParam projectDeleteLandReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{

            jsonResult = projectLandPartMapService.deleteLandInfoToSubProject(projectDeleteLandReqParam, getCurrentUser(), jsonResult);

        }catch (Exception e){
            logger.error("##########  deleteLandToSubProject error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


}
