package com.example.administrator.manager;

import com.example.administrator.model.LoginConfig;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import java.util.zip.DataFormatException;

/**
 * Created by Administrator on 2015/9/13.
 */
public class XmppConnectionManager {
    //这个类用来连接服务 使用connect()连接
    private XMPPConnection connection;
    //作为与XMPP服务建立连接的配置，它能配置，连接是否使用TLS ，SASL加密
    //包含内嵌类ConnectionConfiguration.SecurityMode
    private static ConnectionConfiguration configuration;
    private static XmppConnectionManager xmppConnectionManager;

    public XmppConnectionManager(){}

    public static XmppConnectionManager getInstance(){
        if(xmppConnectionManager==null){
            xmppConnectionManager=new XmppConnectionManager();
        }
        return xmppConnectionManager;
    }
    public XMPPConnection init(LoginConfig loginConfig){
        //是开发过程中不会弹出一个窗口来显示我们的连接与发送的Packet信息
        Connection.DEBUG_ENABLED=false;
        ProviderManager providerManager=ProviderManager.getInstance();
        configure(providerManager);

        configuration=new ConnectionConfiguration("116.19.68.152",5222,"WIN7-20141126FP");
        configuration.setSASLAuthenticationEnabled(false);   // 不使用SASL验证，设置为false
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        //允许自动连接
        configuration.setReconnectionAllowed(true);
        //允许登录成功后更新在线状态
        configuration.setSendPresence(true);
        // 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        connection=new XMPPConnection(configuration);
        return connection;
        /**
         表示存储了很多RosterEntry的一个花名册，为了易于管理，花名册的项被分配到各个group中
         使用connect.getRoster()获取Roster对象
         Roster.SubscriptionMode的值处理这些请求
         accept_all
         reject_all
         manual:手工处理订阅请求
         创建组:
         RosterGroup group=roster.createGroup("大学");
         再向组中添加RosterEntry对象
         group.addEntry(entry);
         Roster中每条记录包含用户ID，用户名或用户分配 的昵称

         Presence
         表示XMPP状态的packet，每个Presence packet都有一个状态
         */
    }

    /**
     * 返回一个有效的xmpp连接，如果无效则返回异常
     * @return
     */
    public XMPPConnection getConnection(){
        if(connection==null){
            throw new RuntimeException("请先初始化XMPPConnection连接");
        }
        return connection;
    }

    /**
     * 销毁xmpp连接
     */
    public void disConnect(){
        if(connection!=null){
            connection.disconnect();
        }
    }
    public void configure(ProviderManager providerManager){
        /**
         *
         XMPP协议的命名空间：

         jabber:iq:private   -- 私有数据存储，用于本地用户私人设置信息，比如用户备注等。
         jabber:iq:conference  -- 一般会议，用于多个用户之间的信息共享
         jabber:x:encrypted -- 加密的消息，用于发送加密消息
         jabber:x:expire  -- 消息终止
         jabber:iq:time  -- 客户端时间
         jabber:iq:auth  -- 简单用户认证，一般用于服务器之间或者服务器和客户端之间的认证
         jabber:x:roster  -- 内部花名册
         jabber:x:signed  -- 标记的在线状态
         jabber:iq:search -- 用户数据库查询，用于向服务器发送查询请求
         jabber:iq:register -- 注册请求，用于用户注册相关信息
         jabber:x:iq:roster -- 花名册管理
         jabber:x:conference -- 会议邀请，用于向参加会议用户发送开会通知
         jabber:x:event  -- 消息事件
         vcard-temp  -- 临时的vCard,用于设置用户的头像以及昵称等
         */
        // Private Data Storage
        providerManager.addIQProvider("query","jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());
      //Time
        try{
            providerManager.addIQProvider("query","jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));
        }catch (ClassNotFoundException e){
           e.printStackTrace();
        }
        //XHTML
        providerManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());

        //Roster Exchange
        providerManager.addExtensionProvider("x", "jabber:x:roster",new RosterExchangeProvider());

        // Message Events
        providerManager.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
        // Chat State
        providerManager.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        providerManager.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        providerManager.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        providerManager.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());

        //FileTransfer
        providerManager.addIQProvider("si","http://jabber.org/protocol/si",new StreamInitiationProvider());

        //Group Chat Invitations
        providerManager.addExtensionProvider("x","jabber:x:conference",new GroupChatInvitation.Provider());

        //Service Discovery #Items
        providerManager.addIQProvider("query","http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());

        // Data Forms
        providerManager.addExtensionProvider("x","jabber:x:data",new DataFormProvider());
        //MUC User
        providerManager.addExtensionProvider("x","http://jabber.org/protocol/muc#user",new MUCUserProvider());
        //MUC Admin
//        providerManager.addIQProvider("query","http://jabber.org/protocol/muc#admin",new MUCUserProvider());
        //MUC Ower
//        providerManager.addIQProvider("query","http://jabber.org/protocol/muc#owner",new MUCUserProvider());
        //Delayed Delivery
        providerManager.addExtensionProvider("x","jabber:x:delay",new DelayInformationProvider());

        //Version
        try{
            providerManager.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        //User Register
        try {
            providerManager.addIQProvider("query", "jabber:iq:register",Class.forName("org.jivesoftware.smack.packet.IQ"));
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        //VCard
        providerManager.addIQProvider("vCard", "vcard-temp",new VCardProvider());
        //OffLine Message Request
        providerManager.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        providerManager.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());
       // Last Activity
        providerManager.addIQProvider("query", "jabber:iq:last",new LastActivity.Provider());
        //User Search
        providerManager.addIQProvider("query", "jabber:iq:search",new UserSearch.Provider());
        //SharedGroupsInfo
        providerManager.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        providerManager.addExtensionProvider("address","http://jabber.org/protocol/address",new MultipleAddressesProvider());
    }
}
