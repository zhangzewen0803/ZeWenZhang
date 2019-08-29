package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * @ClassName：ProductType
 * @Description：业态表
 * @Author zewenzhang
 * @Date 2019/8/29 13:51
 * @Version 1.0.0
 */
public class ProductType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id",type = IdType.INPUT)
    private String id;

    /**
     * 业态CODE
     */
    private String code;

    /**
     * 业态名称
     */
    private String name;

    /**
     *父ID
     */
    private String parentId;

    /**
     *排序号
     */
    private Integer sortNo;

    /**
     *是否删除（0-否；1-是）
     */
    private Integer isDelete;

    /**
     *是否禁用（0-否；1-是）
     */
    private Integer isDisable;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDisable() {
        return isDisable;
    }

    public void setIsDisable(Integer isDisable) {
        this.isDisable = isDisable;
    }

    @Override
    public String toString() {
        return "ProductType{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", sortNo=" + sortNo +
                ", isDelete=" + isDelete +
                ", isDisable=" + isDisable +
                '}';
    }
}
