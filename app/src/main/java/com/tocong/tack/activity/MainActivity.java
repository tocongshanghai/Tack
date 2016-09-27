package com.tocong.tack.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tocong.tack.R;
import com.tocong.tack.fragment.FoodInfoFragment;
import com.tocong.tack.fragment.FoodTypeListFragment;
import com.tocong.tack.fragment.FragmentListManager;
import com.tocong.tack.fragment.LoginFragment;
import com.tocong.tack.fragment.SearchFragment;

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

   /* @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        ButterKnife.bind(this);
        initViews(savedInstanceState);
    }*/

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
}
