package com.tahoecn.bo.controller.webapi;


import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.model.vo.BasicProjectParamInfoVo;
import com.tahoecn.bo.model.vo.DictionaryInfoVo;
import com.tahoecn.bo.service.BoDictionaryItemService;
import com.tahoecn.bo.service.BoDictionaryTypeService;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "字典API", value = "字典API")
@RestController
@RequestMapping(value = "/api/dictionary")
public class DictionaryInfoApi {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryInfoApi.class);

    @Autowired
    private BoDictionaryTypeService dictionaryTypeService;

    @Autowired
    private BoDictionaryItemService dictionaryItemService;

    @ApiOperation(value = "查询获取字典数据", notes = "查询获取字典数据")
    @RequestMapping(value = "/getDictionaryInfoList", method = RequestMethod.POST)
    public JSONResult<List<DictionaryInfoVo>> getDictionaryInfoList(
            @ApiParam(name="dicCode", value="字典编码(不传为全部信息)", required=false)@RequestParam(value = "dicCode", defaultValue = "", required=false) String dicCode) {
        JSONResult jsonResult = new JSONResult();

        try{
            List<DictionaryInfoVo> dictionaryInfoVos = dictionaryItemService.selectDictionInfo(dicCode);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            jsonResult.setData(dictionaryInfoVos);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


    @ApiOperation(value = "查询项目基本参数数据字典", notes = "查询项目基本参数数据字典")
    @RequestMapping(value = "/getBasicProjectInfoList", method = RequestMethod.POST)
    public JSONResult<List<BasicProjectParamInfoVo>> getBasicProjectInfoList() {
        JSONResult jsonResult = new JSONResult();

        try{
            List<BasicProjectParamInfoVo> basicProjectParamInfoVos = dictionaryItemService.getBasicProjectInfoList();
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            jsonResult.setData(basicProjectParamInfoVos);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }


}
