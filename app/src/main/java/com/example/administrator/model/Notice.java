package com.example.administrator.model;

import com.example.administrator.common.Constant;
import com.example.administrator.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/19.
 * Comparable:
 * 此接口强行对实现它的每个类的对象进行整体排序。
 * 此排序被称为该类的自然排序 ，类的 compareTo 方法被称为它的自然比较方法 。
 */
public class Notice implements Serializable,Comparable<Notice>{
    private static final long serialVersionUID = 1L;
    public static final int ADD_FRIEND=1;   //添加好友请求
    public static final int SYS_MSG=2;      //消息
    public static final int CHAT_MSG=3;    //聊天消息
    public static final int READ=0;
    public static final int UNREAD=1;
    public static final int ALL=2;        //所有消息

    private String id;  //主键
    private String title;  //标题
    private String content;  //内容
    private Integer status;   //状态    0已读 1未读
    private String from;    //通知来源
    private String to;     //通知去向
    private String noticTime;   //通知时间
    private Integer noticeType;   //通知类型  1.好友请求 2.系统消息

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNoticTime() {
        return noticTime;
    }

    public void setNoticTime(String noticTime) {
        this.noticTime = noticTime;
    }

    public Integer getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(Integer noticeType) {
        this.noticeType = noticeType;
    }

    @Override
    public int compareTo(Notice another) {
        if(null==this.getNoticTime()||null==another.getNoticTime() ){
            return 0;
        }
        String format=null;
        String time1="";
        String time2="";
        if(this.getNoticTime().length()==another.getNoticTime().length()&& this.getNoticTime().length()==23){
            time1=this.getNoticTime();
            time2=another.getNoticTime();
            format= Constant.MSG_FORMAT;
        }else {
            time1=this.getNoticTime().substring(0,19);
            time2=another.getNoticTime().substring(0,19);
        }
        Date date1= DateUtil.str2Date(time1,format);
        Date date2=DateUtil.str2Date(time2,format);
        if(date1.before(date2)){
            return 1;
        }
        if(date2.before(date1)){
            return -1;
        }
        return 0;
    }
}
