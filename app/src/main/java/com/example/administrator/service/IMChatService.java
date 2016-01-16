package com.example.administrator.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.administrator.activity.ChatActivity;
import com.example.administrator.activity.R;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.MessageManager;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.Notice;
import com.example.administrator.util.DateUtil;
import com.example.administrator.util.NoticeUtil;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import java.util.Calendar;

/**
 * Created by Administrator on 2015/9/21.
 */
public class IMChatService extends Service {

    private Context context;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        context=this;
        initChatManager();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initChatManager(){
        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        XMPPConnection conn= XmppConnectionManager.getInstance().getConnection();
        conn.addPacketListener(pListener,new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat));
    }
  PacketListener pListener=new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            org.jivesoftware.smack.packet.Message message=(org.jivesoftware.smack.packet.Message)packet;
            if(message!=null &&message.getBody()!=null && !message.getBody().equals("null")){
                IMMessage msg=new IMMessage();
                String time= DateUtil.date2Str(Calendar.getInstance(), Constant.MSG_FORMAT);
                msg.setTime(time);
                msg.setContent(message.getBody());
                if(org.jivesoftware.smack.packet.Message.Type.error==message.getType()){
                    msg.setType(IMMessage.ERROR);
                }else{
                    msg.setType(IMMessage.SUCCESS);
                }
                String from=message.getFrom().split("/")[0];
                msg.setFromSubJid(from);
                msg.setType(0);
                //生成通知
                NoticeManager manager=NoticeManager.getInstance(context);
                Notice notice=new Notice();
                notice.setTitle("回话消息");
                notice.setNoticeType(Notice.CHAT_MSG);
                notice.setContent(message.getBody());
                notice.setFrom(from);
                notice.setStatus(Notice.UNREAD);
                notice.setNoticTime(time);

                //历史记录
                IMMessage newMessage=new IMMessage();
                newMessage.setMsgType(0);
                newMessage.setFromSubJid(from);
                newMessage.setContent(message.getBody());
                newMessage.setTime(time);
                MessageManager.getInstance(context).saveIMMessage(newMessage);

               long noticeId=manager.saveNotice(notice);
                if(noticeId!=-1){
                    Intent intent=new Intent(Constant.NEW_MESSAGE_ACTION);
                    intent.putExtra(IMMessage.IMMESSAGE_KEY,msg);
                    notice.setId("" +"noticeId");
                    intent.putExtra("notice",notice);
                    sendBroadcast(intent);
                    NoticeUtil noticeUtil=NoticeUtil.getInstance();
                     noticeUtil.setNoticeType(R.drawable.im, getResources().getString(R.string.new_message), notice.getContent(), ChatActivity.class, notice.getFrom(), context, notificationManager);
                }
             }
             }
    };

}
