package com.example.administrator.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2015/9/30.
 */
public class NoticeUtil {

    private static NoticeUtil noticeUtil;
   public static NoticeUtil getInstance(){
       if(noticeUtil==null){
           noticeUtil=new NoticeUtil();
       }
       return noticeUtil;
   }
    /**
     *
     * 发出Notification的method.
     *
     * @param iconId
     *            图标
     * @param contentTitle
     *            标题
     * @param contentText
     *            你内容
     * @param activity
     * @author shimiso
     * @update 2012-5-14 下午12:01:55
     */
    public static void setNotiType(int iconId, String contentTitle,
                             String contentText, Class activity,Context context,NotificationManager myNoticeManager) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent = new Intent(context,activity);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(context, 0,
                notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
        myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
        myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(context, contentTitle, contentText, appIntent);
		/* 送出Notification */
        myNoticeManager.notify(0, myNoti);
    }

    /**
     *
     * @param iconId   图标
     * @param contentTitle    标题
     * @param contentText   内容
     * @param activity
     * @param from
     */
   public static void setNoticeType(int iconId,String contentTitle,String contentText,Class activity,String from,Context context,NotificationManager manager){
       /*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent=new Intent(context,activity);
        notifyIntent.putExtra("to",from);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //创建PendingIntent作为设置递延运行的Activity
        PendingIntent appIntent=PendingIntent.getActivity(context, 0, notifyIntent, 0);
        //创建Notication,并设置相关参数
        Notification notification=new Notification();
        //点击自动取消
        notification.flags=Notification.FLAG_AUTO_CANCEL;
         /* 设置statusbar显示的文字信息 */
        notification.tickerText=contentText;
         /* 设置notification发生时同时发出默认声音 */
        notification.defaults=Notification.DEFAULT_SOUND;
         /* 设置statusbar显示的icon */
        notification.icon=iconId;
        notification.setLatestEventInfo(context,contentTitle,contentText,appIntent);
        manager.notify(0,notification);
    }
}
