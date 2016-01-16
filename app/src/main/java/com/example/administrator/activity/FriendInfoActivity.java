package com.example.administrator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/9/24.
 */
public class FriendInfoActivity extends ActivitySupport{

    private ImageView titleBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_info);
        init();
    }

    private void init(){
        getIMApplication().addActivity(this);
        titleBack=(ImageView)findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
