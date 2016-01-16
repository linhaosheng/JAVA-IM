package com.example.administrator.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.adapter.MessageListAdapter;

import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.MessageManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.User;


import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.packet.Message;


import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 */
public class ChatActivity extends AChatActivity{

    private ImageView titleBack;
    private EditText messageInput=null;
    private Button messageSendBtn=null;
    private ImageButton userInfo;
    private ImageView iv_status;
    private TextView tvChatTitle;
    private ListView listView;
    private int recordCount;
    private View listHead;
    private Button listHeadButton;
    private String to_name;
    private User user;   //聊天的人
    private MessageListAdapter listAdapter;
    private String localCameraPath="";
    private static final String LOG_TAG="AudioRecordTest";
    //语音文件保存路劲
    private String mVoideFileName=null;
    //按住说话按钮
    private Button mBtnVoide;
    //用于语音播放
    private MediaPlayer mPlayer=new MediaPlayer();
    //用于完成录音
    private MediaRecorder mRecorder=null;
    private static Message message=null;
    private Bitmap bitmap;
    private static final String PATH = "/sdcard/MyVoiceForder/Record/";
    private static final String PATHIMG = "/sdcard/MyImageForder/Record/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        recordCount= MessageManager.getInstance(context).getCharCountWithSb(to);
        if(recordCount<=0){
            listHead.setVisibility(View.GONE);
        }else {
            listHead.setVisibility(View.VISIBLE);
        }
        listAdapter.refreshList(getMessages());
    }

    public void init(){
     titleBack=(ImageView)findViewById(R.id.title_back);
       titleBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
       //
      tvChatTitle=(TextView)findViewById(R.id.to_chat_name);
       user= ContacterManager.getByUserJid(to, XmppConnectionManager.getInstance().getConnection());
      //  System.out.println("ChatActivity--------"+user);
      if(user==null){
            to_name= StringUtil.getUserNameByJid(to);
      }else{
          to_name=user.getName()==null ? user.getJID():user.getName();
      }
        tvChatTitle.setText(to_name);
      userInfo=(ImageButton)findViewById(R.id.use_info);
       userInfo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent();
               intent.setClass(context,FriendInfoActivity.class);
               startActivity(intent);
           }
       });
       listView=(ListView)findViewById(R.id.chat_list);
       listView.setCacheColorHint(0);
       listAdapter=new MessageListAdapter(ChatActivity.this,getMessages(),listView,user,to);

       //界面头
       LayoutInflater mynflater=LayoutInflater.from(context);
       listHead=mynflater.inflate(R.layout.chatlistheader,null);
       listHeadButton=(Button)listHead.findViewById(R.id.buttonChatHistory);
      //  if(getMessages()!=null) {
            listHeadButton.setOnClickListener(chatHistory);
     //   }
       listView.addHeaderView(listHead);
       listView.setAdapter(listAdapter);

       messageInput=(EditText)findViewById(R.id.chat_content);
       messageSendBtn=(Button)findViewById(R.id.chat_send_btn);
       messageSendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              String message= messageInput.getText().toString();
               if("".equals(message)){
                   Toast.makeText(ChatActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
               }else{
                       try{
                           sendMessage(message);
                           messageInput.setText("");
                       }catch (Exception e){
                           showToast("信息发送失败");
                           e.printStackTrace();
                       }
                   closeInput();
               }
           }
       });

   }
    @Override
    protected void receiveNewMessage(IMMessage message) {

    }

    @Override
    protected void refreshMessage(List<IMMessage> messages) {
                 listAdapter.refreshList(messages);
    }

    private View.OnClickListener chatHistory=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context,ChatHistoryActivity.class);
            intent.putExtra("to",to);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.chat_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent();
        switch (item.getItemId()){
            case R.id.menu_return_main_page:
                intent.setClass(context,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_relogin:
                intent.setClass(context,LoginActivity.class);
                startActivity(intent);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return true;


    }
}
