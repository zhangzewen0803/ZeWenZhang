package com.tahoecn.bo.model.vo.reqvo;

import com.tahoecn.bo.model.vo.LandBasicAreaInfoVo;
import com.tahoecn.bo.model.vo.SubProjectProductTypeVo;
import com.tahoecn.bo.model.vo.SubProjectQuoteTypeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
@ApiModel(value="项目分期添加地块信息",description="项目分期修改基本信息")
public class SubProjectAddLandReqParam extends LandBasicAreaInfoVo {

    @ApiModelProperty(name="subProjectId", value="项目分期id", required=true)
    private String subProjectId;

    @ApiModelProperty(name="isDev", value="是否全部开发（0-否；1-是）", required=true)
    private Integer isDev;

    @ApiModelProperty(value="土地Id", name="landId")
    private String landId;

    @ApiModelProperty(name="quoteTypeList", value="项目分期业态指标详情", required=true)
    private List<SubProjectProductTypeVo> quoteTypeList;

}
