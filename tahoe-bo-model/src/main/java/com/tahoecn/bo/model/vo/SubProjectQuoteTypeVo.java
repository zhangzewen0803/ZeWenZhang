package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目分期添加地块指标详情信息",description="项目分期添加地块指标详情信息")
public class SubProjectQuoteTypeVo {

    @ApiModelProperty(value="指标分组映射Id", name="groupMapId")
    private String groupMapId;

    @ApiModelProperty(value="指标数据Id", name="typeQuoteId")
    private String typeQuoteId;

    @ApiModelProperty(value="指标Id", name="quoteId")
    private String quoteId;

    @ApiModelProperty(value="指标Code", name="quoteCode")
    private String quoteCode;

    @ApiModelProperty(value="指标值", name="quoteValue")
    private String quoteValue;

    @ApiModelProperty(value="指标名称", name="quoteFormatName")
    private String quoteFormatName;

    @ApiModelProperty(value="指标类型", name="quoteFormatType")
    private String quoteFormatType;

    @ApiModelProperty(value="值类型(TEXT、NUMBER、DATE、GROUP)", name="quoteFormatValueType")
    private String quoteFormatValueType;

    @ApiModelProperty(value="数据格式，根据value_type对应不同的含义   value_type=TEXT时，data_format无意义, value_type=NUMBER时  data_format=2表示小数点后保留2位, value_type=DATE时  data_format=yyyy-MM-dd表示日期的格式 , value_type=GROUP时 data_format=DIC_CODE表示字典CODE值", name="quoteFormatData")
    private String quoteFormatData;

    @ApiModelProperty(value="单位", name="quoteFormatUnit")
    private String quoteFormatUnit;

    @ApiModelProperty(value="是否汇总", name="quoteFormatIsGather")
    private Integer quoteFormatIsGather;

    @ApiModelProperty(value="是否可编辑", name="quoteFormatIsEdit")
    private Integer quoteFormatIsEdit;

    @ApiModelProperty(value="公式", name="quoteFormatFormat")
    private String quoteFormatFormat;

    @ApiModelProperty(value="正则", name="quoteFormatRegexps")
    private String quoteFormatRegexps;

    @ApiModelProperty(value="字段跨行", name="quoteFormatColspan")
    private Integer quoteFormatColspan;

}
