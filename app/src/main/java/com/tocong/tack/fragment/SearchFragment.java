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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lyf.xlibrary.util.StringUtil;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.R;
import com.tocong.tack.activity.MainActivity;
import com.tocong.tack.entity.OrderDetails;
import com.tocong.tack.entity.UserInfo;
import com.tocong.tack.util.Constants;
import com.tocong.tack.util.RetrofitUtil;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

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
 * 创作时间: 2016-09-22.14:52
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class SearchFragment extends Fragment {

    @Bind(R.id.searchView)
    SearchView searchView;
    @Bind(R.id.textView)
    TextView idOrder;
    @Bind(R.id.textView2)
    TextView userName;
    @Bind(R.id.textView3)
    TextView userId;
    @Bind(R.id.textView4)
    TextView requestTime;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public String scanResult;
    private List<OrderDetails> orderDetails;
    private CommonAdapter<OrderDetails> adapter;
    private String id_order;
    public String[] seq;//库位号
    public String idOder_or_tpSeq = "-1";
    public long food_id;
    public boolean basket_food; // 0代表篮子或者订单号，1代表菜品

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        initViews(savedInstanceState);
        return view;
    }

    private void initViews(Bundle savedInstanceState) {
        orderDetails = new ArrayList<>();
        //修改系统搜索输入参数如字体颜色、搜索框高度等
        int search_src_text_id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(search_src_text_id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 48);
        layoutParams.setMargins(-12, -8, 0, -8);
        layoutParams.weight = 1;
        textView.setLayoutParams(layoutParams);

        //修改系统搜索框搜索父类相关参数
        int search_plate_id = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        LinearLayout search_plate = (LinearLayout) searchView.findViewById(search_plate_id);
        search_plate.setBackgroundResource(R.color.searchView_bg);

        //修改系统默认搜索图标
        int search_mag_icon_id = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView mSearchIcon = (ImageView) searchView.findViewById(search_mag_icon_id);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(-10, 0, 0, 0);
        layoutParams1.gravity = Gravity.CENTER;
        mSearchIcon.setLayoutParams(layoutParams1);
        mSearchIcon.setImageDrawable(getResources().getDrawable(R.mipmap.search_grey));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //点击搜索后的回调监听
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (StringUtil.stringIsNull(query)) {
                    ToastUtil.makeText(getActivity(), "请输入订单号或库位号", 2).show();
                } else {
                    // 先隐藏键盘
                    InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                    getOrderDetails(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CommonAdapter<OrderDetails>(getActivity(), R.layout.item_recyclerview_searchorder, orderDetails) {
            @Override
            public void convert(ViewHolder holder, final OrderDetails orderDetail) {
                holder.setText(R.id.tv_foodName, orderDetail == null ? "" : orderDetail.getFood_name());
                holder.setText(R.id.tv_foodStandard, (orderDetail == null ? "" : orderDetail.getSale_describe()));
                holder.setText(R.id.tv_foodNum, orderDetail == null ? "" : (orderDetail.getOrder_number() + "份"));
                if (orderDetail != null) {
                    if (orderDetail.getPick_num() < orderDetail.getOrder_number()) {
                        holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_no_sort);
                        holder.setText(R.id.tv_isSort, "入篮" + orderDetail.getPick_num() + "份");
                        holder.setTextColor(R.id.tv_isSort, Color.RED);
                    } else {
                        holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_sort);
                        holder.setText(R.id.tv_isSort, "已入篮");
                        holder.setTextColor(R.id.tv_isSort, Color.WHITE);
                    }
                }
//                if ((orderDetail == null ? 0 : orderDetail.is_pick()) == 0) {
//                    holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_sort);
//                    holder.setText(R.id.tv_isSort, "已入篮");
//                    holder.setTextColor(R.id.tv_isSort, Color.WHITE);
//                } else if (orderDetail.is_pick() == 1) {
//                    holder.setBackgroundRes(R.id.tv_isSort, R.drawable.btn_no_sort);
//                    holder.setText(R.id.tv_isSort, "入篮确认");
//                    holder.setTextColor(R.id.tv_isSort, Color.RED);
//                }
                holder.setOnClickListener(R.id.tv_isSort, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (orderDetail != null && orderDetail.is_pick() == 1) {
//                            if (loadingDialog != null) {
//                                loadingDialog.show();
//                            }
//                            sortConfirm(orderDetail.getId_detail_order());
//                        }
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);

    }


    public void getOrderDetails(final String order_id) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginrecord", Context.MODE_PRIVATE);
        boolean isLoginFlag = false;
        isLoginFlag = sharedPreferences.getBoolean("isLoginFlag", false);
        if (!isLoginFlag) {
            return;
        }
        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_searchOrderFoodPick);
        // params.put("id_order", order_id);

        if (order_id.matches("^[0-9]+$")) {
            params.put("id_order", order_id);
        } else if (order_id.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+$")) {
            params.put("tp_seq", order_id);

        }
        Log.i("searchOrderActivity", order_id);

        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        UserInfo userInfo = null;
                        String orderDetailsStr = jsonObject.getJSONObject(Constants.RETURNDATA).getString("id_order_detail");
                        if (!isFreezeLocation(order_id)) {
                            String userInfoStr = jsonObject.getJSONObject(Constants.RETURNDATA).getString("user_info");
                            userInfo = new Gson().fromJson(userInfoStr, UserInfo.class);
                        }

                        List<OrderDetails> lists = new Gson().fromJson(orderDetailsStr, new TypeToken<List<OrderDetails>>() {
                        }.getType());

                        if (orderDetails == null) {
                            return;
                        }
                        orderDetails.clear();
                        orderDetails.addAll(lists);
                        if (orderDetails.size() > 0 && userInfo != null) {
                            String id_order = orderDetails.get(0).getId_order() + "";
                            changeView("会员名：" + userInfo.getContact_user(), "会员ID：" + userInfo.getId_user(), "送达时间：" + userInfo.getRequire_time_str(), "送货单号：" + id_order);
                        } else if (orderDetails.size() <= 0) {
                            changeView("", "", "", "");
                            ToastUtil.makeText(getActivity(), "订单不存在", 2).show();
                        } else if (orderDetails.size() > 0 && userInfo == null) {
                            changeView("", "", "", "");
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        changeView("", "", "", "");
                        orderDetails.clear();
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {

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

    private boolean isFreezeLocation(String tp_seq) {
        if (tp_seq.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+$")) {
            if (tp_seq.endsWith("0000")) {
                return true;
            }
        }
        return false;
    }

    //给视图赋值
    public void changeView(String name, String userID, String request_time, String id_Order) {
        idOrder.setText(id_Order);
        userName.setText(name);
        userId.setText(userID);
        requestTime.setText(request_time);
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
                sortConfirm(idOder_or_tpSeq, orderDetails.getId_food() + "", "1");
            }
        });
        builder.setPositiveButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                is_sort_all = false;
                sortConfirm(idOder_or_tpSeq, orderDetails.getId_food() + "", "0");
            }
        });
        builder.create().show();
    }

    private void sortConfirm(final String idOder_or_tpSeq, String id_food, String is_all_pick) {
        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_store_food_by_id_order);
        //   params.put("id_order", id_order);

        if (idOder_or_tpSeq.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+$")) {
            params.put("tp_seq", idOder_or_tpSeq);
        }
        if (idOder_or_tpSeq.matches("^[0-9]+$")) {
            params.put("id_order", idOder_or_tpSeq);
        }
        if (id_food.matches("^(69){1}[0-9]{11}$")) {
            params.put("food_bar_code", id_food);
        } else {
            params.put("id_food", id_food);
        }

        params.put("is_all_pick", is_all_pick);
        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        ToastUtil.makeText(getActivity(), "成功", 2).show();
                        getOrderDetails(idOder_or_tpSeq + "");
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (idOder_or_tpSeq.equals("-1")) {
                Toast.makeText(getActivity(), "请扫描库位号或者订单号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!idOder_or_tpSeq.equals(-1) && !basket_food) {
                if (idOder_or_tpSeq.matches("^[0-9]*-*[0-9]+-[0-9]+-[0-9]+$")) {
                    //库位号
                    searchView.setQuery(seq.length > 1 ? seq[1] : "", false);
                    getOrderDetails(idOder_or_tpSeq);
                }
                if (idOder_or_tpSeq.matches("^[0-9]+$")) {
                    //订单号
                    searchView.setQuery(idOder_or_tpSeq, true);
                }

            }
            if (basket_food) {
                if (String.valueOf(food_id).matches("^(69){1}[0-9]{11}$")) {
                    boolean flag = false;
                    for (OrderDetails or : orderDetails) {
                        Log.i("searchOrderActivity", "------------" + or.getFood_info().getFood_bar_code().trim());
                        if (or.getFood_info().getFood_bar_code().replace(" ", "").equals(String.valueOf(food_id))) {
                            if (id_order_details != or.getId_detail_order()) {
                                is_sort_all = true;
                            }
                            id_order_details = or.getId_detail_order();
                            if ((or.getOrder_number() - or.getPick_num()) > 1 && is_sort_all) {
                                sortDialog(or);
                            } else {
                                // sortConfirm(id_order, result, "0");
                                sortConfirm(idOder_or_tpSeq, String.valueOf(food_id), "0");
                            }
                            flag = true;
                            break;
                        }

                    }
                    if (!flag) {
                        ToastUtil.makeText(getActivity(), "订单不存在此商品", 2).show();
                    }
                    return;
                } else {
                    if (food_id == 0) {
                        return;
                    }
                    boolean flag = false;
                    for (OrderDetails or : orderDetails) {
                        if (or.getId_food() == food_id) {
                            if (id_order_details != or.getId_detail_order()) {
                                is_sort_all = true;
                            }
                            id_order_details = or.getId_detail_order();
                            if ((or.getOrder_number() - or.getPick_num()) > 1 && is_sort_all) {
                                sortDialog(or);
                            } else {
                                // sortConfirm(id_order, id_food + "", "0");
                                sortConfirm(idOder_or_tpSeq, food_id + "", "0");
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        ToastUtil.makeText(getActivity(), "订单不存在此商品", 2).show();
                    }
                    return;
                }
            }

        }
    }
}
