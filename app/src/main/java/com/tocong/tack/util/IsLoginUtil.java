package com.tocong.tack.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-27.16:02
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class IsLoginUtil {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static  IsLoginUtil mInstance;
    Context context;
    private IsLoginUtil(Context context){
        sharedPreferences=context.getSharedPreferences("loginrecord",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

    }

    public  static IsLoginUtil getmInstance(Context context){
        if(mInstance==null){
            synchronized (IsLoginUtil.class){
                if(mInstance==null){
                    mInstance=new IsLoginUtil(context);
                }
            }
        }
        return  mInstance;
    }

    public static void putUserName(String username,Context context){
       getmInstance(context)._putUserName(username);
    }

    public static boolean getUserName(Context context){
       return getmInstance(context)._getUserName();
    }
    private  void _putUserName(String username){

        editor.putString("username",username);

    }
    private boolean _getUserName(){

        String username=sharedPreferences.getString("username",null);
        if(username==null){
            return false;
        }else {
        return  true;}
    }
}
