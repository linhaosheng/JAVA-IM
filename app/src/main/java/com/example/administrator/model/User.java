package com.example.administrator.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jivesoftware.smack.packet.RosterPacket;

/**
 * Created by Administrator on 2015/9/23.
 * intent可以携带传递Parcel数据，需要实现三个方法 . 1、describeContents()返回0就可以.
 * 2、将需要的数据写入Parcel中，框架调用这个方法传递数据. 3、重写外部类反序列化该类时调用的方法.
 */
public class User implements Parcelable {

    public static final String userKey="lovesong_user";

    private String name;
    private String JID;
    private static RosterPacket.ItemType type;
    private String status;
    private String from;
    private String groupNmae;

    /**
     * 用户状态对应的图片
     */
    private int imgId;
    /**
     * group的size
     */
    private int size;
    /**
     *  是否在线
     */
    private boolean available;

    public static RosterPacket.ItemType getType() {
        return type;
    }

    public static void setType(RosterPacket.ItemType type) {
        User.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJID() {
        return JID;
    }

    public void setJID(String JID) {
        this.JID = JID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupNmae() {
        return groupNmae;
    }

    public void setGroupNmae(String groupNmae) {
        this.groupNmae = groupNmae;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(JID);
        dest.writeString(name);
        dest.writeString(from);
        dest.writeString(status);
        dest.writeInt(available ? 1 : 0);
    }
   public static final Parcelable.Creator<User>CREATOR=new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            User user=new User();
            user.JID=source.readString();
            user.name=source.readString();
            user.from=source.readString();
            user.status=source.readString();
            user.available=source.readInt()==1 ? true:false;
            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User clone(){
        User user=new User();
        user.setAvailable(User.this.available);
        user.setFrom(User.this.from);
        user.setGroupNmae(User.this.groupNmae);
        user.setImgId(User.this.imgId);
        user.setJID(User.this.JID);
        user.setName(User.this.name);
        user.setStatus(User.this.status);
        user.setSize(User.this.size);
        return user;
    }
}
