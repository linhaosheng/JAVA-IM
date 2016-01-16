package com.example.administrator.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.manager.UserManager;
import com.example.administrator.model.LoginConfig;
import com.example.administrator.task.ThreadPoolManager;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smackx.packet.VCard;

import java.io.InputStream;

/**
 * Created by Administrator on 2015/9/19.
 * 用户资料查看
 */
public class UserInfoActivity extends ActivitySupport{

    private ImageView titleBack,userImageView;
    private LinearLayout user_info_detail, user_info_edit;
    private Button edit_btn,finish_btn;
    private TextView firstNameView,nickNameView,orgNameView,orgUnitView,mobileView,emailHomeView,discView;
    private EditText firstNameEdit,nickNameEdit,orgNameEdit,orgUnitEdit,mobileEdit,emailHomeEdit,discEdit;
    private UserManager userManager;
    private LoginConfig loginConfig;
    private VCard vCard;
    private ThreadPoolManager poolManager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        init();
    }

    public void init(){
        poolManager=ThreadPoolManager.getInstance();
        userManager=UserManager.getInstance(this);
        loginConfig=getLoginConfig();
        userImageView=(ImageView)findViewById(R.id.userimage);
        firstNameView=(TextView)findViewById(R.id.firstname);
        nickNameView=(TextView)findViewById(R.id.nickname);
        orgNameView=(TextView)findViewById(R.id.orgname);
        orgUnitView=(TextView)findViewById(R.id.orgunit);
        mobileView=(TextView)findViewById(R.id.mobile);
        emailHomeView=(TextView)findViewById(R.id.emailhome);
        discView=(TextView)findViewById(R.id.disc);

        firstNameEdit=(EditText)findViewById(R.id.e_firstname);
        nickNameEdit=(EditText)findViewById(R.id.e_nickname);
        orgNameEdit=(EditText)findViewById(R.id.e_orgname);
        orgUnitEdit=(EditText)findViewById(R.id.e_orgunit);
        mobileEdit=(EditText)findViewById(R.id.e_emailhome);
        discEdit=(EditText)findViewById(R.id.e_disc);

        String jid= StringUtil.getJidByName(loginConfig.getUserName(),loginConfig.getXmppServiceName());
        vCard=userManager.getUserVCard(jid);
        InputStream is=userManager.getUserImage(jid);
        if(is!=null){
            Bitmap bitmap= BitmapFactory.decodeStream(is);
            userImageView.setImageBitmap(bitmap);
        }
        setVCardView(vCard);

        titleBack=(ImageView)findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });

        user_info_detail=(LinearLayout)findViewById(R.id.user_info_detail);
        user_info_edit=(LinearLayout)findViewById(R.id.user_info_edit);

        edit_btn=(Button)findViewById(R.id.edit_btn);
        finish_btn=(Button)findViewById(R.id.finsh_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish_btn.setVisibility(View.VISIBLE);
                user_info_edit.setVisibility(View.VISIBLE);
                user_info_detail.setVisibility(View.GONE);
                discView.setVisibility(View.GONE);
                discEdit.setVisibility(View.VISIBLE);
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        vCard.setFirstName(firstNameEdit.getText().toString());
                        vCard.setNickName(nickNameEdit.getText().toString());
                        vCard.setOrganization(orgUnitEdit.getText().toString());
                        vCard.setOrganizationUnit(orgUnitEdit.getText().toString());
                        vCard.setField("MOBILE", mobileEdit.getText().toString());
                        vCard.setEmailHome(mobileEdit.getText().toString());
                        vCard.setField("DESC", discEdit.getText().toString());
                        vCard = userManager.saveUserVCard(vCard);

                        if (vCard != null) {
                            setVCardView(vCard);
                            finish_btn.setVisibility(View.GONE);
                            user_info_edit.setVisibility(View.GONE);
                            user_info_detail.setVisibility(View.VISIBLE);
                            edit_btn.setVisibility(View.VISIBLE);
                            discView.setVisibility(View.VISIBLE);
                            discEdit.setVisibility(View.GONE);
                            showToast("用户信息已保存");
                        } else {
                            showToast("用户更新信息失败");
                        }
                    }

                };
                poolManager.addTask(runnable);
            }
        });
    }
    private void setVCardView(VCard vCard){
        firstNameView.setText(vCard.getFirstName());
        nickNameView.setText(vCard.getNickName());
        orgUnitView.setText(vCard.getOrganizationUnit());
        orgNameView.setText(vCard.getOrganization());
        mobileView.setText(vCard.getField("MOBILE"));
        emailHomeView.setText(vCard.getEmailHome());
        discView.setText(vCard.getField("DESC"));

        firstNameEdit.setText(vCard.getFirstName());
        nickNameEdit.setText(vCard.getNickName());
        orgUnitEdit.setText(vCard.getOrganizationUnit());
        orgNameEdit.setText(vCard.getOrganization());
        mobileEdit.setText(vCard.getField("MOBILE"));
        emailHomeView.setText(vCard.getEmailHome());
        discView.setText(vCard.getField("DESC"));

    }
}
