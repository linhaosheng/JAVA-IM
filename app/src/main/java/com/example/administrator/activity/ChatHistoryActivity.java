package com.example.administrator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.adapter.MsgHisListAdapter;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.MessageManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.User;
import com.example.administrator.util.StringUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/24.
 */
public class ChatHistoryActivity extends ActivitySupport {
    private ImageView titleBack;
    private LinearLayout user_info_detail, user_info_edit;
    private Button edit_btn,finish_btn;
    private List<IMMessage>msgList;
    private MessageManager msgManager;
    private ListView listView;
    private String to;
    private int pageSize=10;
    private int currentPage=1;
    private int pageCount;   //总页数
    private int recordCount;  //记录总数
    private ImageView imageViewLeft;// 上一页
    private ImageView imageViewRight;// 上一页
    private TextView editTextPage;// 当前页
    private Button delBtn;
    private TextView textViewPage;  //总页数
    private User user; // 聊天人
    private User me;    // 聊天人自己
    private TextView ivTitleName;
    private MsgHisListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chathistory);
        init();
    }

    public void init(){
        to=getIntent().getStringExtra("to");
        if(to==null)
            return;
        msgManager=MessageManager.getInstance(context);
        getIMApplication().addActivity(this);
        titleBack=(ImageView)findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 头部标题
        ivTitleName=(TextView)findViewById(R.id.ivTitleName);
        user= ContacterManager.getByUserJid(to, XmppConnectionManager.getInstance().getConnection());
        String data=getResources().getString(R.string.chat_his_with_sb);
        if(null!=user){
            data=String.format(data,user.getName());
        }else {
            data=String.format(data, StringUtil.getUserNameByJid(to));
        }
        ivTitleName.setText(data);
        //分页
        recordCount=MessageManager.getInstance(context).getCharCountWithSb(to);
        pageCount=(recordCount+pageSize-1) /pageSize;
        imageViewLeft=(ImageView)findViewById(R.id.imageViewLeft);
        imageViewRight=(ImageView)findViewById(R.id.imageViewRight);
        editTextPage=(TextView)findViewById(R.id.editTextPage);
        editTextPage.setText(currentPage + "");
        //下一页
        imageViewRight.setOnClickListener(nextClick);
        //上一页
        imageViewLeft.setOnClickListener(preClick);
        //总页数
        textViewPage=(TextView)findViewById(R.id.textViewPage);
        textViewPage.setText(""+pageCount);

        //删除
        delBtn=(Button)findViewById(R.id.buttonDelete);
        delBtn.setOnClickListener(delClick);
        if(msgList !=null && msgList.size()>0){
            Collections.sort(msgList);
            adapter=new MsgHisListAdapter(context,msgList,user,to,me);
        }
        listView=(ListView)findViewById(R.id.listViewHistory);
        msgList=msgManager.getMessageListByFrom(to,currentPage,pageSize);
        if(msgList!=null && msgList.size()>0){
            Collections.sort(msgList);
            adapter=new MsgHisListAdapter(context,msgList,user,to,me);
            listView.setAdapter(adapter);
        }
    }
    private View.OnClickListener nextClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(currentPage>pageCount){
               return;
           }
            currentPage +=1;
            editTextPage.setText(currentPage + "");
            msgList=msgManager.getMessageListByFrom(to,currentPage,pageSize);
            adapter.refreshList(msgList);
        }
    };
    private View.OnClickListener preClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if(currentPage<=1){
                 return;
             }
            currentPage=currentPage-1;
            editTextPage.setText(currentPage+ "");
            msgList=msgManager.getMessageListByFrom(to,currentPage,pageSize);
            adapter.refreshList(msgList);
        }
    };
    private View.OnClickListener delClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            msgManager.deleteChartHisWithSb(to);
            finish();
        }
    };
}
