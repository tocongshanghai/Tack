package com.tocong.tack.fragment;

import android.app.Fragment;

import java.util.ArrayList;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-26.17:56
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class FragmentListManager {
    public static ArrayList<Fragment> fragmentArrayList=new ArrayList<>();

    public static  void  addFragment(Fragment fragment){
        fragmentArrayList.add(fragment);
    }

    public  static void deleteFragment(Fragment fragment){
        fragmentArrayList.remove(fragment);

    }
}
