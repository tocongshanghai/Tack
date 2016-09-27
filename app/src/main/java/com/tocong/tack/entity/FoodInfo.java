package com.tocong.tack.entity;

import java.io.Serializable;

/**
 * 此类描述的是：商品信息
 * 作者：肖雷
 * 时间：2016/7/7 19:29
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class FoodInfo implements Serializable{
    private int id_food;
    private String food_name;
    /**未分拣数量*/
    private int noIsPickCount;
    /**全部数量*/
    private int isPickCount;
    private String begin_time;
    private String end_time;
    /**订单数量*/
    private  int order_number;
    /*
    * 一维码
    * */
    private String food_bar_code;

    public String getFood_bar_code() {
        return food_bar_code;
    }

    public void setFood_bar_code(String food_bar_code) {
        this.food_bar_code = food_bar_code;
    }

    public  int getOrder_number(){return  order_number;};

    public void setOrder_number(int orderNumber){this.order_number = orderNumber;}


    private String sale_describe;

    public String getSale_describe() {
        return sale_describe;
    }

    public void setSale_describe(String sale_describe) {
        this.sale_describe = sale_describe;
    }

    public int getId_food() {
        return id_food;
    }

    public void setId_food(int id_food) {
        this.id_food = id_food;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getNoIsPickCount() {
        return noIsPickCount;
    }

    public void setNoIsPickCount(int noIsPickCount) {
        this.noIsPickCount = noIsPickCount;
    }

    public int getPickCount() {
        return isPickCount;
    }

    public void setPickCount(int isPickCount) {
        this.isPickCount = isPickCount;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
