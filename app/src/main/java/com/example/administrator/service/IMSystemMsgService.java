package com.example.administrator.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;

import com.example.administrator.activity.R;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.Notice;
import com.example.administrator.util.DateUtil;
import com.example.administrator.util.NoticeUtil;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/30.
 * 系统消息服务.
 */
public class IMSystemMsgService extends Service{

    private Context context;
    /* 声明对象变量 */
    private NotificationManager myNotiManager;

    SoundPool soundPool;   //声明SoundPool的引用
    HashMap<Integer,Integer>map;     // 声明一个HashMap来存放声音文件
    int currStreamId;     //当前正播放的stream

    @Override
    public void onCreate() {
        context=this;
        super.onCreate();
        initSysTemMsgManager();
    }

    public void initSysTemMsgManager(){
        initSoundPool();
        XMPPConnection con=XmppConnectionManager.getInstance().getConnection();
        con.addPacketListener(pListener,new MessageTypeFilter(Message.Type.normal));
    }
    //消息监听
    PacketListener pListener=new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
           Message message=(Message)packet;
            if(message.getType()== Message.Type.normal){
                NoticeManager noticeManager=NoticeManager.getInstance(context);
                Notice notice=new Notice();
                //playSound(1,0);   //播放音效
                notice.setTitle("系统消息");
                notice.setNoticeType(Notice.SYS_MSG);
                notice.setFrom(packet.getFrom());
                notice.setContent(message.getBody());
                notice.setNoticTime(DateUtil.date2Str(Calendar.getInstance(), Constant.MSG_FORMAT));
                notice.setTo(packet.getTo());
                notice.setStatus(Notice.UNREAD);

                long noriceId=noticeManager.saveNotice(notice);
                if(noriceId!=-1){
                    Intent intent=new Intent();
                    intent.setAction(Constant.ACTION_SYS_MSG);
                    notice.setId(String.valueOf(noriceId));
                    intent.putExtra("notice",notice);
                    sendBroadcast(intent);
                    NoticeUtil noticeUtil=NoticeUtil.getInstance();
                    noticeUtil.setNotiType(R.drawable.icon,Constant.SYS_MSG_DIS,message.getBody(),IMSystemMsgService.class,context,myNotiManager);
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化声音池的方法
     */
    public void initSoundPool(){
        soundPool=new SoundPool(4, AudioManager.STREAM_MUSIC,0); // 创建SoundPool对象
        map=new HashMap<Integer, Integer>();
        // map.put(1, soundPool.load(this, R.raw.musictest, 1)); //
        // 加载声音文件musictest并且设置为1号声音放入hm中
    }
    //播放声音的方法
    public void playSound(int sound,int loop){   //huodeAudioManager引用
      AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        //获取当前音量
        float streamVolumeCurrent=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取系统最大音量
        float streamVolumeMax=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //计算得到的播放音量
        float volume=streamVolumeCurrent/streamVolumeMax;
        //调用soundPool的play方法来播放声音文件
        currStreamId=soundPool.play(map.get(sound),volume,volume,1,loop,1.0f);

    }
}
