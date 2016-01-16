package com.example.administrator.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.common.Constant;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.LoginConfig;
import com.example.administrator.task.LoginTask;
import com.example.administrator.util.StringUtil;
import com.example.administrator.util.ValidateUtil;


public class LoginActivity extends ActivitySupport {

    private EditText edt_userName;
    private EditText edt_password;
    private CheckBox rememberCb;
    private CheckBox autoLoginCb;
    private CheckBox noVisibleCb;
    private Button btn_login;
    private TextView tv_regedit;
    private TextView tv_forget_password;
    private LoginConfig loginConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //校验SD卡
        checkMemoryCard();
        //检测网络和版本
        validateInternet();
        //初始化xmpp配置
        XmppConnectionManager.getInstance().init(loginConfig);
    }

    /**
     * 初始化
      */
     public void init(){
      preferences=getSharedPreferences(Constant.LOGIN_SET,0);
      rememberCb=(CheckBox)findViewById(R.id.remember);
      loginConfig=getLoginConfig();
      // 如果为自动登录
        if(loginConfig.isAutoLogin()) {
            /**
             *
             if(!StringUtil.empty(loginConfig.getUserName())||!StringUtil.empty(loginConfig.getPassword())) {
             edt_userName.setText(loginConfig.getUserName());
             edt_password.setText(loginConfig.getPassword());
             autoLoginCb.setChecked(loginConfig.isAutoLogin());
             noVisibleCb.setChecked(loginConfig.isNovisible());
             rememberCb.setChecked(loginConfig.isRemember());
             }
             */
            LoginTask loginTask=new LoginTask(LoginActivity.this,loginConfig);
            loginTask.execute();
        }
            rememberCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        autoLoginCb.setChecked(false);
                    }
                }
            });


     edt_userName=(EditText)findViewById(R.id.ui_username_input);
     edt_password=(EditText)findViewById(R.id.ui_password_input);
     autoLoginCb=(CheckBox)findViewById(R.id.autoLogin);
     noVisibleCb=(CheckBox)findViewById(R.id.novisible);
     btn_login=(Button)findViewById(R.id.ui_login_btn);
     tv_forget_password=(TextView)findViewById(R.id.forget_password);
     tv_regedit=(TextView)findViewById(R.id.regedit);
      /**
       * 如果是自动登录，就开始初始化各组件
       */
      edt_userName.setText(loginConfig.getUserName());
      edt_password.setText(loginConfig.getPassword());
      rememberCb.setChecked(loginConfig.isRemember());

      autoLoginCb.setChecked(loginConfig.isAutoLogin());
      noVisibleCb.setChecked(loginConfig.isNovisible());
      btn_login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
                if(checkData()&&validateInternet()){
                    //关闭键盘
                    closeInput();
                    String userName=edt_userName.getText().toString();
                    String password=edt_password.getText().toString();
                    //先记下个组件的目前状态，登录成功后才保存
                    loginConfig.setPassword(password);
                    loginConfig.setUserName(userName);
                    loginConfig.setRemember(rememberCb.isChecked());
                    loginConfig.setAutoLogin(autoLoginCb.isChecked());
                    loginConfig.setNovisible(noVisibleCb.isChecked());

                    LoginTask loginTask=new LoginTask(LoginActivity.this,loginConfig);
                    loginTask.execute();
                }
          }
      });
         tv_regedit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                 context.startActivity(intent);
             }
         });

         tv_forget_password.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(LoginActivity.this,ChangePasswordActivity.class);
                 startActivity(intent);
             }
         });
  }

    private boolean checkData(){
       boolean check=false;
        check=(!ValidateUtil.isEmpty(edt_userName,"登录名")&& !ValidateUtil.isEmpty(edt_password,"密码"));
        return check;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       final EditText xmppHostText=new EditText(getContext());
        xmppHostText.setText(loginConfig.getXmppHost());

       switch (item.getItemId()){
           case R.id.menu_login_set:     //登录设置
                final AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
               dialog.setTitle("服务器设置").setIcon(R.drawable.ic_dialog_info)
                       .setView(xmppHostText)
                       .setMessage("请设置服务器IP地址")
                       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               String xmppHost = StringUtil.doEmpty(xmppHostText.getText().toString());
                               loginConfig.setXmppHost(xmppHost);
                               XmppConnectionManager.getInstance().init(loginConfig);
                               LoginActivity.this.saveLoginConfig(loginConfig);
                           }
                       })
                       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialog.setCancelable(true);
                           }
                       });
               AlertDialog alertDialog=dialog.create();
               alertDialog.show();
               break;
           case R.id.menu_relogin :     //重新登录
               Intent intent=new Intent();
               intent.setClass(getContext(),LoginActivity.class);
               startActivity(intent);
               finish();
               break;
           case R.id.menu_exit:    //退出
               isExit();
               break;
       }
        return true;
    }

    @Override
    public void onBackPressed() {
        isExit();
    }
}
