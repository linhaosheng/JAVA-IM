package com.example.administrator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.ChangePawwordConfig;
import com.example.administrator.util.StringUtil;
import com.example.administrator.util.ValidateUtil;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by Administrator on 2015/9/20.
 */
public class ChangePasswordActivity extends ActivitySupport {

    private EditText et_newPassword;
    private Button bt_comfirm;
    private ChangePawwordConfig config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chang_password);
    }
    public void init(){
        et_newPassword=(EditText)findViewById(R.id.edit_newPassword);
        bt_comfirm=(Button)findViewById(R.id.confirm_newPassword);
        bt_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPPConnection connection= XmppConnectionManager.getInstance().getConnection();
                try{
                    if(!ValidateUtil.isEmpty(et_newPassword,"新密码")){
                        connection.connect();
                    }
                    connection.getAccountManager().changePassword(et_newPassword.getText().toString());
                    Intent intent=new Intent(ChangePasswordActivity.this,LoginActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
