package com.example.administrator.manager;

import com.example.administrator.common.Constant;
import com.example.administrator.model.User;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/23.
 */
public class ContacterManager {
    /**
     * 保存着所有联系人的信息
     */
    public static Map<String,User>contacters=null;

    public static void init(Connection connection){
        contacters=new HashMap<String, User>();
        /**
         *  //  RosterEntry
         //  表示Roster（花名册）中的每条记录.它包含了用户的JID，用户名，或用户分配的昵称.
         */

        for(RosterEntry entry:connection.getRoster().getEntries()){
            contacters.put(entry.getUser(),transEntryToUser(entry,connection.getRoster()));
        }
    }

    /**
     * 删除所有的联系人
     */
    public static void destory(){
         contacters=null;
    }

    /**
     * 获得所有人的联系列表
     * @return
     */
   public static List<User>getContacterList(){

 //   if(contacters==null){
 //       throw new RuntimeException("contacters is null");
 //   }
       List<User>userList=new ArrayList<User>();
       if(contacters!=null) {
           for (String key : contacters.keySet()) {
               userList.add(contacters.get(key));
           }
       }
       return userList;
}

    public static List<User>getNoGroupUserList(Roster roster){
     List<User>userList=new ArrayList<User>();
        // 服务器的用户信息改变后，不会通知到unfiledEntries
        if(contacters!=null) {
            for (RosterEntry entry : roster.getUnfiledEntries()) {
                userList.add(contacters.get(entry.getUser()).clone());
            }
        }
        return userList;
}

    /**
     * 获得所有分组的联系人
     * @param roster
     * @return
     */
    public static List<MRosterGroup>getGroups(Roster roster){
     //   if(contacters==null){
    //        throw new RuntimeException("contacters is null");
    //    }
        List<MRosterGroup>groups=new ArrayList<MRosterGroup>();
        groups.add(new MRosterGroup(Constant.ALL_FRIEND,getContacterList()));
        for (RosterGroup group:roster.getGroups()){
            List<User>groupUser=new ArrayList<User>();
           for (RosterEntry entry:group.getEntries()){
               if(contacters!=null) {
                   groupUser.add(contacters.get(entry.getUser()));
               }
           }
          groups.add(new MRosterGroup(group.getName(),groupUser));
        }
        groups.add(new MRosterGroup(Constant.NO_GROUP_FRIEND,getNoGroupUserList(roster)));
        return groups;
    }
    /**
     * 根据RosterEntry创建一个User
     */
    public static User transEntryToUser(RosterEntry entry,Roster roster) {
        User user = new User();
        if (entry.getName() == null) {
            user.setName(StringUtil.getUserNameByJid(entry.getUser()));
        } else {
            user.setName(entry.getName());
        }
        user.setJID(entry.getUser());
        System.out.println(entry.getUser());
        Presence presence = roster.getPresence(entry.getUser());
        user.setFrom(presence.getFrom());
        user.setStatus(presence.getStatus());
        user.setSize(entry.getGroups().size());
        user.setAvailable(presence.isAvailable());
        user.setType(entry.getType());
        return user;
    }

    /**
     * 修改好友的昵称
     * @param nickName
     * @param connection
     */
    public static void setNickName(User user,String nickName,XMPPConnection connection){
           RosterEntry entry=connection.getRoster().getEntry(user.getJID());
           entry.setName(nickName);
    }

    /**
     * 把一个好友添加到一个组中
     * @param user
     * @param groupName
     * @param connection
     */
    public static void addUserToGroup(final User user,final String groupName,final XMPPConnection connection){
                if(groupName==null || user==null){
                    return ;
                }
        // 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
        new Thread(){
              public void run(){
                  RosterGroup group=connection.getRoster().getGroup(groupName);
                  // 这个组已经存在就添加到这个组，不存在创建一个组
                  RosterEntry entry=connection.getRoster().getEntry(user.getJID());
                  try {
                      if(group!=null){
                          if(entry!=null)
                              group.addEntry(entry);
                      }else {
                          RosterGroup newGroup=connection.getRoster().createGroup(groupName);
                          if(entry!=null)
                              newGroup.addEntry(entry);
                      }
                  }catch (Exception e){
                      e.printStackTrace();
                  }
              }
        }.start();
    }

    /**
     * 把一个好友从组中删除
     * @param user
     * @param groupName
     * @param connection
     */
    public static void removeUserFromGroup(final User user,final String groupName,final XMPPConnection connection){
        if(groupName==null || user==null){
            return ;
        }
        new Thread(){
            @Override
            public void run() {
               RosterGroup group=connection.getRoster().getGroup(groupName);
                if(group!=null){
                    try{
                        System.out.println(user.getJID()+"----------------");
                        RosterEntry entry=connection.getRoster().getEntry(user.getJID());
                        if(entry!=null){
                            group.removeEntry(entry);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public static class MRosterGroup{
        private String name;
        private List<User>users;

        public MRosterGroup(String name, List<User> users) {
            this.name = name;
            this.users = users;
        }
       public int getCount(){
           if(users!=null){
               return users.size();
           }else {
               return 0;
           }
       }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }
    /**
     * 根据jid获得用户昵称
     */

    public static User getNickName(String jid,XMPPConnection connection){
        Roster roster=connection.getRoster();
        for(RosterEntry entry:roster.getEntries()){
            String params=entry.getUser();
            if(params.split("/")[0].equals(jid)){
                return transEntryToUser(entry,roster);
            }
        }
        return null;
    }
    /**
     * 添加分组
     */
    public static void addGroup(final String groupName,final XMPPConnection connection){
        if(StringUtil.empty(groupName)){
            return ;
        }
        // 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
      new Thread(){
          @Override
          public void run() {
               try{
                   RosterGroup group=connection.getRoster().getGroup(groupName);
                   if(group!=null){
                       return;
                   }
                   connection.getRoster().createGroup(groupName);
               }catch (Exception e){
                   e.printStackTrace();
               }
          }
      }.start();

    }
    /**
     * 获得所有组名
     */
    public static List<String>getGroupName(Roster roster){

        List<String>groupNames=new ArrayList<String>();
        for(RosterGroup group:roster.getGroups()){
            groupNames.add(group.getName());
        }
        return groupNames;
    }

    /**
     * 根据Jid把用户从花名册中删除
     * @param userJid
     */
    public static void deleteUser(String userJid){

        Roster roster=XmppConnectionManager.getInstance().getConnection().getRoster();
        RosterEntry entry=roster.getEntry(userJid);
        try {
            XmppConnectionManager.getInstance().getConnection().getRoster().removeEntry(entry);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 根据用户Jid得到用户
     * @param userJid
     * @param connection
     * @return
     */
   public static User getByUserJid(String userJid,XMPPConnection connection){
       Roster roster=connection.getRoster();
       RosterEntry entry=connection.getRoster().getEntry(userJid);
       if(entry==null){
           return null;
       }
       User user=new User();
       if(entry.getName()==null){
           user.setName(StringUtil.getUserNameByJid(entry.getUser()));
       }else{
           user.setName(entry.getName());
       }
       user.setJID(entry.getUser());
       System.out.println(entry.getUser());
       Presence presence=roster.getPresence(entry.getUser());
       user.setFrom(presence.getFrom());
       user.setStatus(presence.getStatus());
       user.setSize(roster.getGroups().size());
       user.setAvailable(presence.isAvailable());
       user.setType(entry.getType());
       return user;
   }
}
