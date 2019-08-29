package com.tahoecn.bo.controller.webapi;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.model.dto.ProductTypeDto;
import com.tahoecn.bo.model.vo.ProductTypeInfoVo;
import com.tahoecn.bo.model.vo.ProductTypeVo;
import com.tahoecn.bo.service.ProductTypeService;
import com.tahoecn.core.json.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName：ProductTypeController
 * @Description：业态信息API
 * @Author zewenzhang
 * @Date 2019/8/29 14:35
 * @Version 1.0.0
 */
@Api(tags = "业态信息API",value = "业态信息API")
@RestController
@RequestMapping(value = "/api/productType")
public class ProductTypeController {

    private Logger logger = LoggerFactory.getLogger(ProductTypeController.class);

    @Autowired
    private ProductTypeService productTypeService;

    @ApiOperation(value = "查询获取业态数据", notes = "查询获取业态数据")
    @RequestMapping(value = "/getProductTypeInfoList", method = RequestMethod.POST)
    public JSONResult<ProductTypeInfoVo> getProductTypeInfoList(
            @ApiParam(name="productTypeId", value="业态id(不传为全部信息)", required=false)
            @RequestParam(value = "productTypeId", defaultValue = "", required=false) String productTypeId) {
        JSONResult jsonResult = new JSONResult();

        try {
            List<ProductTypeDto> productTypeList = productTypeService.findProductTypeList(productTypeId);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

            ProductTypeVo productTypeVo = new ProductTypeVo();
            productTypeVo.setTypes(productTypeList);
            jsonResult.setData(productTypeVo);
        } catch (Exception e) {
            logger.error("########## getProductTypeList  error #########",e.getMessage(),e);
            jsonResult.setCode(CodeEnum.INTERNAL_ERROR.getKey());
            jsonResult.setMsg(CodeEnum.INTERNAL_ERROR.getValue());
        }

        return jsonResult;
    }

}
