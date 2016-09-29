package com.tocong.tack.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.R;
import com.tocong.tack.activity.MainActivity;
import com.tocong.tack.entity.FoodInfo;
import com.tocong.tack.entity.OrderDetails;
import com.tocong.tack.util.Constants;
import com.tocong.tack.util.RetrofitUtil;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-22.14:51
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class FoodInfoFragment extends Fragment {

    @Bind(R.id.food_name)
    TextView foodName;
    @Bind(R.id.food_standard)
    TextView foodStandard;
    @Bind(R.id.sort_num)
    TextView sortNum;
    @Bind(R.id.no_sort_num)
    TextView noSortNum;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<OrderDetails> orderDetails;
    private CommonAdapter<OrderDetails> adapter;
    private int sort_num = -1;
    public long food_id = -1;
    public String scanResult;
    public String tp_seq;
    public boolean food_basket;  //0代表 菜品， 1代表篮子

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foodinfo, container, false);
        ButterKnife.bind(this, view);
        initViews(savedInstanceState);
        return view;
    }

    private void initViews(Bundle savedInstanceState) {

        orderDetails = new ArrayList<>();

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CommonAdapter<OrderDetails>(getActivity(), R.layout.item_recyclerview_foodinfo, orderDetails) {
            @Override
            public void convert(final ViewHolder holder, final OrderDetails orderDetails) {
                holder.setText(R.id.tv_number, orderDetails == null ? "" : isFreezeLocation1(orderDetails));
                String count = "<font color='red'>" + (orderDetails == null ? "" : orderDetails.getOrder_number()) + "</font>份";
                holder.setText(R.id.tv_count, Html.fromHtml(count));
                if (orderDetails != null) {
                    if (orderDetails.getPick_num() < orderDetails.getOrder_number()) {
                        holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_no_sort);
                        holder.setText(R.id.tv_isSort, "入篮" + orderDetails.getPick_num() + "份");
                        holder.setTextColor(R.id.tv_isSort, Color.RED);
                    } else {
                        holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_sort);
                        holder.setText(R.id.tv_isSort, "已入篮");
                        holder.setTextColor(R.id.tv_isSort, Color.WHITE);
                    }
                }
            }
        };
        mRecyclerView.setAdapter(adapter);
        // getOrderDetails(food_id);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Toast.makeText(getActivity(), "" + food_id, Toast.LENGTH_SHORT).show();
            if (food_id == -1) {
                Toast.makeText(getActivity(), "请扫描二维码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (food_id != -1 && !food_basket) {
                //获取菜品的订单详情
                getOrderDetails(food_id);
            }
            if (food_basket) {
                //入篮
                try {
                    tp_seq = URLDecoder.decode(tp_seq, "utf-8");
                    Log.i("FoodInfoActivity", tp_seq);

                    String[] split = tp_seq.split("\\$");
                    if (orderDetails != null && orderDetails.size() > 0) {
                        boolean flag = false;
                        for (OrderDetails or : orderDetails) {
                            if ((isFreezeLocation(or)).equals(split[0])) {
                                if (id_order_details != or.getId_detail_order()) {
                                    is_sort_all = true;
                                }
                                id_order_details = or.getId_detail_order();
                                if ((or.getOrder_number() - or.getPick_num()) > 1 && is_sort_all) {
                                    sortDialog(or);
                                } else {
                                    sortConfirm(isFreezeLocation(or), "0");
                                }
                                flag = true;
                                break;
                            } else {
                                flag = false;
                            }
                        }
                        if (!flag) {
                            ToastUtil.makeText(getActivity(), "此库位号没有  " + foodName.getText().toString(), 2).show();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getOrderDetails(long id_food) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginrecord", Context.MODE_PRIVATE);
        boolean isLoginFlag = false;
        isLoginFlag = sharedPreferences.getBoolean("isLoginFlag", false);
        if (!isLoginFlag) {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_getOrderPickByFood);
        if (String.valueOf(id_food).matches("^(69){1}[0-9]{11}$")) {
            params.put("food_bar_code", id_food + "");
        } else {
            params.put("id_food", id_food + "");
        }

        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        String orderDetailsStr = jsonObject.getJSONObject(Constants.RETURNDATA).getString("order_detail");
                        String foodInfoStr = jsonObject.getJSONObject(Constants.RETURNDATA).getString("food_info");
                        List<OrderDetails> lists = new Gson().fromJson(orderDetailsStr, new TypeToken<List<OrderDetails>>() {
                        }.getType());
                        FoodInfo foodInfo = new Gson().fromJson(foodInfoStr, FoodInfo.class);
                        //重新给food_id 赋值
                        food_id = foodInfo.getId_food();
                        if (foodName == null || foodStandard == null || sortNum == null || noSortNum == null || orderDetails == null) {
                            return;
                        }
                        foodName.setText(foodInfo == null ? "" : foodInfo.getFood_name());
                        foodStandard.setText("规格：" + (foodInfo == null ? "" : foodInfo.getSale_describe()));
                        sortNum.setText("全部" + (foodInfo == null ? 0 : foodInfo.getPickCount()) + "份");
                        sort_num = foodInfo == null ? 0 : foodInfo.getNoIsPickCount();
                        String s = "未投" + (sort_num < 0 ? 0 : sort_num) + "份";
                        if (sort_num <= 0) {
                            s = "已投完";
                            noSortNum.setTextColor(Color.GREEN);
                        }
                        noSortNum.setText(s);
                        orderDetails.clear();
                        orderDetails.addAll(lists);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {

                    ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
            }
        });
    }


    /*
    * 确认入篮
    * */
    private void sortConfirm(String tp_seq, String is_all_pick) {

        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_updOrderFoodPick);
        if (String.valueOf(food_id).matches("^(69){1}[0-9]{11}$")) {
            params.put("food_bar_code", food_id + "");
        } else {
            params.put("id_food", food_id + "");
        }

        params.put("tp_seq", tp_seq);
        params.put("is_all_pick", is_all_pick);
        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        ToastUtil.makeText(getActivity(), "成功", 2).show();
                        getOrderDetails(food_id);
                    }
                } catch (JSONException e) {
                    ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
            }
        });
    }


    private boolean is_sort_all = true;//同一件商品存在多份时，选择不再提示后，标示是否再次弹出框
    private int id_order_details = -1;//标示是否是同一库位号

    private void sortDialog(final OrderDetails orderDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setTitle("提示");
        String s = "<font color='red'>" + (orderDetails.getOrder_number() - orderDetails.getPick_num()) + "</font>份一起入篮？";
        builder.setMessage(Html.fromHtml(s));
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sortConfirm(orderDetails.getTp_seq(), "1");
                sortConfirm(isFreezeLocation(orderDetails), "1");
            }
        });
        builder.setPositiveButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                is_sort_all = false;
                // sortConfirm(orderDetails.getTp_seq(), "0");
                sortConfirm(isFreezeLocation(orderDetails), "0");
            }
        });
        builder.create().show();
    }


    /*
  * 判断tp_seq_str末尾是0并且只有一位数，如果true,则将tp_seq末尾替换成0000
  * */
    private String isFreezeLocation(OrderDetails orderDetails) {
        String[] str = orderDetails.getTp_seq_str().split("-");
        String str1 = orderDetails.getTp_seq();
        if (str[1].equals("0") && str[1].length() == 1) {
            str1 = str1.substring(0, str1.lastIndexOf("-")) + "-0000";
        }
        return str1;
    }

    /*
    * 判断订单详情中 tp_seq_str是不是区域的保温箱，如果是则返回A11-1川沙南（1）,否则返回 tp_seq_str(A1陆家嘴-02).
    * */
    private String isFreezeLocation1(OrderDetails orderDetails) {
        String[] str = orderDetails.getTp_seq_str().split("-");
        if (str[1].equals("0") && str[1].length() == 1) {
            return orderDetails.getProcess_remark() + matchChinese(str[0]) + "(" + orderDetails.getOrder_number() + ")";
        }
        return orderDetails.getTp_seq_str();
    }


    /*
    * 将tp_seq_str="B7高行-0" 截取下 "高行"
    * */
    private String matchChinese(String s) {
        String pattern = "[\\u4E00-\\u9FA5]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group();
        } else {
            return "";
        }
    }
}
