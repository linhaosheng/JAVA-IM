package com.example.administrator.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.activity.GuideViewActivity;
import com.example.administrator.activity.IActivitySuppost;
import com.example.administrator.activity.MainActivity;
import com.example.administrator.activity.R;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.LoginConfig;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import java.util.Collection;

/**
 * Created by Administrator on 2015/9/13.
 */
public class LoginTask extends AsyncTask<String,Integer,Integer> {

    private String TAG="LoginTask";
    private ProgressDialog dialog;
    private Context context;
    private IActivitySuppost activitySuppost;
    private LoginConfig loginConfig;
    private SharedPreferences preferences=null;
    public LoginTask(IActivitySuppost activitySuppost,LoginConfig loginConfig){
        this.activitySuppost=activitySuppost;
        this.loginConfig=loginConfig;
        dialog=activitySuppost.getProgressDialog();
        this.context=activitySuppost.getContext();
        this.preferences=context.getSharedPreferences(Constant.LOGIN_SET,0);
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("请稍等");
        dialog.setMessage("正在登录...");
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        return login();
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();
        switch (result){
            case Constant.LOGIN_SUCCESS:   //登录成功
                Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                if(loginConfig.isFirstStart()){  //如果是首次启动的
                 intent.setClass(context, GuideViewActivity.class);
                    loginConfig.setFirstStart(false);
                }else {
                    intent.setClass(context, MainActivity.class);
                }
                activitySuppost.saveLoginConfig(loginConfig);  //保存登录配置
                activitySuppost.startService();  //开启各项服务
                context.startActivity(intent);
                break;
            case Constant.LOGIN_ERROR_ACCOUT_PASS:    //登录密码或者账号错误
                Toast.makeText(context,context.getResources().getString(R.string.message_invalid_username_password),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"登录密码或者账号错误");
             break;
            case Constant.SERVER_UNAVAILABLE:   //服务器连接失败
                Toast.makeText(context,context.getResources().getString(R.string.message_server_unavailable),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"服务器连接失败");
                break;
            case Constant.LOGIN_ERROR:   //未知异常
                Toast.makeText(context,context.getResources().getString(R.string.unrecoverable_error),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"未知异常");
                break;
        }
        super.onPostExecute(result);
    }

    /**
     * 登录
     * @return
     */
    private Integer login(){
        String userName=loginConfig.getUserName();
        String password=loginConfig.getPassword();
        try{
            XMPPConnection connection= XmppConnectionManager.getInstance().getConnection();
            connection.connect();
            connection.login(userName,password);
            connection.sendPacket(new Presence(Presence.Type.available));
            if(loginConfig.isNovisible()){ //隐身登录
                Presence presence=new Presence(Presence.Type.unavailable);
                Collection<RosterEntry>roster=connection.getRoster().getEntries();
                for(RosterEntry rosterEntry:roster){
                    presence.setTo(rosterEntry.getUser());
                    connection.sendPacket(presence);
                }
            }
            loginConfig.setUserName(userName);
            if(loginConfig.isRemember()){  //保存密码
                loginConfig.setPassword(password);
            }else {
                loginConfig.setPassword("");
            }
            loginConfig.setOnline(true);
           return Constant.LOGIN_SUCCESS;
        }catch (Exception e){
            if(e instanceof XMPPException){
                XMPPException xmppException=(XMPPException)e;
                final XMPPError error=((XMPPException) e).getXMPPError();
                int errorCode=0;
                if(error!=null){
                    errorCode=error.getCode();
                }
                if(errorCode==401){
                    return Constant.LOGIN_ERROR_ACCOUT_PASS;
                }else if(errorCode==403){
                    return Constant.LOGIN_ERROR_ACCOUT_PASS;
                }else {
                    return Constant.SERVER_UNAVAILABLE;
                }
            }else {
                return Constant.LOGIN_ERROR;
            }
        }

    }
}
