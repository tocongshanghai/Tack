package com.tocong.tack.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tocong.tack.R;
import com.tocong.tack.fragment.FoodInfoFragment;
import com.tocong.tack.fragment.FoodTypeListFragment;
import com.tocong.tack.fragment.FragmentListManager;
import com.tocong.tack.fragment.LoginFragment;
import com.tocong.tack.fragment.SearchFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    @Bind(R.id.pb_show)
    ProgressBar mProgressBar_show;

    BottomNavigationBar mBottomNavigationBar;
    public FoodTypeListFragment mFoodTypeListFragment;
    public FoodInfoFragment mFoodInfoFragment;
    public LoginFragment mLoginFragment;
    public SearchFragment mSearchFragment;

    private FragmentManager mFragmentManager;
    private int index;
    public static  boolean onTabSelect_or_onScan; // 0是点击底部导航栏，1是扫描
    @Override
    public void preSetContentView() {

    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mFragmentManager = getFragmentManager();
        initFragment();
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.bottomnavigationbar_list, "菜单"))
                .addItem(new BottomNavigationItem(R.mipmap.bottomnavigationbar_basket, "入篮"))
                .addItem(new BottomNavigationItem(R.mipmap.bottomnavigationbar_search, "查询"))
                .addItem(new BottomNavigationItem(R.mipmap.bottomnavigationbar_me, "我"))
                .setFirstSelectedPosition(3).initialise();

        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();


    }

    private void setDefaultFragment() {
        mFragmentManager.beginTransaction().replace(R.id.fl_content, mLoginFragment).commit();

    }

    @Override
    public void onTabSelected(int position) {
        index = position;
        onTabSelect_or_onScan=false;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = FragmentListManager.fragmentArrayList.get(FragmentListManager.fragmentArrayList.size() - 1);
        switch (position) {
            case 0:
                if (mFoodTypeListFragment == null) {
                    mFoodTypeListFragment = new FoodTypeListFragment();
                    fragmentTransaction.hide(fragment);
                    fragmentTransaction.add(R.id.fl_content, mFoodTypeListFragment, mFoodTypeListFragment.getClass().getName());

                }
                fragmentTransaction.hide(fragment);
                FragmentListManager.deleteFragment(fragment);
                fragmentTransaction.show(mFoodTypeListFragment);

                FragmentListManager.addFragment(mFoodTypeListFragment);
                //  fragmentTransaction.replace(R.id.fl_content, mFoodTypeListFragment);
                break;
            case 1:
                if (mFoodInfoFragment == null) {
                    mFoodInfoFragment = new FoodInfoFragment();
                    fragmentTransaction.hide(fragment);
                    fragmentTransaction.add(R.id.fl_content, mFoodInfoFragment, mFoodInfoFragment.getClass().getName());
                }
                fragmentTransaction.hide(fragment);
                FragmentListManager.deleteFragment(fragment);
                fragmentTransaction.show(mFoodInfoFragment);
                FragmentListManager.addFragment(mFoodInfoFragment);
                //   fragmentTransaction.replace(R.id.fl_content, mFoodInfoFragment);
                break;
            case 2:
                if (mSearchFragment == null) {
                    mSearchFragment = new SearchFragment();
                    fragmentTransaction.hide(fragment);
                    fragmentTransaction.add(R.id.fl_content, mSearchFragment, mSearchFragment.getClass().getName());
                }
                fragmentTransaction.hide(fragment);
                FragmentListManager.deleteFragment(fragment);
                fragmentTransaction.show(mSearchFragment);
                FragmentListManager.addFragment(mSearchFragment);
                //fragmentTransaction.replace(R.id.fl_content, mSearchFragment);
                break;
            case 3:
                if (mLoginFragment == null) {
                    mLoginFragment = new LoginFragment();
                    fragmentTransaction.hide(fragment);
                    fragmentTransaction.add(R.id.fl_content, mLoginFragment, mLoginFragment.getClass().getName());
                }
                fragmentTransaction.hide(fragment);
                FragmentListManager.deleteFragment(fragment);
                fragmentTransaction.show(mLoginFragment);
                FragmentListManager.addFragment(mLoginFragment);
                //    fragmentTransaction.replace(R.id.fl_content, mLoginFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private void initFragment() {
        mLoginFragment = new LoginFragment();
        FragmentListManager.addFragment(mLoginFragment);
    }

    @Override
    public void getScanResult(String result) {
        onTabSelect_or_onScan=true;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (index) {
            case 0:

                break;
            case 1:
                if (result != null) {
                    try {
                        result = URLDecoder.decode(result, "utf-8");
                        Log.i("FoodTypeListActivity", result);
                        if (result.contains("http")) {
                            long id_food = -1;
                            try {
                                String[] strings = result.split("sn=");
                                String sn = strings[1];
                                String food_id = sn.substring(3, 8);
                                id_food = Long.parseLong(food_id);
                            } catch (Exception e) {
                                break;
                            }
                            if (id_food == -1) {
                                return;
                            }
                            mFoodInfoFragment.food_id = id_food;
                            mFoodInfoFragment.food_basket = false;
                            fragmentTransaction.hide(mFoodInfoFragment);
                            fragmentTransaction.show(mFoodInfoFragment);

                        }
                        if (result.matches("^(69){1}[0-9]{11}$")) {
                            mFoodInfoFragment.food_id = Long.parseLong(result.trim());
                            mFoodInfoFragment.food_basket = false;
                            fragmentTransaction.hide(mFoodInfoFragment);
                            fragmentTransaction.show(mFoodInfoFragment);
                        }
                        if (result.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+\\$.*$")) {
                           /* String[] str = result.split("\\$");
                            startActivity(new Intent(this, SearchOrderActivity.class).putExtra("seq", str));*/
                            mFoodInfoFragment.tp_seq = result;
                            mFoodInfoFragment.food_basket = true;
                            fragmentTransaction.hide(mFoodInfoFragment);
                            fragmentTransaction.show(mFoodInfoFragment);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (result != null) {
                    try {
                        result = URLDecoder.decode(result, "utf-8");
                        if (result.contains("http")) {
                            long id_food = -1;
                            try {
                                String[] strings = result.split("sn=");
                                String sn = strings[1];
                                String food_id = sn.substring(3, 8);
                                id_food = Long.parseLong(food_id);
                            } catch (Exception e) {
                                return;
                            }
                            if (id_food == -1) {
                                return;
                            }
                            mSearchFragment.food_id = id_food;
                            mSearchFragment.basket_food = true;
                            fragmentTransaction.hide(mSearchFragment);
                            fragmentTransaction.show(mSearchFragment);

                        }
                        if (result.matches("^(69){1}[0-9]{11}$")) {
                            mSearchFragment.food_id = Long.parseLong(result.trim());
                            mSearchFragment.basket_food = true;
                            fragmentTransaction.hide(mSearchFragment);
                            fragmentTransaction.show(mSearchFragment);
                        }
                        if (result.matches("^[0-9]+$")) {
                            mSearchFragment.idOder_or_tpSeq = result.trim();
                            mSearchFragment.basket_food = false;
                            fragmentTransaction.hide(mSearchFragment);
                            fragmentTransaction.show(mSearchFragment);
                        }
                        if (result.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+\\$.*$")) {
                            String[] str = result.split("\\$");
                            mSearchFragment.idOder_or_tpSeq = str[0];
                            mSearchFragment.seq = str;
                            mSearchFragment.basket_food = false;
                            fragmentTransaction.hide(mSearchFragment);
                            fragmentTransaction.show(mSearchFragment);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("loginrecord", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
