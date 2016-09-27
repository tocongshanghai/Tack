package com.tocong.tack.entity;

/**
 * 此类描述的是：
 * 作者：肖雷
 * 时间：2016/7/11 10:23
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class UserInfo {
    private int id_user;
    private String contact_user;
    private String require_time_str;

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getContact_user() {
        return contact_user;
    }

    public void setContact_user(String contact_user) {
        this.contact_user = contact_user;
    }

    public String getRequire_time_str() {
        return require_time_str;
    }

    public void setRequire_time_str(String require_time_str) {
        this.require_time_str = require_time_str;
    }
}
