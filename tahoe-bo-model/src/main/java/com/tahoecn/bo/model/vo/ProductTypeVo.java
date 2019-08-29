package com.tahoecn.bo.model.vo;

import com.tahoecn.bo.model.dto.ProductTypeDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：ProductTypeVo
 * @Description：TODO
 * @Author zewenzhang
 * @Date 2019/8/29 14:02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "业态信息",description = "业态信息")
public class ProductTypeVo  {

    List<ProductTypeDto> types;

}
