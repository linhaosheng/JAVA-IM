package com.example.administrator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2015/10/7.
 */
public class SetingActivity extends ActivitySupport {

    private Button btn_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        init();
    }
    private void init(){
        btn_exit=(Button)findViewById(R.id.exit_app);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExit();
            }
        });
    }
}
