package com.tocong.tack.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lyf.xlibrary.util.SharePreUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SaveUserPwdUtil {

    private Context mContext;

    public SaveUserPwdUtil(Context context) {
        this.mContext = context;
    }

    public UserInfo getUserInfo(String userName, String passWord, boolean isSavePwd) {
        return new UserInfo(userName, passWord, isSavePwd);

    }

    /**
     * 此方法描述的是： 查询所有用户名
     *
     * @return List<String>
     */
    public List<String> quaryAllUserName() {
        List<String> userNames = new ArrayList<String>();
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        for (UserInfo userInfo : userInfos) {
            userNames.add(userInfo.userName);
        }
        return userNames;
    }

    /**
     * 此方法描述的是： 通过用户名查密码
     *
     * @param userName
     * @return String
     */
    public String quaryPwdFromUserName(String userName) {
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        for (UserInfo userInfo : userInfos) {
            if (userName.endsWith(userInfo.userName)) {
                return userInfo.passWord;
            }
        }
        return null;
    }

    /**
     * 此方法描述的是： 删除用户
     *
     * @param userName void
     */
    public void deleteUserName(String userName) {
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        for (int i = 0; i < userInfos.size(); i++) {
            if (userName.endsWith(userInfos.get(i).userName)) {
                userInfos.remove(i);
            }
        }
        saveUserInfoToSp(userInfos);
    }

    /**
     * 此方法描述的是： 增加用户名
     *
     * @param info void
     */
    public void insertUserInfos(UserInfo info) {
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        userInfos.addFirst(info);
        saveUserInfoToSp(userInfos);
    }

    /**
     * 此方法描述的是： 通过用户名查找是否记住密码了
     *
     * @param userName
     * @return boolean
     */
    public boolean quaryIsSavePwd(String userName) {
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        for (UserInfo userInfo : userInfos) {
            if (userName.endsWith(userInfo.userName)) {
                return userInfo.isSavePwd;
            }
        }
        return false;
    }

    /**
     * 此方法描述的是： 相同用户名密码更改
     *
     * @param info void
     */
    public void updataUserPassword(UserInfo info) {
        LinkedList<UserInfo> userInfos = getUserInfoListFromSp();
        userInfos.addFirst(info);
        for (int i = 0; i < userInfos.size(); i++) {
            if (i != 0) {
                if (userInfos.get(i).userName.equals(info.userName)) {
                    userInfos.remove(i);
                }
            }
        }
        saveUserInfoToSp(userInfos);
    }

    /**
     * 此方法描述的是： 保存用户名和密码到SP
     *
     * @param userInfo void
     */
    public void saveUserInfoToSp(LinkedList<UserInfo> userInfo) {
        Gson gson = new Gson();
        String infos = gson.toJson(userInfo);
        SharePreUtil.putString(Constants.SAVE_PASSWORD, mContext, "userInfos", infos);
    }

    public LinkedList<UserInfo> getUserInfoListFromSp() {
        String userInfo = SharePreUtil.getString(Constants.SAVE_PASSWORD, mContext, "userInfos");
        if (!"".equals(userInfo)) {
            Gson gson = new Gson();
            LinkedList<UserInfo> userInfos = gson.fromJson(userInfo, new TypeToken<LinkedList<UserInfo>>() {
            }.getType());
            return userInfos;
        } else {
            return new LinkedList<UserInfo>();
        }
    }

    public final class UserInfo {
        public String userName;
        public String passWord;
        public boolean isSavePwd;

        public UserInfo(String userName, String passWord, boolean isSavePwd) {
            super();
            this.userName = userName;
            this.passWord = passWord;
            this.isSavePwd = isSavePwd;
        }

        public UserInfo() {

        }
    }
}
