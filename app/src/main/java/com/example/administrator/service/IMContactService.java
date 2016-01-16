package com.example.administrator.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.example.administrator.activity.MyNoticeActivity;
import com.example.administrator.activity.R;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.Notice;
import com.example.administrator.model.User;
import com.example.administrator.util.DateUtil;
import com.example.administrator.util.NoticeUtil;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Calendar;
import java.util.Collection;

/**
 * Created by Administrator on 2015/9/30.
 * 联系人服务
 */
public class IMContactService extends Service {

    private Roster roster=null;
    private Context context;
    private NotificationManager myNoticeManager;

    @Override
    public void onCreate() {
        context=this;
        addSubscriptionListener();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onStartCommand会告诉系统如何重启服务，如判断是否异常终止后重新启动，在何种情况下异常终止
     * 系统重启时调用该方法
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }
    private void init(){
        myNoticeManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        initRoster();
    }
    /**
     * 初始化花名册,服务重启时，更新花名册
     */
    private void initRoster(){
        roster= XmppConnectionManager.getInstance().getConnection().getRoster();
        roster.removeRosterListener(rosterListener);
        roster.addRosterListener(rosterListener);
        ContacterManager.init(XmppConnectionManager.getInstance().getConnection());
    }

    /**
     * 天机一个监听，监听好友添加请求
     */
   private void addSubscriptionListener(){
       PacketFilter filter=new PacketFilter() {
           @Override
           public boolean accept(Packet packet) {
               if(packet instanceof Presence){
                   Presence presence=(Presence)packet;
                   if(presence.getType().equals(Presence.Type.subscribe)){
                       return true;
                   }
               }
               return false;
           }
       };
       XmppConnectionManager.getInstance().getConnection().addPacketListener(subscriptionPacketListener,filter);
   }
    private PacketListener subscriptionPacketListener=new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            String user=getSharedPreferences(Constant.LOGIN_SET,0).getString(Constant.USERNAME,null);
            if(packet.getFrom().contains(user))
                return ;
            // 如果是自动接收所有请求，则回复一个添加信息
            if(Roster.getDefaultSubscriptionMode().equals(Roster.SubscriptionMode.accept_all)){
                Presence subscription=new Presence(Presence.Type.subscribe);
                subscription.setTo(packet.getFrom());
                XmppConnectionManager.getInstance().getConnection().sendPacket(subscription);
            }else {
                NoticeManager noticeManager=NoticeManager.getInstance(context);
                Notice notice=new Notice();
                notice.setTitle("好友请求");
                notice.setNoticeType(Notice.ADD_FRIEND);
                notice.setContent(StringUtil.getUserNameByJid(packet.getFrom()) + "申请添加您为好友");
                notice.setFrom(packet.getFrom());
                notice.setTo(packet.getTo());
                notice.setNoticTime(DateUtil.date2Str(Calendar.getInstance(),Constant.MSG_FORMAT));
                notice.setStatus(Notice.UNREAD);
                long noticeId=noticeManager.saveNotice(notice);
                System.out.print(noticeId);
                if(noticeId!=-1){
                    Intent intent=new Intent();
                    intent.setAction(Constant.ROSTER_SUBSCRIPTION);
                    notice.setId("" + "noticeId");
                    intent.putExtra("notice", notice);

                    context.sendBroadcast(intent);
                    NoticeUtil noticeUtil=NoticeUtil.getInstance();
                    noticeUtil.setNotiType(R.drawable.icon, "好友请求", StringUtil.getUserNameByJid(packet.getFrom()) + "申请加您为好友", MyNoticeActivity.class, context, myNoticeManager);

                }
            }
        }
    };

    @Override
    public void onDestroy() {
        //释放资源
       XmppConnectionManager.getInstance().getConnection().removePacketListener(subscriptionPacketListener);
       ContacterManager.destory();
        super.onDestroy();
    }

    //A listener that is fired any time a roster is changed or the presence of a user in the roster is changed.
    private RosterListener rosterListener=new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> strings) {
             for(String address:strings){
                 Intent intent=new Intent();
                 intent.setAction(Constant.ROSTER_ADDED);
                 RosterEntry userEntry=roster.getEntry(address);
                 User user=ContacterManager.transEntryToUser(userEntry,roster);
                 ContacterManager.contacters.put(address,user);
                 intent.putExtra(User.userKey,user);
                 sendBroadcast(intent);
             }
        }

        @Override
        public void entriesUpdated(Collection<String> strings) {

            for(String address:strings){
                Intent intent=new Intent();
                intent.setAction(Constant.ROSTER_UPDATED);
                //获得状态的entry
                RosterEntry userEntry=roster.getEntry(address);
                User user=ContacterManager.transEntryToUser(userEntry,roster);
                if(ContacterManager.contacters.get(address)!=null){
                   //这里发布的是更新前的user
                    intent.putExtra(User.userKey,ContacterManager.contacters.get(address));
                    //将发生改变的用户更新到userManager
                    ContacterManager.contacters.remove(address);
                    ContacterManager.contacters.put(address,user);
                }
                sendBroadcast(intent);
                // 用户更新，getEntries会更新
                // roster.getUnfiledEntries中的entry不会更新
            }
        }

        @Override
        public void entriesDeleted(Collection<String> strings) {
             for(String address:strings){
                 Intent intent=new Intent();
                 intent.setAction(Constant.ROSTER_DELETED);
                 User user=null;
                 if (ContacterManager.contacters.containsKey(address)){
                     user=ContacterManager.contacters.get(address);
                     ContacterManager.contacters.remove(address);
                 }
                 intent.putExtra(User.userKey,user);
                 sendBroadcast(intent);
             }
        }

        @Override
        public void presenceChanged(Presence presence) {
               Intent intent=new Intent();
               intent.setAction(Constant.ROSTER_PRESENCE_CHANGED);
            String subscriber=presence.getFrom().substring(0,presence.getFrom().indexOf("/"));
            RosterEntry entry=roster.getEntry(subscriber);
            if(ContacterManager.contacters.containsKey(subscriber)){
                // 将状态改变之前的user广播出去
                intent.putExtra(User.userKey,ContacterManager.contacters.get(subscriber));
                ContacterManager.contacters.remove(subscriber);
                ContacterManager.contacters.put(subscriber,ContacterManager.transEntryToUser(entry,roster));
            }
            sendBroadcast(intent);
        }
    };
}
