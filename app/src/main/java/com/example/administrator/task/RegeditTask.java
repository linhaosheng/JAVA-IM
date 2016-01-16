package com.example.administrator.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.administrator.activity.IActivitySuppost;
import com.example.administrator.activity.LoginActivity;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.RegisterConfig;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.IQ;

/**
 * Created by Administrator on 2015/9/20.
 */
public class RegeditTask extends AsyncTask<IQ,Integer,IQ> {
    private static final String TAG="RegeditTask";
    private ProgressDialog dialog;
    private Context context;
    private IActivitySuppost activitySuppost;
    private RegisterConfig registerConfig;

    public RegeditTask(IActivitySuppost activitySuppost,RegisterConfig registerConfig){
        dialog=activitySuppost.getProgressDialog();
        this.activitySuppost=activitySuppost;
        context=activitySuppost.getContext();
        this.registerConfig = registerConfig;
    }
    @Override
    protected void onPreExecute() {
        dialog.setTitle("请稍等");
        dialog.setMessage("正在注册");
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(IQ result) {
        dialog.dismiss();
        if (result == null) {
           Toast.makeText(context,"服务器连接失败",Toast.LENGTH_SHORT).show();
        } else if (result.getType() == IQ.Type.RESULT) {
            Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } else {
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                Toast.makeText(context,"IQ.Type.ERROR: "+result.getError().toString(),Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context,"IQ.Type.ERROR: "+result.getError().toString(),Toast.LENGTH_SHORT).show();

            }
        }
    }
    @Override
    protected IQ doInBackground(IQ... params) {
      return regedit();
    }

    public  IQ regedit() {
        XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo("183.58.237.131");
        registration.setUsername(registerConfig.getRegedit_userName());
        registration.setPassword(registerConfig.getRegedit_password());
        registration.addAttribute("android", "geolo_createUser_android");//不能为空
        PacketFilter filter = new AndFilter(new PacketIDFilter(
                registration.getPacketID()), new PacketTypeFilter(IQ.class));

        PacketCollector collector = connection.createPacketCollector(filter);
        connection.sendPacket(registration);
        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();    // 停止请求results（是否成功的结果）

        return result;
    }
}
