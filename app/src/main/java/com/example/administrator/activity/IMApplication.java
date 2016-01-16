package com.example.administrator.activity;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/13.
 */
public class IMApplication  extends Application {
    private List<Activity>activityList=new LinkedList<Activity>();

    //往应用里添加Activity
    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void exit(){
    //  XmppConnectionManage
        for(Activity activity: activityList){
            activity.finish();
        }
    }
}
