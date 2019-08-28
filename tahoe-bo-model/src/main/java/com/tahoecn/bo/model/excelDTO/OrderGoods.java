package com.tahoecn.bo.model.excelDTO;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;

public class OrderGoods implements Serializable {

    @Excel(name = "商品编号",width=25)
    private String goodsId;

    @Excel(name = "商品图片", type = 2 ,width = 40,height = 20)
    private String goodsPic;

    @Excel(name = "商品销售数量",width=25)
    private int goodsNum;

    @Excel(name = "商品单价",type = 10)
    private double price;



    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGoodsPic() {
        return goodsPic;
    }

    public void setGoodsPic(String goodsPic) {
        this.goodsPic = goodsPic;
    }
}
