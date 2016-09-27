package com.tocong.tack.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 此类描述的是：商品一级分
 * 作者：肖雷
 * 时间：2016/7/7 19:22
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class FoodType implements Serializable {
    private int id_type;
    private String type_name;
    private int type_father;
    private int type_weight;
    private int is_online;
    private List<FoodInfo> foodInfoList;

    public List<FoodInfo> getFoodInfoList() {
        return foodInfoList;
    }

    public void setFoodInfoList(List<FoodInfo> foodInfoList) {
        this.foodInfoList = foodInfoList;
    }

    public int getId_type() {
        return id_type;
    }

    public void setId_type(int id_type) {
        this.id_type = id_type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getType_father() {
        return type_father;
    }

    public void setType_father(int type_father) {
        this.type_father = type_father;
    }

    public int getType_weight() {
        return type_weight;
    }

    public void setType_weight(int type_weight) {
        this.type_weight = type_weight;
    }

    public int is_online() {
        return is_online;
    }

    public void set_online(int is_online) {
        this.is_online = is_online;
    }

    @Override
    public String toString() {
        return this.id_type+","+this.type_name+","+this.type_weight+","+this.type_father+","+this.is_online+",";
    }
}
