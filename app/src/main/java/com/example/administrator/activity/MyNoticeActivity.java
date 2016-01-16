package com.example.administrator.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.adapter.NoticeAdapter;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.Notice;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/9/20.
 */
public class MyNoticeActivity extends ActivitySupport{

    private ImageView titleBack;
    private ListView noticeList=null;
    private NoticeAdapter noticeAdapter=null;
    private List<Notice>inviteNotice=new ArrayList<Notice>();
    private NoticeManager noticeManager;
    private ContacterReceiver receiver=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_notice);
        init();
    }

    @Override
    protected void onResume() {
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        filter.addAction(Constant.ACTION_SYS_MSG);
        registerReceiver(receiver,filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
         unregisterReceiver(receiver);
        super.onPause();
    }

    public void init(){
        titleBack=(ImageView)findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        receiver=new ContacterReceiver();
        noticeList=(ListView)findViewById(R.id.my_notice_list);
        noticeManager=NoticeManager.getInstance(context);
        inviteNotice=noticeManager.getNoticeListByTypeAndPage(Notice.ALL);
        noticeAdapter=new NoticeAdapter(context,inviteNotice);
        noticeList.setAdapter(noticeAdapter);
        noticeList.setOnItemClickListener(inviteListClick);
    }

    private AdapterView.OnItemClickListener inviteListClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               final Notice notice=(Notice)view.findViewById(R.id.new_content).getTag();
            //从消息类型判断
            if(Notice.ADD_FRIEND==notice.getNoticeType()&&notice.getStatus()==Notice.UNREAD){  // 添加好友

                 showAddFriendDialog(notice);
            }else if(Notice.SYS_MSG==notice.getNoticeType() &&notice.getStatus()==Notice.UNREAD){ // 系统通知
                Intent intent=new Intent(context,SystemNoticeDetailActivity.class);
                intent.putExtra("notice_id",notice.getId());
                startActivityForResult(intent,1);

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode){    // resultCode为回传的标记
             case 1:
                  refresh();
                 break;
             default:
                 break;
         }
    }

    private class ContacterReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
           Notice notice= (Notice)intent.getSerializableExtra("notice");
         //  Notice notice=(Notice) intent.getParcelableExtra("notice");
       /*     Notice notice=new Notice();
            notice.setId(intent.getStringExtra("id"));
            notice.setNoticTime(intent.getStringExtra("time"));
            notice.setTo(intent.getStringExtra("to"));
            notice.setFrom(intent.getStringExtra("from"));
            notice.setContent(intent.getStringExtra("content"));
            notice.setNoticeType(intent.getIntExtra("noticeType",1));
            notice.setStatus(intent.getIntExtra("status",1));
            notice.setTitle(intent.getStringExtra("title"));
       */

            if(notice!=null &&!"".equals(notice)) {
                inviteNotice.add(notice);
            }
                refresh();
            System.out.print("MyNotice--------------"+notice.getContent());
            System.out.print("MyNoticeAction--------------"+intent.getAction());
        }
    }
    //刷新
    private void refresh(){
        inviteNotice=noticeManager.getNoticeListByTypeAndPage(Notice.ALL);
        Collections.sort(inviteNotice);
        noticeAdapter.setNoticeList(inviteNotice);
        noticeAdapter.notifyDataSetChanged();
    }
    private void showAddFriendDialog(final Notice notice){
        final String subFrom=notice.getFrom();
        new AlertDialog.Builder(context).setMessage(subFrom+"请求加您为好友")
                .setTitle("提示")
                .setPositiveButton("添加",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //接收请求
                        sendSubscribe(Presence.Type.subscribed,subFrom);
                        sendSubscribe(Presence.Type.subscribe,subFrom);
                        NoticeManager noticeManager1=NoticeManager.getInstance(context);
                        noticeManager1.updateAddFriendStatus(notice.getId(),Notice.READ,"已经同意"+ StringUtil.getUserNameByJid(notice.getFrom()+"的好友申请"));

                        refresh();
                    }
                }).setNegativeButton("拒绝",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendSubscribe(Presence.Type.subscribe,subFrom);
                NoticeManager noticeManager1=NoticeManager.getInstance(context);
                noticeManager1.updateAddFriendStatus(notice.getId(),Notice.READ,"已经拒绝"+StringUtil.getUserNameByJid(notice.getFrom()+"的好友请求"));
                refresh();
            }
        }).show();
    }

    /**
     * 回复一个presence信息给用户
     * @param type
     * @param to
     */
    protected void sendSubscribe(Presence.Type type,String to){
           Presence presence=new Presence(type);
        presence.setTo(to);
        XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.my_notice_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                NoticeManager.getInstance(context).delALL();
                break;
            case R.id.menu_relogin:
                Intent intent=new Intent(context,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return  true;
    }
}
