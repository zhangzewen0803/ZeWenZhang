package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 业态数据
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel(value="业态数据", description="业态数据")
public class GetProductTypeListVO implements Serializable{


    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业态CODE")
    private String code;

    @ApiModelProperty(value = "业态名称")
    private String name;

    @ApiModelProperty(value = "ID全路径")
    private String idTree;

    @ApiModelProperty(value = "业态CODE全路径，/分隔")
    private String codeTree;

    @ApiModelProperty(value = "业态名称全路径，/分隔")
    private String nameTree;

    @ApiModelProperty(value = "父ID")
    private String parentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdTree() {
        return idTree;
    }

    public void setIdTree(String idTree) {
        this.idTree = idTree;
    }

    public String getCodeTree() {
        return codeTree;
    }

    @Override
    public String toString() {
        return "GetProductTypeListVO{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", idTree='" + idTree + '\'' +
                ", codeTree='" + codeTree + '\'' +
                ", nameTree='" + nameTree + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    public void setCodeTree(String codeTree) {
        this.codeTree = codeTree;
    }

    public String getNameTree() {
        return nameTree;
    }

    public void setNameTree(String nameTree) {
        this.nameTree = nameTree;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

