package com.tocong.tack.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tocong.tack.R;
import com.tocong.tack.fragment.FoodInfoFragment;
import com.tocong.tack.fragment.FoodTypeListFragment;
import com.tocong.tack.fragment.LoginFragment;
import com.tocong.tack.fragment.MyInfoFragment;
import com.tocong.tack.fragment.SearchFragment;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    BottomNavigationBar mBottomNavigationBar;
    private FoodTypeListFragment mFoodTypeListFragment;
    private FoodInfoFragment mFoodInfoFragment;
    private LoginFragment mLoginFragment;
    private SearchFragment mSearchFragment;
    private MyInfoFragment mMyInfoFragment;
    private FragmentManager mFragmentManager;

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
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (position) {
            case 0:
                fragmentTransaction.replace(R.id.fl_content, mFoodTypeListFragment);
                break;
            case 1:
                fragmentTransaction.replace(R.id.fl_content, mFoodInfoFragment);
                break;
            case 2:
                fragmentTransaction.replace(R.id.fl_content, mSearchFragment);
                break;
            case 3:
                fragmentTransaction.replace(R.id.fl_content, mLoginFragment);
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
        mFoodTypeListFragment = new FoodTypeListFragment();
        mFoodInfoFragment = new FoodInfoFragment();
        mSearchFragment = new SearchFragment();
        mLoginFragment = new LoginFragment();
        mMyInfoFragment=new MyInfoFragment();
    }
}
