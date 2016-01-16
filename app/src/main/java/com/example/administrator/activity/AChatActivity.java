package com.example.administrator.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.administrator.common.Constant;
import com.example.administrator.manager.MessageManager;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.Notice;
import com.example.administrator.util.DateUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 * 聊天对话
 */
public abstract class AChatActivity extends ActivitySupport{
    public Chat chat=null;
    private List<IMMessage>messages_pool=null;
    public String to;  //聊天对象
    private static int pageSize =10;
    private List<Notice>noticeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        to=getIntent().getStringExtra("to");
        if(to==null)
            return ;
        chat= XmppConnectionManager.getInstance().getConnection().getChatManager().createChat(to,null);
    }

    @Override
    protected void onResume() {
        //第一次查询
        messages_pool= MessageManager.getInstance(context).getMessageListByFrom(to,1,pageSize);
        if(null!=messages_pool && messages_pool.size()>0){
            Collections.sort(messages_pool);
        }
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        registerReceiver(receiver, filter);
        //更新与所有人的通知
        NoticeManager.getInstance(context).updateStatusByFrom(to,Notice.READ);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
               String action=intent.getAction();
            if(Constant.NEW_MESSAGE_ACTION.equals(action)){
                IMMessage message=intent.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
                messages_pool.add(message);
                receiveNewMessage(message);
                refreshMessage(messages_pool);
            }
        }
    };

    protected abstract void receiveNewMessage(IMMessage message);
    protected abstract void refreshMessage(List<IMMessage> messages);
    protected List<IMMessage>getMessages(){
        return messages_pool;
    }

    /**
     * 发送消息
     * @param messageContent
     * @throws XMPPException
     */
    protected void sendMessage(String messageContent) throws XMPPException{
        String time= DateUtil.date2Str(Calendar.getInstance(),Constant.MSG_FORMAT);
        Message message=new Message();
        message.setProperty(IMMessage.KEY_TIME,time);
        message.setBody(messageContent);
        chat.sendMessage(message);

        IMMessage newMessage=new IMMessage();
        newMessage.setMsgType(1);
        newMessage.setFromSubJid(chat.getParticipant());
        newMessage.setTime(time);
        newMessage.setContent(messageContent);
        messages_pool.add(newMessage);
        MessageManager.getInstance(context).saveIMMessage(newMessage);
        // MChatManager.message_pool.add(newMessage);
        //刷新视图
         refreshMessage(messages_pool);
    }
    /**
     * 下拉加载信息 true 返回成功，false 数据已经全部加载，全部查完
     */

    protected Boolean addNewMessage(){
        List<IMMessage>newMsgList=MessageManager.getInstance(context).getMessageListByFrom(to,messages_pool.size(),pageSize);
         if(newMsgList.size()>0&& newMsgList!=null){
             messages_pool.addAll(newMsgList);
             Collections.sort(messages_pool);
             return true;
         }
        return false;
        }
    /**
     * 刷新视图
     */
    public void resh(){
        refreshMessage(messages_pool);
    }
}
