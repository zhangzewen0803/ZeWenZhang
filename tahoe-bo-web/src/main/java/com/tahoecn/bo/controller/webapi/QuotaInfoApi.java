package com.tahoecn.bo.controller.webapi;


import com.tahoecn.core.json.JSONResult;
import com.tahoecn.bo.model.vo.ConditionQuotaVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "面积指标API", value = "面积指标API")
@RestController
@RequestMapping(value = "/api/measureQuota")
public class QuotaInfoApi {

    @ApiOperation(value = "查询项目规划指标", notes = "查询项目规划指标")
    @RequestMapping(value = "/getQuotaInfoByProject", method = RequestMethod.POST)
    public JSONResult<ConditionQuotaVo> getQuotaInfoByProject() {
        JSONResult jsonResult = new JSONResult();


        return jsonResult;
    }


    @ApiOperation(value = "查询项目分期指标信息", notes = "查询项目分期指标信息")
    @RequestMapping(value = "/getQuotaInfoByProjectStages", method = RequestMethod.POST)
    public JSONResult<ConditionQuotaVo> getQuotaInfoByProjectStages() {
        JSONResult jsonResult = new JSONResult();


        return jsonResult;
    }

}
