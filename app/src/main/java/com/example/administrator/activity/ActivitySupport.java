package com.example.administrator.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.administrator.common.Constant;
import com.example.administrator.model.LoginConfig;
import com.example.administrator.service.IMChatService;
import com.example.administrator.service.IMContactService;
import com.example.administrator.service.IMSystemMsgService;
import com.example.administrator.service.ReConnectService;

/**
 * Created by Administrator on 2015/9/13.
 */
public class ActivitySupport extends Activity implements IActivitySuppost {

    protected Context context=null;
    protected SharedPreferences preferences;
    protected ProgressDialog progressDialog;
    protected NotificationManager notificationManager;
    protected IMApplication imApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        preferences=getSharedPreferences(Constant.LOGIN_SET,0);
        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        progressDialog=new ProgressDialog(context);
       imApplication=(IMApplication)getApplication();
        imApplication.addActivity(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public IMApplication getIMApplication() {
        return imApplication;
    }

    @Override
    public void stopService() {
        //好友联系人服务
        Intent contacterService=new Intent(context, IMContactService.class);
        context.stopService(contacterService);

        //聊天服务
        Intent chatService=new Intent(context, IMChatService.class);
        context.stopService(chatService);

        //自动连接服务
        Intent reConnectService=new Intent(context, ReConnectService.class);
        context.stopService(reConnectService);

        //系统消息连接服务
        Intent imSystemService=new Intent(context, IMSystemMsgService.class);
        context.stopService(imSystemService);
    }

    @Override
    public void startService() {

        //好友联系人服务
        Intent contacterService=new Intent(context, IMContactService.class);
        context.startService(contacterService);

        //聊天服务
        Intent chatService=new Intent(context, IMChatService.class);
        context.startService(chatService);

        //自动连接服务
        Intent reConnectService=new Intent(context, ReConnectService.class);
        context.startService(reConnectService);

        //系统消息连接服务
        Intent imSystemService=new Intent(context, IMSystemMsgService.class);
        context.startService(imSystemService);
    }

    @Override
    public boolean validateInternet() {
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager==null){
            openWirelessSet();
            return false;
        }else {
            NetworkInfo[]infos=manager.getAllNetworkInfo();
            if(infos!=null){
                for(int i=0;i<infos.length;i++){
                    if(infos[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        openWirelessSet();
        return false;

    }

    @Override
    public boolean hasInternetConnected() {
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager!=null){
            NetworkInfo networkInfo=manager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isConnectedOrConnecting()){
                return true;
            }

        }
        return false;
    }

    @Override
    public void isExit() {
        new AlertDialog.Builder(context).setTitle("确定退出")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService();
                        imApplication.exit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public boolean hasLocationGPS() {
        LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean hasLocationNetWork() {
        LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void checkMemoryCard() {
       if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
          new AlertDialog.Builder(context).setTitle(R.string.prompt)
                  .setMessage("请检查内存卡").setPositiveButton(R.string.menu_settings,new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                  Intent intent=new Intent(Settings.ACTION_SETTINGS);
                  context.startActivity(intent);
              }
          })
           .setNegativeButton("退出", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
                   imApplication.exit();
               }
           }).create().show();
       }
    }

    @Override
    public void showToast(String text, int longTime) {
        Toast.makeText(context,text,longTime).show();
    }

    @Override
    public void showToast(String text) {
         Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public SharedPreferences getLoginUserSharePre() {
        return preferences;
    }

    @Override
    public void saveLoginConfig(LoginConfig loginConfig) {
        preferences.edit().putString(Constant.XMPP_HOST,loginConfig.getXmppHost()).commit();
        preferences.edit().putInt(Constant.XMPP_POST,loginConfig.getXmppPort()).commit();
        preferences.edit().putString(Constant.XMPP_SERVICE_NAME,loginConfig.getXmppServiceName()).commit();
        preferences.edit().putString(Constant.USERNAME,loginConfig.getUserName()).commit();
        preferences.edit().putString(Constant.PASSWORD,loginConfig.getPassword()).commit();
        preferences.edit().putBoolean(Constant.IS_AUTOLOGIN,loginConfig.isAutoLogin()).commit();
        preferences.edit().putBoolean(Constant.IS_FIRSTSTART,loginConfig.isFirstStart()).commit();
        preferences.edit().putBoolean(Constant.IS_NOVISIBLE,loginConfig.isNovisible()).commit();
        preferences.edit().putBoolean(Constant.IS_REMEMBER,loginConfig.isRemember()).commit();
        preferences.edit().putBoolean(Constant.IS_ONLINE,loginConfig.isOnline()).commit();
    }

    @Override
    public LoginConfig getLoginConfig() {
        LoginConfig loginConfig=new LoginConfig();
        String host1=preferences.getString(Constant.XMPP_HOST,null);
        String host2=getResources().getString(R.string.xmpp_host);
        loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,getResources().getString(R.string.xmpp_host)));
        loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_POST,getResources().getInteger(R.integer.xmpp_port)));
        loginConfig.setUserName(preferences.getString(Constant.USERNAME,null));
        loginConfig.setPassword(preferences.getString(Constant.PASSWORD,null));
        loginConfig.setXmppServiceName(preferences.getString(Constant.XMPP_SERVICE_NAME,getResources().getString(R.string.xmpp_service_name)));
        loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,getResources().getBoolean(R.bool.is_autologin)));
        loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,getResources().getBoolean(R.bool.is_remember)));
        loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,getResources().getBoolean(R.bool.is_novisible)));
        return loginConfig;
    }

    @Override
    public boolean getUserOnlineState() {
        return preferences.getBoolean(Constant.IS_ONLINE,true);
    }

    @Override
    public void setUserOnlineState(boolean isOnline) {
       preferences.edit().putBoolean(Constant.IS_ONLINE,isOnline).commit();
    }

    @Override
    public void setNoticType(int iconId, String contentTitle, String contentText, Class activity, String from) {
        /*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent=new Intent(this,activity);
        notifyIntent.putExtra("to",from);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notifyIntent,0);
        /* 创建Notication，并设置相关参数 */
        Notification notification=new Notification();
        //点击自动消失
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        notification.icon=iconId;
        notification.tickerText=contentText;
        /* 设置notification发生时同时发出默认声音 */
        notification.defaults=Notification.DEFAULT_SOUND;
        notification.setLatestEventInfo(this,contentTitle,contentText,pendingIntent);
        notificationManager.notify(0,notification);
    }

    /**
     * 打开网络设置
     *
     */
    public void openWirelessSet(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.prompt).setMessage(context.getString(R.string.check_connection))
                .setPositiveButton(R.string.menu_settings,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.close,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }
    /**
     * 关闭键盘事件
     */
    public void closeInput(){
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null && this.getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
