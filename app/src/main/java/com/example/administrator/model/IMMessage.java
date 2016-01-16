package com.example.administrator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.administrator.common.Constant;
import com.example.administrator.util.DateUtil;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/21.
 */
public class IMMessage implements Parcelable,Comparable<IMMessage>{

    public static final String IMMESSAGE_KEY="immessage.key";
    public static final String KEY_TIME="immessage.time";
    public static final int SUCCESS=0;  //成功发送消息
    public static final int ERROR=1;    //失败发送消息
    private int type;
    private String content;    //聊天的内容
    private String time;      //聊天时间
    /**
     * 存在本地表示与谁聊天
     */
    private String fromSubJid;
    /**
     * 0：接受  1：发送
     */
    private int msgType=0;

    public IMMessage(){
        this.type=SUCCESS;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getFromSubJid() {
        return fromSubJid;
    }

    public void setFromSubJid(String fromSubJid) {
        this.fromSubJid = fromSubJid;
    }

    @Override
    public int compareTo(IMMessage another) {
        if(null==another.getTime() || null==this.getTime()){
            return 0;
        }
        String format=null;
        String time1="";
        String time2="";
        if( this.getTime().length()==another.getTime().length()&&this.getTime().length()==23){
            time1=this.getTime();
            time2=another.getTime();
            format= Constant.MSG_FORMAT;
        }else {
            time1=this.getTime().substring(0,19);
            time2=this.getTime().substring(0,19);
        }
        Date date1= DateUtil.str2Date(time1,format);
        Date date2=DateUtil.str2Date(time2,format);
        /**
         * 有可能有错误应该是
         *  if(date1.before(date2)){
         return 1;
         }
         if(date2.before(date1)){
         return -1;
         }
         return 0;
           }
         */
        //返回对比结果 0为相等，负整数为小于，正整数为大于
        if(date1.before(date2)){
            return -1;
        }
        if(date2.before(date1)){
            return 1;
        }
        return 0;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(fromSubJid);
        dest.writeInt(msgType);
    }

     public static final Parcelable.Creator<IMMessage> CREATOR= new Parcelable.Creator<IMMessage>() {
         @Override
         public IMMessage createFromParcel(Parcel source) {
             IMMessage message=new IMMessage();
             message.setType(source.readInt());
             message.setContent(source.readString());
             message.setFromSubJid(source.readString());
             message.setMsgType(source.readInt());
             message.setTime(source.readString());
             return message;
         }

         @Override
         public IMMessage[] newArray(int size) {
             return new IMMessage[size];
         }
     };
    /**
     * 新消息的构造方法
     */
    public IMMessage(String content,String time,String fromSubJid,int msgType){
        this.content=content;
        this.time=time;
        this.fromSubJid=fromSubJid;
        this.type=type;
    }
}
