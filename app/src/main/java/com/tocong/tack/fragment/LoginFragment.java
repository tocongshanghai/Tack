package com.tocong.tack.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyf.xlibrary.util.AppUtils;
import com.lyf.xlibrary.util.ToastUtil;
import com.tocong.tack.R;
import com.tocong.tack.activity.MainActivity;
import com.tocong.tack.service.DownLoadService;
import com.tocong.tack.util.Constants;
import com.tocong.tack.util.RetrofitUtil;
import com.tocong.tack.util.SaveUserPwdUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-22.14:52
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class LoginFragment extends Fragment {
    @Bind(R.id.version)
    TextView version;
    @Bind(R.id.username)
    EditText username;// 用户名
    @Bind(R.id.password)
    EditText password;// 密码
    @Bind(R.id.remember)
    CheckBox remember;// 记住密码
    @Bind(R.id.dropdown_button)
    ImageButton dropdown_button;// 用户名下拉选取按钮
    @Bind(R.id.login)
    Button login;// 登录

    @Bind(R.id.tv_area)
    TextView tv_area;
    @Bind(R.id.fl_rl_1)
    RelativeLayout mRelativeLayout_1;
    @Bind(R.id.fl_rl_2)
    RelativeLayout mRelativeLayout_2;
    boolean isLoginFlag = false;
    String area;

    private SaveUserPwdUtil pwdUtil;// 记住密码帮助类
    private PopupWindow popView;// 用户名提示下拉框
    private ListView listView;// 下框内容控件
    private PopViewlistAdapter adapter;
    private LinkedList<String> userNames;

    private ProgressDialog downloadProgressDialog; //显示下载进度的窗口
    private ProgressBar mProgressBar_show;
    private int progress;
    private BroadcastReceiver receiver;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                downloadProgressDialog.setProgress(msg.what);
                downloadProgressDialog.dismiss();
                getActivity().unregisterReceiver(receiver);
                receiver = null;
            }
            downloadProgressDialog.setProgress(msg.what);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        mProgressBar_show = (ProgressBar) getActivity().findViewById(R.id.pb_show);
        if (!isLoginFlag) {
            initViews_LoginFragment();
        } else {
            mRelativeLayout_1.setVisibility(View.INVISIBLE);
            mRelativeLayout_2.setVisibility(View.VISIBLE);
            initView_MyInfoFragment();
        }
        return view;
    }

    public void initView_MyInfoFragment() {
        Log.i("LoginFragment","-------------3"+mProgressBar_show.getProgress());
        mProgressBar_show.setProgress(0);
        Log.i("LoginFragment","-------------4"+mProgressBar_show.getProgress());
        tv_area.setText(area);

    }

    public void initViews_LoginFragment() {
        pwdUtil = new SaveUserPwdUtil(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progress = intent.getIntExtra("progress", 0);
                Message msg = new Message();
                msg.what = progress;
                handler.sendMessage(msg);
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter("DownLoadService.GetFileSize"));
        checkVersion();
        initPop();
    }

    private void checkVersion() {
        // 检测版本
        // 1.获取本地版本号
        final int nativeVersion = AppUtils.getVersionNumber(getActivity());
        String versionName = AppUtils.getVersionName(getActivity());
        version.setText("当前版本：" + (versionName == null ? "" : versionName));
        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_get_user_version_url_4_pda);
        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        String updateUrl = jsonObject.getJSONObject(Constants.RETURNDATA).getString("latest_url");
                        int serverVersion = Integer.parseInt(jsonObject.getJSONObject(Constants.RETURNDATA).getString("version_code"));
                        // 3.检测
                        if (nativeVersion < serverVersion) {
                            //显示下载进度窗口
                            showProgressDialog();

                            // 3.1本地版本号低于服务器版本下载更新
                            Intent intent = new Intent(getActivity(), DownLoadService.class);
                            intent.putExtra("updateUrl", updateUrl);
                            getActivity().startService(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                t.printStackTrace();
            }
        });
    }

    private void initPop() {
        listView = new ListView(getActivity());
        listView.setDividerHeight(1);
        listView.setDivider(getResources().getDrawable(R.drawable.bg_listview_divider));
        userNames = new LinkedList<>();
        List<String> saveNames = pwdUtil.quaryAllUserName();
        if (null != saveNames && saveNames.size() != 0) {
            String lastUsername = saveNames.get(0);
            userNames.addAll(saveNames);
            username.setText(lastUsername);
            password.setText(pwdUtil.quaryPwdFromUserName(lastUsername));
            if (pwdUtil.quaryIsSavePwd(lastUsername)) {
                remember.setChecked(true);
            } else {
                remember.setChecked(false);
            }
        }
        adapter = new PopViewlistAdapter();
        adapter.setMyListener(new MyListener() {

            @Override
            public void delete(int position) {
                String userName = userNames.get(position);
                pwdUtil.deleteUserName(userName);
                userNames.remove(position);
                adapter.setUserNames(userNames);
                adapter.notifyDataSetChanged();
                if (userNames.size() == 0) {
                    popView.dismiss();
                    popView = null;
                    dropdown_button.setBackgroundResource(R.mipmap.login_input_arrow);
                }
                username.setText("");
                password.setText("");
            }
        });
        adapter.setUserNames(userNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = (String) adapter.getItem(position);
                String passWord = pwdUtil.quaryPwdFromUserName(userName);
                boolean isCheck = pwdUtil.quaryIsSavePwd(userName);
                username.setText(userName);
                password.setText(passWord);
                remember.setChecked(isCheck);
                popView.dismiss();
                popView = null;
                dropdown_button.setBackgroundResource(R.mipmap.login_input_arrow);
            }
        });
    }

    @OnClick({R.id.login, R.id.dropdown_button})
    public void listener(View v) {
        switch (v.getId()) {
            case R.id.login:
                CharSequence userName = username.getText();
                CharSequence passWord = password.getText();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
                    if (userName.toString().trim().length() == 0 || password.toString().trim().length() == 0) {
                        ToastUtil.makeText(getActivity(), "用户名或密码不能为空", 2).show();
                    }
                    checkLogin(userName.toString().trim(), passWord.toString().trim());
                } else {
                    ToastUtil.makeText(getActivity(), "用户名或密码不能为空", 2).show();
                }
                break;
            case R.id.dropdown_button:
                if (userNames.size() != 0) {
                    dropdown_button.setBackgroundResource(R.mipmap.login_input_up);
                    popView = new PopupWindow(listView, username.getWidth(), userNames.size() == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (username.getHeight() * 2), false) {
                        @Override
                        public void dismiss() {
                            dropdown_button.setBackgroundResource(R.mipmap.login_input_arrow);
                            super.dismiss();
                        }
                    };
                    popView.setFocusable(true);
                    popView.setOutsideTouchable(true);
                    popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg));
                    popView.showAsDropDown(username, 0, 1);
                }
                break;

            default:
                break;
        }
    }

    private void checkLogin(final String userName, final String passWord) {
        Map<String, String> params = new WeakHashMap<>();
        params.put(Constants.PARA_method, Constants.MD_adminLoginByPda);
        params.put(Constants.PARA_account_user, userName);
        params.put(Constants.PARA_account_pwd, passWord);
        mProgressBar_show.setProgress(0);
        RetrofitUtil.asynPost(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (((MainActivity) getActivity()).loadDataException(jsonObject)) {
                        ToastUtil.makeText(getActivity(), "登录成功", 2).show();
                        //id_center 1:浦东 2：浦西
                        area = jsonObject.getJSONObject(Constants.RETURNDATA).getString(Constants.ID_CENTER);
                        if (!TextUtils.isEmpty(area)) {
                            area = area.equals("1") ? "浦东" : "浦西";
                        } else {
                            area = "";
                        }
                        // 保存密码
                        SaveUserPwdUtil.UserInfo info = null;
                        if (remember.isChecked()) {
                            info = pwdUtil.getUserInfo(userName, passWord, true);
                        } else {
                            info = pwdUtil.getUserInfo(userName, "", false);
                        }
                        if (!userNames.contains(userName)) {
                            userNames.addFirst(userName);
                            pwdUtil.insertUserInfos(info);
                        } else {
                            userNames.remove(userName);
                            userNames.addFirst(userName);
                            pwdUtil.updataUserPassword(info);
                        }
                        adapter.setUserNames(userNames);
                        adapter.notifyDataSetChanged();
                       /* startActivity(new Intent(LoginActivity.this,FoodTypeListActivity.class).putExtra("area",area));

                        finish();*/

                        Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            int i=0;
                            @Override
                            public void run() {
                                i=i+1;
                                mProgressBar_show.setProgress(i*10);
                            }
                        },0,10);
                        Log.i("LoginFragment","-------------1"+mProgressBar_show.getProgress());
                        mRelativeLayout_1.setVisibility(View.INVISIBLE);
                        mRelativeLayout_2.setVisibility(View.VISIBLE);
                        mProgressBar_show.setProgress(0);
                        Log.i("LoginFragment","-------------2"+mProgressBar_show.getProgress());
                        tv_area.setText(area);
                        isLoginFlag = true;

                    }

                } catch (JSONException e) {
                    ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ToastUtil.makeText(getActivity(), Constants.ERRORMSG, 2).show();
                t.printStackTrace();
            }
        });

    }


    @OnTextChanged(R.id.username)
    public void textChange() {
        password.setText("");
        if (!TextUtils.isEmpty(username.getText())) {
            if (username.getText().toString().trim().length() != 0) {
                List<String> allUserName = pwdUtil.quaryAllUserName();
                for (String name : allUserName) {
                    if (name.equals(username.getText().toString().trim())) {
                        boolean isCheck = pwdUtil.quaryIsSavePwd(name);
                        String passWord = pwdUtil.quaryPwdFromUserName(name);
                        password.setText(passWord);
                        remember.setChecked(isCheck);
                        break;
                    }
                }
            }
        }
    }

    private class PopViewlistAdapter extends BaseAdapter {
        private List<String> userNames;
        private MyListener myListener;

        public void setUserNames(List<String> userNames) {
            this.userNames = userNames;
        }

        public void setMyListener(MyListener myListener) {
            this.myListener = myListener;
        }

        @Override
        public int getCount() {
            return userNames == null ? 0 : userNames.size();
        }

        @Override
        public Object getItem(int position) {
            return userNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_dropdown, parent, false);
                mHolder = new ViewHolder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.username.setText((String) getItem(position));
            mHolder.del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    myListener.delete(position);
                }
            });
            ViewGroup group = (ViewGroup) convertView;
            group.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            return convertView;
        }
    }

    public static class ViewHolder {

        @Bind(R.id.textview)
        TextView username;
        @Bind(R.id.delete)
        ImageButton del;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    interface MyListener {
        void delete(int position);
    }


    private void showProgressDialog() {
        if (downloadProgressDialog == null) {
            downloadProgressDialog = new ProgressDialog(getActivity());
            downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgressDialog.setMessage("正在下载,请稍后...");
            downloadProgressDialog.setMax(100);
            downloadProgressDialog.setIndeterminate(false);
            downloadProgressDialog.setCancelable(false);
            downloadProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    downloadProgressDialog = null;
                }
            });
            downloadProgressDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            getActivity().unregisterReceiver(receiver);
            receiver=null;
        }
    }
}


