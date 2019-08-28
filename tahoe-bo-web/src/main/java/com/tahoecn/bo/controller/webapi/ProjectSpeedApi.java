package com.tahoecn.bo.controller.webapi;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.vo.ProjectSpeedUpdateReqParam;
import com.tahoecn.bo.model.vo.reqvo.ProjectUpdateInfoReqParam;
import com.tahoecn.bo.service.BoBuildingService;
import com.tahoecn.bo.service.BoBuildingSpeedService;
import com.tahoecn.bo.service.BoProjectQuotaExtendService;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "工程进度填报API", value = "工程进度填报API")
@RestController
@RequestMapping(value = "/api/speed")
public class ProjectSpeedApi extends TahoeBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectSpeedApi.class);

    @Autowired
    private BoBuildingSpeedService buildingSpeedService;

    @ApiOperation(value = "工程进度填报查询", notes = "工程进度填报查询")
    @RequestMapping(value = "/getProjectSpeedInfo", method = RequestMethod.POST)
    public JSONResult<ProjectSpeedUpdateReqParam> getProjectSpeedInfo(@ApiParam(name="projectId", value="项目id", required=true)@RequestParam(value = "projectId", defaultValue = "", required=true) String projectId,
                                          @ApiParam(name="speedTime", value="进度时间", required=true)@RequestParam(value = "speedTime", defaultValue = "", required=true) String speedTime) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = buildingSpeedService.getProjectSpeedInfo(projectId, speedTime, jsonResult);
        }catch (Exception e){
            logger.error("##########  getProjectSpeedInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

    @ApiOperation(value = "工程进度填报修改", notes = "工程进度填报修改")
    @RequestMapping(value = "/updateProjectSpeedInfo", method = RequestMethod.POST)
    public JSONResult updateProjectSpeedInfo(@RequestBody ProjectSpeedUpdateReqParam projectSpeedUpdateReqParam) {
        JSONResult jsonResult = new JSONResult();

        try{
            jsonResult = buildingSpeedService.updateProjectSpeedInfo(projectSpeedUpdateReqParam, getCurrentUser(), jsonResult);
        }catch (Exception e){
            logger.error("##########  getProjectSpeedInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

}
