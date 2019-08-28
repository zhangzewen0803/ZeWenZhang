package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="字典信息",description="字典信息")
public class DictionaryInfoVo {

    @ApiModelProperty(value="字典编码", name="dicCode")
    private String dicCode;

    @ApiModelProperty(value="字典名称", name="dicName")
    private String dicName;

    @ApiModelProperty(value="字典item", name="dicName")
    private List<DictionaryItemInfoVo> dicItem;

    @Data
    @ApiModel(value="字典item信息",description="字典item信息")
    public static class DictionaryItemInfoVo{

        @ApiModelProperty(value="字典item编码", name="dicItemCode")
        private String dicItemCode;

        @ApiModelProperty(value="字典item名称", name="dicItemName")
        private String dicItemName;
    }

}
