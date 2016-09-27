package com.tocong.tack.entity;

import java.io.Serializable;

/**
 * 此类描述的是：
 * 作者：肖雷
 * 时间：2016/7/8 14:53
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class OrderDetails implements Serializable {
    private int id_detail_order;
    private int id_order;
    private int order_number;
    private String process_remark;
    private int is_pick;
    private int id_food;
    private String food_name;
    private String sale_describe;
    private int pick_num;
    private String tp_seq;
    private String tp_seq_str;
    private FoodInfo food_info;

    public FoodInfo getFood_info() {
        return food_info;
    }

    public void setFood_info(FoodInfo food_info) {
        this.food_info = food_info;
    }


    public String getTp_seq() {
        return tp_seq;
    }

    public void setTp_seq(String tp_seq) {
        this.tp_seq = tp_seq;
    }

    public String getTp_seq_str() {
        return tp_seq_str;
    }

    public void setTp_seq_str(String tp_seq_str) {
        this.tp_seq_str = tp_seq_str;
    }

    public int getPick_num() {
        return pick_num;
    }

    public void setPick_num(int pick_num) {
        this.pick_num = pick_num;
    }

    public String getSale_describe() {
        return sale_describe;
    }

    public void setSale_describe(String sale_describe) {
        this.sale_describe = sale_describe;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getId_food() {
        return id_food;
    }

    public void setId_food(int id_food) {
        this.id_food = id_food;
    }

    public int is_pick() {
        return is_pick;
    }

    public void set_pick(int is_pick) {
        this.is_pick = is_pick;
    }

    public int getId_detail_order() {
        return id_detail_order;
    }

    public void setId_detail_order(int id_detail_order) {
        this.id_detail_order = id_detail_order;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getOrder_number() {
        return order_number;
    }

    public void setOrder_number(int order_number) {
        this.order_number = order_number;
    }

    public String getProcess_remark() {
        return process_remark;
    }

    public void setProcess_remark(String process_remark) {
        this.process_remark = process_remark;
    }

}
