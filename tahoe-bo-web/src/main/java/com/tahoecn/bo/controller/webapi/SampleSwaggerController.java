package com.tahoecn.bo.controller.webapi;

import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@Api(tags = "Swagger接口描述Demos", value = "Swagger接口描述Demos")
@RestController
@RequestMapping(value = "/api")
public class SampleSwaggerController {

    @ApiOperation(value = "演示接口名称", notes = "演示接口描述")
    @ApiImplicitParams({ @ApiImplicitParam(name = "infoId", value = "变量注释", required = true, dataType = "String") })
    @RequestMapping(value = "/info", method = { RequestMethod.POST, RequestMethod.GET })
    public JSONResult demo(String infoId) {
        JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(0);
        jsonResult.setMsg("SUCCESS");
        jsonResult.setData(infoId);

        return jsonResult;
    }
}
