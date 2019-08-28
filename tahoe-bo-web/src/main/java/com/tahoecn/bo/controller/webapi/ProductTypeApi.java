package com.tahoecn.bo.controller.webapi;


import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.model.dto.ProductTypeInfoDto;
import com.tahoecn.bo.model.vo.ProductTypeInfoVo;
import com.tahoecn.bo.service.BoProductTypeService;
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

@Api(tags = "业态API", value = "业态API")
@RestController
@RequestMapping(value = "/api/type")
public class ProductTypeApi {

    private static final Logger logger = LoggerFactory.getLogger(ProductTypeApi.class);

    @Autowired
    private BoProductTypeService productTypeService;

    @ApiOperation(value = "查询获取业态数据", notes = "查询获取业态数据")
    @RequestMapping(value = "/getProductTypeInfoList", method = RequestMethod.POST)
    public JSONResult<ProductTypeInfoVo> getProductTypeInfoList(
            @ApiParam(name="productTypeId", value="业态id(不传为全部信息)", required=false)@RequestParam(value = "productTypeId", defaultValue = "", required=false) String productTypeId) {
        JSONResult jsonResult = new JSONResult();

        try{
            List<ProductTypeInfoDto> productTypeInfoDtos = productTypeService.selectProductTypeInfoList(productTypeId);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            ProductTypeInfoVo productTypeInfoVo = new ProductTypeInfoVo();
            productTypeInfoVo.setTypes(productTypeInfoDtos);
            jsonResult.setData(productTypeInfoVo);
        }catch (Exception e){
            logger.error("##########  getProjectInfo error  ########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

}
