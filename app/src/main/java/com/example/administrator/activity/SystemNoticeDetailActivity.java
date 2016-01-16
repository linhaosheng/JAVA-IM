package com.example.administrator.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.manager.NoticeManager;
import com.example.administrator.model.Notice;

/**
 * Created by Administrator on 2015/9/28.
 */
public class SystemNoticeDetailActivity extends ActivitySupport {
    private ImageView titleBack;
    private String noticeId;
    private TextView ivTitleName;
    private TextView noticeTiitle;
    private TextView notice_content;
    private Button delBtn;
    private Notice notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_notice_detail);
        init();
    }

    public void init(){
        titleBack=(ImageView)findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);    //该结果被MyNotice所获取表名回传成功
                finish();
            }
        });
        // 获取传值
        noticeId=getIntent().getStringExtra("notice_id");
        notice= NoticeManager.getInstance(context).getNoticeById(noticeId);
        //修改已读信息
        NoticeManager.getInstance(context).updateStatus(noticeId,Notice.READ);
        //head
        ivTitleName=(TextView)findViewById(R.id.ivTitleName);
        //小标题
        noticeTiitle=(TextView)findViewById(R.id.notice_title);
        noticeTiitle.setText(notice.getTitle());
        //内容
        notice_content=(TextView)findViewById(R.id.notice_content);
        notice_content.setText(notice.getContent());
        //删除
        delBtn=(Button)findViewById(R.id.delHistory);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeManager.getInstance(context).delById(noticeId);
                setResult(1);
                finish();
            }
        });
    }
}
