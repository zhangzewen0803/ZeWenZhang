package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 指标VO
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value="面积指标数据", description="面积指标数据")
public class GetAreaQuotaListVO implements Serializable{

    @ApiModelProperty(value="指标头信息/指标定义信息")
    private List<QuotaHeadVO> head;

    @ApiModelProperty(value="指标键值")
    private List<QuotaBodyVO> body;

    public List<QuotaHeadVO> getHead() {
        return head;
    }

    public void setHead(List<QuotaHeadVO> head) {
        this.head = head;
    }

    public List<QuotaBodyVO> getBody() {
        return body;
    }

    public void setBody(List<QuotaBodyVO> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "GetAreaQuotaListVO{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}

