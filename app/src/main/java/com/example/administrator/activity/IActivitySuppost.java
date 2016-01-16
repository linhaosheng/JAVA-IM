package com.example.administrator.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.model.LoginConfig;

/**
 * Created by Administrator on 2015/9/13.
 */
public interface IActivitySuppost {
    /**
     * 获取IMApplication
     * @return
     */
    public abstract IMApplication getIMApplication();

    /**
     * 终止服务
     */
    public abstract void stopService();

    /**
     * 开启服务
     */
    public abstract void startService();

    /**
     * 校验网络-如果没有网络就弹出设置，并返回true
     * @return
     */
    public abstract boolean validateInternet();

    /**
     * 检验网络，如果没有网络就返回true;
     * @return
     */
    public abstract boolean hasInternetConnected();

    /**
     * 退出应用
     */
    public abstract void isExit();

    /**
     * 判断GPS是否已经开启
     * @return
     */
    public abstract boolean hasLocationGPS();

    /**
     * 判断是否有开启基站
     * @return
     */
    public abstract boolean hasLocationNetWork();

    /**
     * 检查内存卡
     */
    public abstract void checkMemoryCard();

    /**
     * 显示toast
     * @param text
     * @param longTime
     */
    public abstract void showToast(String text,int longTime);

    /**
     * 短时间显示toast
     * @param text
     */
    public abstract void showToast(String text);

    /**
     * 显示进度条
     * @return
     */
    public abstract ProgressDialog getProgressDialog();

    /**
     * 返回当前Activity的上下文
     * @return
     */
    public abstract Context getContext();

    /**
     * 获取当前用户的SharePreFerences的配置
     * @return
     */
    public SharedPreferences getLoginUserSharePre();

    /**
     * 保存登录设置
     * @param loginConfig
     */
    public void saveLoginConfig(LoginConfig loginConfig);

    /**
     * 获取用户配置
     * @return
     */
    public LoginConfig getLoginConfig();

    /**
     * 用户是否在线(当前网络是否连接成功)
     * @return
     */
    public boolean getUserOnlineState();

    /**
     * 设置用户在线状态  在线为true ，不在线为false
     * @param isOnline
     */
    public void setUserOnlineState(boolean isOnline);

    /**
     * 通知类型
     * @param iconId
     * @param contentTitle
     * @param contentText
     * @param activity
     * @param from
     */
    public void setNoticType(int iconId,String contentTitle,String contentText,Class activity,String from);

}
