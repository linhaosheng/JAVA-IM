package com.example.administrator.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.administrator.activity.ChatActivity;
import com.example.administrator.activity.IActivitySuppost;
import com.example.administrator.activity.R;
import com.example.administrator.common.Constant;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.Notice;
import com.example.administrator.util.DateUtil;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.OfflineMessageManager;

import java.util.Iterator;

/**
 * Created by Administrator on 2015/10/5.
 * 离线消息
 */
public class OfflineMsgManager {
    private static OfflineMsgManager offlineMsgManager=null;
    private IActivitySuppost activitySuppost;
    private Context context;

    private OfflineMsgManager(IActivitySuppost activitySuppost){
        this.activitySuppost=activitySuppost;
        this.context=activitySuppost.getContext();
    }

    public static OfflineMsgManager getInstance(IActivitySuppost activitySuppost){
        if(offlineMsgManager==null){
            offlineMsgManager=new OfflineMsgManager(activitySuppost);
        }
        return offlineMsgManager;
    }

    /**
     * 处理离线消息
     * @param connection
     */
    public void dealOfflineMsg(XMPPConnection connection){
        OfflineMessageManager offlineMessage=new OfflineMessageManager(connection);
        try{
            Iterator<Message>iterator=offlineMessage.getMessages();
            Log.i("离线消息的数量",""+offlineMessage.getMessageCount());
            while(iterator.hasNext()){
                Message message=iterator.next();
                Log.i("收到离线消息", "Received from 【" + message.getFrom()
                        + "】 message: " + message.getBody());
                if(message!=null && !message.equals("")){
                    IMMessage msg=new IMMessage();
                    String time=(String)message.getProperty(IMMessage.KEY_TIME);
                    msg.setTime(time);
                    msg.setContent(message.getBody());
                    if(Message.Type.error==message.getType()){
                        msg.setType(IMMessage.ERROR);
                    }else {
                        msg.setType(IMMessage.SUCCESS);
                    }
                 final   String from=message.getFrom().split("/")[0];
                    msg.setFromSubJid(from);
                    //生成通知
                    NoticeManager noticeManager=NoticeManager.getInstance(context);
                  final   Notice notice=new Notice();
                    notice.setTitle("会话消息");
                    notice.setNoticeType(Notice.CHAT_MSG);
                    notice.setContent(message.getBody());
                    notice.setFrom(from);
                    notice.setStatus(Notice.UNREAD);
                    notice.setNoticTime(time==null ? DateUtil.getCurDate() :time);

                    //历史记录
                    IMMessage newMessage=new IMMessage();
                    newMessage.setMsgType(0);
                    newMessage.setFromSubJid(from);
                    newMessage.setContent(message.getBody());
                    newMessage.setTime(time==null ? DateUtil.getCurDate() :time);
                    MessageManager.getInstance(context).saveIMMessage(newMessage);

                    long noticeId=noticeManager.saveNotice(notice);
                    if(noticeId!=-1){
                        Intent intent=new Intent(Constant.NEW_MESSAGE_ACTION);
                        intent.putExtra(IMMessage.IMMESSAGE_KEY,msg);
                        intent.putExtra("notice",noticeId);
                        context.sendBroadcast(intent);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activitySuppost.setNoticType(R.drawable.icon, context.getResources().getString(R.string.new_message), notice.getContent(), ChatActivity.class, from);
                            }
                        },1000);

                    }
                }
            }
            offlineMessage.deleteMessages();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
