package com.tahoecn.bo.model.vo;

import com.tahoecn.bo.model.dto.ProductTypeInfoDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="业态信息",description="业态信息")
public class ProductTypeInfoVo {

    List<ProductTypeInfoDto> types;

}
