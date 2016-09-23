package com.tocong.tack.util;

/**
 * 此类描述的是：常量
 * 作者：肖雷
 * 时间：2016/5/11 14:16
 * 公司：上海家乐宝真好电子商务有限公司
 */
public interface Constants {

    /** 肖雷 */
//	 String URL = "http://192.168.1.68:8080/jialebao/";
    /** 俞文彬 */
    //String URL = "http://192.168.1.102:8080/jialebao/";
    /** 李博凯 */
//	 String URL = "http://192.168.1.211:80/jialebao/";
    /** 陆麒羽 */
	// String URL = "http://192.168.1.20:8080/jialebao/";
    /**
     * 本地计算机
     */
//    String URL = "http://192.168.1.83:8080/jialebao/";
    /**
     * 测试库
     */
    String URL = "http://121.40.155.207/";
    /** 正式库 */
//    String URL = "http://www.jialebao.cc/";//121.40.205.167

    /**
     * 连接服务器失败时提示信息
     */
    String ERRORMSG = "网络状态欠佳，请稍后再试";
    /**
     * 连接服务器状态码标示
     */
    String STATUS = "status";
    /**
     * 连接服务器状态消息标示
     */
    String MSG = "msg";
    /**
     * 连接服务器返回数据标示
     */
    String RETURNDATA = "returnData";
    /**
     * 显示日志的等级 1->verbose; 2->DEBUG;3->INFO;4->WARN;5->ERROR;6->不显示
     */
    int LOG_LEVEL = 1;

    /*
    * id_center 1:浦东  2:浦西
     *  */
    String ID_CENTER = "id_center";

    /**
     * SP保存密码
     */
    String SAVE_PASSWORD = "saveuserpassword";
    /**
     * 应用下载目录
     */
    String DOWN_LOACD_ADDR = "/download/";

    // 接口
    String IF_mgr_food = "mgr_food";

    // 方法
    /**
     * 检测更新
     */
    String MD_get_user_version_url_4_pda = "get_user_version_url_4_pda";
    /**
     * 登录
     */
    String MD_adminLoginByPda = "adminLoginByPda";
    /**
     * 分拣商口分类
     */
    String MD_getIsPickByFoodType = "getIsPickByFoodType";
    /**
     * 根据商口查需要入的篮子
     */
    String MD_getOrderPickByFood = "getOrderPickByFood";
    /**
     * 商口入篮确认
     */
    String MD_updOrderFoodPick = "updOrderFoodPick";
    /**
     * 查询库位号或订单号后商口入篮确认
     */
    String MD_store_food_by_id_order = "store_food_by_id_order";
    /**
     * 查询订单入篮情况
     */
    String MD_searchOrderFoodPick = "searchOrderFoodPick";

    //参数
    String PARA_method = "method";
    String PARA_account_user = "account_user";
    String PARA_account_pwd = "account_pwd";
}
