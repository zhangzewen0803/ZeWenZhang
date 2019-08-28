package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 工规证
 *
 * @author panglx
 * @date 2019/6/4
 */
@ApiModel("工规证")
public class GetProjectPlanCardListVO implements Serializable {

    @ApiModelProperty("工规证ID")
    private String id;

    @ApiModelProperty("工规证号")
    private String planCode;

    @ApiModelProperty("拿证时间，格式：yyyy-MM-dd，如：2019-06-04")
    private String replyTime;

    @ApiModelProperty("楼栋列表")
    private List<Building> buildingList;

    @ApiModel("楼栋")
    public static class Building implements Serializable{

        @ApiModelProperty("主键ID")
        private String id;

        @ApiModelProperty("楼栋名称")
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Building{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public List<Building> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(List<Building> buildingList) {
        this.buildingList = buildingList;
    }

    @Override
    public String toString() {
        return "GetGovPlanCardListVO{" +
                "id='" + id + '\'' +
                ", planCode='" + planCode + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", buildingList=" + buildingList +
                '}';
    }
}
