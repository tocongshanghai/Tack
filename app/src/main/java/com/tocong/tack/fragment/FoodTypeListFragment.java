package com.tocong.tack.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.R;
import com.tocong.tack.activity.MainActivity;
import com.tocong.tack.adapter.FoodTypeAdapter;
import com.tocong.tack.entity.FoodInfo;
import com.tocong.tack.entity.FoodType;
import com.tocong.tack.util.Constants;
import com.tocong.tack.util.RetrofitUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-22.14:50
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class FoodTypeListFragment extends Fragment implements ExpandableListView.OnChildClickListener{
   @Bind(R.id.elv)
    ExpandableListView mExpandableListView;
    @Bind(R.id.empty)
    ScrollView empty;
    @Bind(R.id.empty_msg)
    TextView empty_msg;

    private List<FoodType> foodTypes;
    private List<List<FoodInfo>> foodInfos;
    private FoodTypeAdapter foodTypeAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foodtypelist, container, false);
        ButterKnife.bind(this, view);
        initViews(savedInstanceState);
        return view;
    }

    private void initViews(Bundle savedInstanceState){

        init();


    }
    private void init(){
        foodTypes=new ArrayList<>();
        foodInfos=new ArrayList<>();
        mExpandableListView.setEmptyView(empty);
        mExpandableListView.setOnChildClickListener(this);
        foodTypeAdapter = new FoodTypeAdapter(getActivity(), foodTypes, foodInfos);
        mExpandableListView.setAdapter(foodTypeAdapter);
        getFoodTypes(true);
    }

    public void getFoodTypes(final boolean isSortAll){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("loginrecord", Context.MODE_PRIVATE);
        boolean isLoginFlag=false;
       isLoginFlag= sharedPreferences.getBoolean("isLoginFlag",false);
        if(isLoginFlag) {

            Map<String, String> params = new WeakHashMap<>();
            params.put(Constants.PARA_method, Constants.MD_getIsPickByFoodType);
            RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, final Response<String> response) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (!((MainActivity) getActivity()).loadDataException(jsonObject)) {
                                    return;
                                }
                                JSONObject jsonObject1 = jsonObject.getJSONObject(Constants.RETURNDATA);
                                String sort_foodTypeStr = jsonObject1.getString("pick_food_type");//全部
                                final List<FoodType> sortFoodType = new Gson().fromJson(sort_foodTypeStr, new TypeToken<List<FoodType>>() {
                                }.getType());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (foodTypes == null || foodInfos == null) {
                                            return;
                                        }
                                        foodTypes.clear();
                                        foodInfos.clear();
                                        if (isSortAll) {
                                            foodTypes.addAll(sortFoodType);
                                        }
                                        if (foodTypes.size() == 0) {
                                            if (empty_msg != null) {
                                                empty_msg.setText(getResources().getString(R.string.tv_empty));
                                            }
                                        }
                                        for (FoodType foodType : foodTypes) {
                                            foodInfos.add(foodType.getFoodInfoList());
                                        }
                                        foodTypeAdapter.notifyDataSetChanged();
                                   /* if (loadingDialog != null) {
                                        loadingDialog.dismiss();
                                    }*/
                                    }
                                });


                            } catch (JSONException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (empty_msg != null) {
                                            empty_msg.setText(getResources().getString(R.string.tv_empty));
                                        }

                                   /* if (loadingDialog != null) {
                                        loadingDialog.dismiss();
                                    }*/
                                        ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();

                                    }
                                });
                                e.printStackTrace();
                            } catch (Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (empty_msg != null) {
                                            empty_msg.setText(getResources().getString(R.string.tv_empty));
                                        }

                                  /*  if (loadingDialog != null) {
                                        loadingDialog.dismiss();
                                    }*/
                                        ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();

                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }) {
                    }.start();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (empty_msg != null) {
                        empty_msg.setText(getResources().getString(R.string.tv_empty));
                    }
                    ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                }
            });
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            getFoodTypes(true);
        }
    }
}
