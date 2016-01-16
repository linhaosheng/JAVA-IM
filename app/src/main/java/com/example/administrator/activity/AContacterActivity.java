package com.example.administrator.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.administrator.common.Constant;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.Notice;
import com.example.administrator.model.User;
import com.example.administrator.util.DateUtil;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
public abstract class AContacterActivity extends ActivitySupport{
    private static final String TAG="AContacterActivity";

    private ContacterReciver reciver;
    protected int noticeNum=0 ;//通知数量，未读消息数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化 reciver
     */
    public void init(){
        reciver=new ContacterReciver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(reciver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.ROSTER_ADDED);
        filter.addAction(Constant.ROSTER_DELETED);
        filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
        filter.addAction(Constant.ROSTER_UPDATED);
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        //好友请求
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.ACTION_SYS_MSG);

        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        registerReceiver(reciver,filter);
        super.onResume();
    }

    private class ContacterReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
           String action= intent.getAction();

            User user=intent.getParcelableExtra(User.userKey);
        //    Notice notice=(Notice)intent.getSerializableExtra("notice");
            if(Constant.ROSTER_ADDED.equals(action)){
             addUserReceive(user);
            }else if(Constant.ROSTER_DELETED.equals(action)){
              deleteUserReceive(user);
            }else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)){
                changePresenceReceive(user);
            }else if (Constant.ROSTER_UPDATED.equals(action)){
                updateUserReceive(user);
            }else if (Constant.ROSTER_SUBSCRIPTION.equals(action)){
                subscripUserReceive(intent.getStringExtra(Constant.ROSTER_SUB_FROM));
            }else if(Constant.NEW_MESSAGE_ACTION.equals(action)){
                // intent.putExtra("noticeId", noticeId);
             //   String noticeId=intent.getStringExtra("noticeId");
                Notice notice=(Notice)intent.getSerializableExtra("notice");
                msgReceive(notice);

            }else if (Constant.ACTION_RECONNECT_STATE.equals(action)){
                boolean isSuccess=intent.getBooleanExtra(Constant.RECONNECT_STATE,true);
                 handReConnect(isSuccess);
            }
        }
    }

    /**
     * roster添加了一个subcriber（订阅）
     * @param user
     */
    protected abstract void addUserReceive(User user);

    /**
     * roster删除了一个subscriber
     * @param user
     */
    protected abstract void deleteUserReceive(User user);

    /**
     * roster中的一个subscriber的状态信息信息发生了改变
     * @param user
     */
    protected abstract void changePresenceReceive(User user);

    /**
     * roster的一个subscriber信息更新
     * @param user
     */
    protected abstract void updateUserReceive(User user);

    /**
     * 收到一个好友添加请求
     * @param subFrom
     */
    protected abstract void subscripUserReceive(String subFrom);

    /**
     * 有新消息进来
     * @param notice
     */
    protected abstract void msgReceive(Notice notice);

    /**
     * 重连接
     * @param isSuccess
     */
    protected abstract void handReConnect(boolean isSuccess);
    /**
     * 回复一个presence信息给用户
     */
    protected void sendSubscribe(Presence.Type type,String to){
        Presence presence=new Presence(type);
        presence.setTo(to);
        XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
    }
    /**
     * 修改好友的昵称
     */
    protected void setNickName(User user,String nickName){
        ContacterManager.setNickName(user,nickName,XmppConnectionManager.getInstance().getConnection());
    }
    /**
     * 把一个好友移到另外的组中
     */
    protected void addUserToOtherGroup(final User user,final String groupName){
        if(user==null){
            return ;
        }
        if(StringUtil.notEmpty(groupName)&&Constant.ALL_FRIEND!=groupName &&Constant.NO_GROUP_FRIEND!=groupName){
            ContacterManager.addUserToGroup(user,groupName,XmppConnectionManager.getInstance().getConnection());
        }

    }

    /**
     * 把一个好友从组中删除
     * @param user
     * @param groupName
     */
    protected void removeUserFromGroup(User user,String groupName){
        if(user==null){
            return ;
        }
        if(StringUtil.notEmpty(groupName)&&!Constant.ALL_FRIEND.equals(groupName)&& !Constant.NO_GROUP_FRIEND.equals(groupName)){
            ContacterManager.removeUserFromGroup(user,groupName,XmppConnectionManager.getInstance().getConnection());
        }
    }

    /**
     * 添加一个联系人
     * @param userId    联系人JID
     * @param nickName      联系人昵称
     * @param groups           联系人添加到哪些组
     */
    protected void createSubscriber(String userId,String nickName,String[]groups){
        try{
            XmppConnectionManager.getInstance().getConnection().getRoster().createEntry(userId,nickName,groups);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除一个联系人
     * @param userJid
     */
    protected void removeSubscriber(String userJid){
        ContacterManager.deleteUser(userJid);
    }

    /**
     * 修改一个组名
     * @param olderGroupName
     * @param newGroupName
     */
    protected void updateGroupName(String olderGroupName,String newGroupName){
        XmppConnectionManager.getInstance().getConnection().getRoster().getGroup(olderGroupName).setName(newGroupName);
    }

    /**
     * 添加分组
     * @param newGroupName
     */
    protected void addGroup(String newGroupName){
        ContacterManager.addGroup(newGroupName,XmppConnectionManager.getInstance().getConnection());
    }

    /**
     * 创建一个聊天
     * @param user
     */
    protected void createChat(User user){
        Intent intent=new Intent(context,ChatActivity.class);
        intent.putExtra("to",user.getJID());
        startActivity(intent);
    }

    /**
     *判断用户名是否存在
     * @param userJid
     * @param groups
     * @return
     */
    protected boolean isExitJid(String userJid,List<ContacterManager.MRosterGroup>groups){
       for(ContacterManager.MRosterGroup g:groups){
           List<User>users=g.getUsers();
           if(users!=null && users.size()>0){
               for(User u:users){
                   if(u.getJID().equals(userJid)){
                       return true;
                   }
               }
           }
       }
        return false;
    }


}
