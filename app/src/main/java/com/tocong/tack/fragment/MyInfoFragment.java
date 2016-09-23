package com.tocong.tack.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tocong.tack.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-23.8:00
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class MyInfoFragment extends Fragment {
    @Bind(R.id.tv_area)
    TextView tv_area;
    String area;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_myinfo,container,false);
        ButterKnife.bind(this,view);
        initViews();
        return view;
    }

    public  void initViews(){
        Bundle bundle=getArguments();
        area=bundle.getString("area");
        tv_area.setText(area);
    }
}
