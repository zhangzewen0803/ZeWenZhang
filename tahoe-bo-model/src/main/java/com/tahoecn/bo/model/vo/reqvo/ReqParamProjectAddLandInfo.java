
package com.tahoecn.bo.model.vo.reqvo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目下添加地块信息",description="项目下添加地块信息")
public class ReqParamProjectAddLandInfo {

    @ApiModelProperty(name="versionId", value="版本id", required=true)
    private String versionId;

    @ApiModelProperty(name="landId", value="土地id", required=true)
    private String landId;

    @ApiModelProperty(name="coverArea", value="建筑占地面积", required=true)
    private String coverArea;

    @ApiModelProperty(name="upLandArea", value="地上面积（建筑计容面积）", required=false)
    private String upLandArea;

    @ApiModelProperty(name="downLandArea", value="地下面积（建筑计容面积）", required=false)
    private String downLandArea;

    @ApiModelProperty(name="landGetPrice", value="土地获取价款", required=true)
    private String landGetPrice;

    @ApiModelProperty(name="takeByLandUseArea", value="(总)代征用地面积", required=true)
    private String takeByLandUseArea;

}