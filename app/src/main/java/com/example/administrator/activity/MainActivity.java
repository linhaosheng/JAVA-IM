package com.example.administrator.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.adapter.MainPageAdapter;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.UserManager;
import com.example.administrator.model.LoginConfig;
import com.example.administrator.model.MainPageItem;
import com.example.administrator.task.ThreadPoolManager;
import com.example.administrator.util.StringUtil;

import org.jivesoftware.smackx.packet.VCard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/13.
 */
public class MainActivity extends ActivitySupport {

    private GridView gridView;
    private List<MainPageItem> list;
    private ImageView iv_status;
    private ImageView userimageView;
    private MainPageAdapter adapter;
    private TextView usernameView;
    private LoginConfig loginConfig;
    private UserManager userManager;
    private ContacterReceiver receiver;
    private ThreadPoolManager poolManager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    @Override
    protected void onResume() {
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.ACTION_SYS_MSG);

        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        registerReceiver(receiver,filter);
        if(getUserOnlineState()){
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_online));
        }else{
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_offline));
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    public void init(){
        getIMApplication().addActivity(this);
        poolManager=ThreadPoolManager.getInstance();
        loginConfig=getLoginConfig();
        gridView=(GridView)findViewById(R.id.gridview);
        iv_status=(ImageView)findViewById(R.id.iv_states);
        usernameView=(TextView)findViewById(R.id.username);
        userimageView=(ImageView)findViewById(R.id.userimage);
        setUserView();
        userimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final Intent intent = new Intent();
                intent.setClass(context, UserInfoActivity.class);
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(intent, 1);
                    }
                };
               poolManager.addTask(runnable);
            }
        });

        receiver=new ContacterReceiver();

        loadMenuList();
        adapter=new MainPageAdapter(this);
        adapter.setList(list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              final Intent intent=new Intent();
                switch (position){
                    case 0:   //我的联系人
                        intent.setClass(context,ContacterMainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:   //我的消息
                        intent.setClass(context,MyNoticeActivity.class);
                        startActivity(intent);
                        break;
                    case 2:   //个人通信录
                        intent.setClass(context,PhoneBookActivity.class);
                        startActivity(intent);
                        break;
                    case 4:  //单点登录
                        intent.setClass(context,SSOActivity.class);
                        startActivity(intent);
                        break;
                    case 5:   //个人文件夹
                        intent.setClass(context,PersionFileActivity.class);
                        startActivity(intent);
                    case 8:
                        intent.setClass(context,SetingActivity.class);
                        startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){   // resultCode为回传的标记
            case 1:
                setUserView();
                break;
            default:
                break;
        }
    }

    private void setUserView(){
        String jid= StringUtil.getJidByName(loginConfig.getUserName(),"183.58.237.131");
      //  System.out.println("jid----------"+jid);
        userManager=new UserManager();
        VCard vCard= userManager.getUserVCard(jid);
     //   System.out.println("vCard----------"+vCard);
       InputStream is= userManager.getUserImage(jid);
        if(is!=null){
            Bitmap bitmap= BitmapFactory.decodeStream(is);
            userimageView.setImageBitmap(bitmap);
        }
        if(vCard.getFirstName()!=null){
            usernameView.setText(vCard.getFirstName()+(StringUtil.notEmpty(vCard.getOrganization())?" - " + vCard.getOrganization():""));
        }else{
            usernameView.setText(loginConfig.getUserName()+(StringUtil.notEmpty(vCard.getOrganization())?"-"+vCard.getOrganization():""));
        }

    }
    /**
     * 加载菜单栏
     */
    protected void loadMenuList(){
        list=new ArrayList<MainPageItem>();
        list.add(new MainPageItem("我的联系人",R.drawable.mycontacts));
        list.add(new MainPageItem("我的消息",R.drawable.mynotice));
        list.add(new MainPageItem("个人通信录",R.drawable.p_contact));
        list.add(new MainPageItem("邮件",R.drawable.email));
        list.add(new MainPageItem("单点登录",R.drawable.sso));
        list.add(new MainPageItem("个人文件夹",R.drawable.p_folder));
        list.add(new MainPageItem("我的笔记",R.drawable.mynote));
        list.add(new MainPageItem("我的签到",R.drawable.signin));
        list.add(new MainPageItem("设置",R.drawable.set));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_page_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         Intent intent=new Intent();
        switch (item.getItemId()){
            case R.id.menu_relogin:
                intent.setClass(context,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isExit();
    }
    /**
     * 联系人广播接受者
     */
    private class ContacterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
          String action=intent.getAction();
            if (Constant.ROSTER_SUBSCRIPTION.equals(action)){
                adapter.notifyDataSetChanged();
            }else if(Constant.NEW_MESSAGE_ACTION.equals(action)){
                //添加小气泡
                adapter.notifyDataSetChanged();
            }else if(Constant.ACTION_RECONNECT_STATE.equals(action)){
                boolean isSuccess=intent.getBooleanExtra(Constant.RECONNECT_STATE,false);
                 handReConnect(isSuccess);
            }else if(Constant.ACTION_SYS_MSG.equals(action)){
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 处理重连接返回状态，连接成功改变头像，失败
     * @param isSuccess
     */
    private void handReConnect(boolean isSuccess){
        if(Constant.RECONNECT_STATE_SUCCESS==isSuccess){   //成功
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_online));
             Toast.makeText(context, "网络恢复,用户已上线!", Toast.LENGTH_LONG).show();
        }else if(Constant.RECONNECT_STATE_FAIL==isSuccess){    //失败
            iv_status.setImageDrawable(getResources().getDrawable(
                    R.drawable.status_offline));
             Toast.makeText(context, "网络断开,用户已离线!", Toast.LENGTH_LONG).show();
        }
    }
}
