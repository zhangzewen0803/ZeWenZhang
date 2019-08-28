package com.tahoecn.bo.model.excelDTO;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderInfo implements Serializable {

    @Excel(name = "订单编号", width = 25, needMerge = true)
    private String orderId;

    @Excel(name = "用户编号", needMerge = true)
    private int userId;

    @Excel(name = "创建时间", width = 40, needMerge = true,databaseFormat="yyyyMMddHHmmss",format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "订单金额", type = 10, needMerge = true)
    private double amount;

    @ExcelCollection(name = "订单商品")
    private List<OrderGoods> goods;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<OrderGoods> getGoods() {
        return goods;
    }

    public void setGoods(List<OrderGoods> goods) {
        this.goods = goods;
    }
}
