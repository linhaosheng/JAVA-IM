package com.example.administrator.common;

import com.example.administrator.util.StringUtil;

/**
 * Created by Administrator on 2015/9/13.
 */
public class Constant {
    /**
     * 登录提示
     */
    public static final int LOGIN_SUCCESS=0;  //成功
    public static final int HAS_NEW_VERSION=1;//发现新版本
    public static final int IS_NEW_VERSION=2; //当前版本为最新版
    public static final int LOGIN_ERROR_ACCOUT_PASS=3;//账号或者密码错误
    public static final int SERVER_UNAVAILABLE=4;//无法连接到服务器
    public static final int LOGIN_ERROR=5;//连接失败

    /**
     * 服务器配置
     */
    public static final String LOGIN_SET = "im_login_set";    // 登录设置
    public static final String USERNAME = "username";   //账户
    public static final String PASSWORD = "password";   //密码
    public static final String XMPP_HOST ="xmpp_host"; //地址
    public static final String XMPP_POST="xmpp_post";   //端口号
    public static final String XMPP_SERVICE_NAME ="xmpp_service_name";   //服务名称
    public static final String IS_AUTOLOGIN ="isAutoLogin";  //是否自动登录
    public static final String IS_REMEMBER ="is_remember";  //是否记住
    public static final String IS_NOVISIBLE ="is_novisible";  //是否隐身
    public static final String IS_FIRSTSTART ="is_firststart";  //是否首次登录

    /**
     * 是否在线的SharedPreferences名称
     */
    public static final String PREFENCE_USER_STATE= "prefence_user_state";
    public static final String IS_ONLINE = "is_online";

    /**
     * 精确到毫秒
     */
    public static final String MSG_FORMAT="yyyy-MM-dd HH:mm:ss SSS";
    /**
     * 收到好友邀请请求
     */
    public static final String ROSTER_SUBSCRIPTION ="roster.subscribe";
    public static final String ROSTER_SUB_FROM = "roster.subscribe.from";
    public static final String NOTICE_ID = "notice.id";
    /**
     * 好友列表 组名
     */
    public static final String ALL_FRIEND="所有好友";
    public static final String NO_GROUP_FRIEND="未分组好友";

    public static final String NEW_MESSAGE_ACTION = "roster.newmessage";

    /**
     * 系统消息
     */
    public static final String ACTION_SYS_MSG="action_sys_msg";  //消息类型关键字
    public static final String MSG_TYPE="broadcast";  //消息类型关键字
    public static final String SYS_MSG="sysMsg";   //系统消息关键字
    public static final String SYS_MSG_DIS="系统消息";  //系统消息
    public static final String ADD_FRIEND_REQUESTT="好友请求";   //系统消息关键字

    /**
     * 重连接状态 action
     */
    public static final String ACTION_RECONNECT_STATE="action_reconnect_state";
    /**
     * 描述重连接状态的关机子，寄放的intent的关键字
     */
    public static final String RECONNECT_STATE="reconnect_state";
    /**
     * 描述重连接
     */
    public static final Boolean RECONNECT_STATE_SUCCESS=true;
    public static final Boolean RECONNECT_STATE_FAIL = false;
    /**
     * 花名册有增加的ACTION和KEY
     */
    public static final String ROSTER_ADDED="roster.added";
    public static final String ROSTER_ADDED_KEY ="roster.added.key";

    /**
     * 花名册有删除的ACTION和KEY
     */
    public static final String ROSTER_DELETED = "roster.deleted";
    public static final String ROSTER_DELETED_KEY = "roster.deleted.key";

    /**
     * 花名册有更新的ACTION和KEY
     */
    public static final String ROSTER_UPDATED = "roster.updated";
    public static final String ROSTER_UPDATED_KEY = "roster.updated.key";

    /**
     * 花名册中成员状态有改变的ACTION和KEY
     */
    public static final String ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
    public static final String ROSTER_PRESENCE_CHANGED_KEY = "roster.presence.changed.key";

}
