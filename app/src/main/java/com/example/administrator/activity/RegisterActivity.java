package com.example.administrator.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.model.RegisterConfig;
import com.example.administrator.task.RegeditTask;
import com.example.administrator.util.StringUtil;
import com.example.administrator.util.ValidateUtil;

/**
 * Created by Administrator on 2015/9/20.
 */
public class RegisterActivity extends ActivitySupport {

    private EditText et_userName;
    private EditText ed_password;
    private EditText ed_confirm_password;
    private Button confirm_create;
    private Context context;
    private RegisterConfig registerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regedit);
        init();
    }

    public void init(){
        et_userName=(EditText)findViewById(R.id.regedit_ed_username);
        ed_password=(EditText)findViewById(R.id.regedit_et_password);
        ed_confirm_password=(EditText)findViewById(R.id.regedit_et_confirm_password);
        confirm_create=(Button)findViewById(R.id.regedit_create);
        confirm_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()) {
                    //关闭键盘
                    closeInput();
                    registerConfig = new RegisterConfig();
                    registerConfig.setRegedit_userName(et_userName.getText().toString());
                    registerConfig.setRegedit_password(ed_password.getText().toString());
                    RegeditTask regeditTask = new RegeditTask(RegisterActivity.this, registerConfig);
                    regeditTask.execute();
                }
            }
        });

    }
    public boolean check(){
        boolean flag=false;
        if(!ValidateUtil.isEmpty(et_userName,"用户名")){
            flag=true;
        }else if(!ValidateUtil.isEmpty(ed_password,"密码")){
            flag=true;
        }else if (!ValidateUtil.isEmpty(ed_confirm_password,"确认密码")){
            flag=true;
        }else if (!ed_password.getText().toString().equals(ed_confirm_password.getText().toString())){
            ed_confirm_password.setError("密码跟确认密码不相同");
            ed_confirm_password.setFocusable(true);
            ed_confirm_password.requestFocus();
            flag=false;
        }
        return flag;
    }


}
