package com.example.administrator.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.adapter.ContacterExpandAdapter;
import com.example.administrator.adapter.RecentChartAdapter;
import com.example.administrator.common.Constant;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.manager.MessageManager;
import com.example.administrator.manager.NoticeManager;
import com.example.administrator.manager.XmppConnectionManager;
import com.example.administrator.model.ChartHisBean;
import com.example.administrator.model.Notice;
import com.example.administrator.model.User;
import com.example.administrator.task.ThreadPoolManager;
import com.example.administrator.util.StringUtil;
import com.example.administrator.view.LayoutChangeListener;
import com.example.administrator.view.ScrollLayout;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/20.
 */
public class ContacterMainActivity extends AContacterActivity implements LayoutChangeListener,View.OnClickListener{

    private final static String TAG = "ContacterMainActivity";
    private LayoutInflater inflater;
    private ScrollLayout layout;
    private ImageView imageView;
    private ImageView tab1;
    private ImageView tab2;
    private ImageView tab3;
    private ExpandableListView contacterList=null;
    private ContacterExpandAdapter expandAdapter=null;
    private ListView inviteList=null;
    private RecentChartAdapter noticeAdapter=null;
    private List<ChartHisBean>inviteNotices=new ArrayList<ChartHisBean>();
    private ImageView headIcon;
    private TextView noticePaopao;
    private List<String>groupNames;
    private List<String>newNames=new ArrayList<String>();
    private List<ContacterManager.MRosterGroup>rGroups;
    private ImageView iv_status;
    private User clivkUser;
    private ThreadPoolManager poolManager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacter_main);
        initContacter();
    }

      private void initContacter(){
          poolManager=ThreadPoolManager.getInstance();
          try {
              groupNames=ContacterManager.getGroupName(XmppConnectionManager.getInstance().getConnection().getRoster());
        rGroups=ContacterManager.getGroups(XmppConnectionManager.getInstance().getConnection().getRoster());
          } catch (Exception e) {

              groupNames=new ArrayList<String>();
              rGroups=new ArrayList<ContacterManager.MRosterGroup>();
              e.printStackTrace();
          }
          iv_status=(ImageView)findViewById(R.id.imageView1);
          getIMApplication().addActivity(this);
          inflater=LayoutInflater.from(context);
          layout=(ScrollLayout)findViewById(R.id.scrolllayout);
          layout.addChangeListener(this);
          tab1 = (ImageView) findViewById(R.id.tab1);
          tab2 = (ImageView) findViewById(R.id.tab2);
          tab3 = (ImageView) findViewById(R.id.tab3);
          noticePaopao=(TextView)findViewById(R.id.notice_paopao);

          imageView=(ImageView)findViewById(R.id.top_bar_select);

          View contacterTab1=inflater.inflate(R.layout.contacter_tab1,null);
          View contacterTab2=inflater.inflate(R.layout.contacter_tab2,null);
          View contacterTab3=inflater.inflate(R.layout.contacter_tab3,null);
          layout.addView(contacterTab1);
          layout.addView(contacterTab2);
          layout.addView(contacterTab3);
          layout.setToScreen(1);

          contacterList=(ExpandableListView)findViewById(R.id.main_expand_list);
          ImageView titleBack=(ImageView)findViewById(R.id.title_back);
          headIcon=(ImageView)findViewById(R.id.head_icon);
          headIcon.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 final Intent intent=new Intent(context,UserInfoActivity.class);
                  Runnable runnable=new Runnable() {
                      @Override
                      public void run() {
                          startActivity(intent);
                      }
                  };
                 poolManager.addTask(runnable);
              }
          });
          titleBack.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  finish();
              }
          });
          //联系人
          if (rGroups!=null) {
              expandAdapter = new ContacterExpandAdapter(context, rGroups);
              contacterList.setAdapter(expandAdapter);
          }
          contacterList.setOnCreateContextMenuListener(onCreateContextMenuListener);
          contacterList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
              @Override
              public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                  createChat((User)v.findViewById(R.id.userName).getTag());
                  return false;
              }
          });

          //未读信息
          inviteList=(ListView)findViewById(R.id.main_invite_list);
          inviteNotices=MessageManager.getInstance(context).getRecentContactsWithLastMsg();
          if(inviteNotices!=null) {
              noticeAdapter = new RecentChartAdapter(context, inviteNotices);
              inviteList.setAdapter(noticeAdapter);
              noticeAdapter.setOnClickListener(contacterOnClick);
          }
      }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        if(getUserOnlineState()){
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_online));
        }else {
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_offline));
        }
    }

  private void refreshList(){
      rGroups=ContacterManager.getGroups(XmppConnectionManager.getInstance().getConnection().getRoster());
      for(String newGroupName:newNames){
          ContacterManager.MRosterGroup mg=new ContacterManager.MRosterGroup(newGroupName,new ArrayList<User>());
          rGroups.add(rGroups.size()-1,mg);
      }
      expandAdapter.setContacter(rGroups);
      expandAdapter.notifyDataSetChanged();

      //刷新notice消息
      inviteNotices=MessageManager.getInstance(context).getRecentContactsWithLastMsg();
      if(inviteNotices!=null) {
          noticeAdapter.getNoticeList(inviteNotices);
          noticeAdapter.notifyDataSetChanged();
      }
      /**
       * 有新消息进来的气泡
       */
      setPaoPao();
  }

    /**
     * 上面滚动条上的气泡设置，有新消息来的通知气泡，数量设置
     */
    private void setPaoPao(){
        if(null!= inviteNotices &&inviteNotices.size()>0){
            int paoCount=0;
            for(ChartHisBean c:inviteNotices){
                Integer count=c.getNoticeSum();
                paoCount+=(count==null ? 0:count);
            }
            if(paoCount==0){
                noticePaopao.setVisibility(View.GONE);
                return;
            }
            noticePaopao.setText(paoCount+"");
            noticePaopao.setVisibility(View.VISIBLE);
        }else {
            noticePaopao.setVisibility(View.GONE);
        }
    }
    private View.OnClickListener contacterOnClick=new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           createChat((User)v.findViewById(R.id.new_content).getTag());
       }
   };

    View.OnCreateContextMenuListener onCreateContextMenuListener=new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            //类型，0代表是group类型  的，1代表的是child类型
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);

            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                int gId = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                String[] longClickItem = null;
                final String groupName = rGroups.get(gId).getName();
                if (StringUtil.notEmpty(groupName) && !Constant.ALL_FRIEND.equals(groupName) && !Constant.NO_GROUP_FRIEND.equals(groupName)) {
                    longClickItem = new String[]{"添加分组", "更改组名",};
                } else {
                    longClickItem = new String[]{"添加分组"};
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(longClickItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:  //添加分组
                                addNewGroup();
                                break;
                            case 1: //更改组名
                                updateGroupNameA(groupName);
                                break;
                        }
                    }
                }).setTitle("选项");
                builder.create().show();
            }else if (type==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                String[] longClickItems=null;
                View view=info.targetView;
                clivkUser=(User)view.findViewById(R.id.userName).getTag();
                showToast(clivkUser.getJID() + "---");

                if(StringUtil.notEmpty(clivkUser.getGroupNmae()) &&!Constant.ALL_FRIEND.equals(clivkUser.getGroupNmae()) && !Constant.NO_GROUP_FRIEND.equals(clivkUser.getGroupNmae())){
                    longClickItems=new String[]{"设置昵称","添加好友", "删除好友","移动到分组","退出该组"};
                }else {
                    longClickItems=new String[]{"设置昵称","添加好友", "删除好友","移动到分组"};
                }
                new AlertDialog.Builder(context).setItems(longClickItems,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:      //设置昵称
                                setNickName(clivkUser);
                                break;
                            case 1:       //添加好友
                                addSubscriber();
                                break;
                            case 2:     //删除好友
                                Runnable delFriend=new Runnable() {
                                    @Override
                                    public void run() {
                                        showDeleteDialog(clivkUser);
                                    }
                                };
                               poolManager.addTask(delFriend);
                                break;
                            case 3:  //移动到分组（1，先移出本组，2，移入某组)
                                /**
                                 * ui移除old组
                                 */
                                 Runnable addGroup=new Runnable() {
                                     @Override
                                     public void run() {

                                         removeUserFromGroupUI(clivkUser);

                                         removeUserFromGroup(clivkUser,clivkUser.getGroupNmae());

                                         addToGroup(clivkUser);
                                     }
                                 };
                                 poolManager.addTask(addGroup);
                                break;
                            case 4:  //移出组
                                /**
                                 * ui移除old组
                                 */
                                removeUserFromGroupUI(clivkUser);
                                /**
                                 * api级出某组
                                 */
                                removeUserFromGroup(clivkUser,clivkUser.getGroupNmae());
                                break;

                        }
                    }
                }).setTitle("选项").show();
            }
        }

        /**
         * 新建组
         */
        public void addNewGroup() {
            final EditText name_input = new EditText(context);
            name_input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            name_input.setHint("输入组名");
            new AlertDialog.Builder(context).setTitle("加入组").setView(name_input)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String groupName = name_input.getText().toString();
                            if (StringUtil.empty(groupName)) {
                                showToast("组名不能为空");
                                return;
                            }
                            // ui上增加数据
                            if (groupNames.contains(groupName)) {
                                showToast("组名已经存在");
                                return;
                            }
                           addGroupNameUi(groupName);
                           ContacterManager.addGroup(groupName,XmppConnectionManager.getInstance().getConnection());
                        }
                    }).setNegativeButton("取消", null).show();
        }
    };

    /**
     * 修改组名
     * @param
     */
    private void updateGroupNameA(final String groupName){
         final EditText name_input=new EditText(context);
        name_input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        name_input.setHint("输入组名");
        new AlertDialog.Builder(context).setTitle("修改组名").setView(name_input)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gNewName=name_input.getText().toString();
                        if(newNames.contains(gNewName) ||groupNames.contains(gNewName)){
                            showToast("组名已存在");
                            return ;
                        }
                        // UI级修改操作
                        updateGroupNameUI(groupName,gNewName);
                        // UIAPI
                        updateGroupName(groupName,gNewName);
                    }
                }).setNegativeButton("取消",null).show();
    }

    /**
     * ui级添加分组
     * @param newGroupName
     */
   private void addGroupNameUi(String newGroupName){
       groupNames.add(newGroupName);
       newNames.add(newGroupName);
       ContacterManager.MRosterGroup mg=new ContacterManager.MRosterGroup(newGroupName,new ArrayList<User>());
       rGroups.add(rGroups.size()-1,mg);
       //刷新用户信息
       expandAdapter.setContacter(rGroups);
       expandAdapter.notifyDataSetChanged();
   }
    /**
     * UI修改组名
     * @param oid
     * @param newGroupName
     */
 private void updateGroupNameUI(String oid,String newGroupName){
     if(StringUtil.empty(oid) ||Constant.ALL_FRIEND.equals(oid) ||Constant.NO_GROUP_FRIEND.equals(oid)){
         return ;
     }
     if(StringUtil.empty(newGroupName)||Constant.ALL_FRIEND.equals(newGroupName)||Constant.NO_GROUP_FRIEND.equals(newGroupName)){
         return ;
     }
     //要添加的组名只是添加到UI级的而已，并没有添加到服务器
     if(newNames.contains(oid)){
         newNames.remove(oid);
         newNames.add(newGroupName);
         return ;
     }
     //列表操作
     for(ContacterManager.MRosterGroup g:rGroups){
         if(g.getName().equals(oid)){
             g.setName(newGroupName);
         }
     }

     expandAdapter.notifyDataSetChanged();
 }

    /**
     * 修改昵称
     * @param user
     */
  private void setNickName(final User user){
      final EditText name_input=new EditText(context);
      name_input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      name_input.setHint("请输入昵称");
      new AlertDialog.Builder(context).setTitle("修改昵称").setView(name_input)
              .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      String name=name_input.getText().toString();
                      if(!"".equals(name))
                          setNickName(user,name);
                  }
              }).setNegativeButton("取消",null).show();
  }

    /**
     * 添加好友
     */
   private void addSubscriber(){
       final EditText name_input=new EditText(context);
       name_input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
       name_input.setHint("输入用户名");
       final EditText nickName=new EditText(context);
       nickName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
       nickName.setHint("输入昵称");
       LinearLayout linearLayout=new LinearLayout(context);
       linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
       linearLayout.setOrientation(LinearLayout.VERTICAL);
       linearLayout.addView(nickName);
       linearLayout.addView(name_input);
       new AlertDialog.Builder(context).setTitle("添加好友").setView(linearLayout)
               .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String userNmae=name_input.getText().toString();
                       String nickname=nickName.getText().toString();
                       if(StringUtil.empty(userNmae)){
                           showToast("用户名不能为空");
                           return ;
                       }
                       userNmae=StringUtil.doEmpty(userNmae);
                       if(StringUtil.empty(nickname)){
                           nickname=null;
                       }

                       if(isExitJid(StringUtil.getJidByName(userNmae),rGroups)){
                            showToast("好友已存在");
                           return ;
                       }
                       try {
                           createSubscriber(StringUtil.getJidByName(userNmae),nickname,null);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               }).setNegativeButton("取消",null).show();
   }

    /**
     * 删除用户
     * @param user
     */
    private void showDeleteDialog(final User user){
         AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.delete_user_confirm))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ui删除
                        deleteUserUI(clivkUser);
                        //api删除
                        try {
                            removeSubscriber(clivkUser.getJID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 删除数据库
                        NoticeManager.getInstance(context).delNoticeHisWithSb(clivkUser.getJID());
                        MessageManager.getInstance(context).deleteChartHisWithSb(clivkUser.getJID());

                    }
                }).setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
  }

    /**
     * UI级删除用户
     * @param user
     */
    private void deleteUserUI(User user){
        for(ContacterManager.MRosterGroup g:rGroups) {
            List<User> us = g.getUsers();
            if (us != null && us.size() > 0) {
                if (us.contains(user)) {
                    us.remove(user);
                    g.setUsers(us);
                }
            }
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();
    }
    /**
     * UI级移动用户，把用户移除某组
     */

    private void removeUserFromGroupUI(User user) {

        for (ContacterManager.MRosterGroup g : rGroups) {
            if (g.getUsers().contains(user)) {
                if (StringUtil.notEmpty(g.getName())
                        && !Constant.ALL_FRIEND.equals(g.getName())) {
                    List<User> users = g.getUsers();
                    users.remove(user);
                    g.setUsers(users);
                }
            }
        }
        expandAdapter.setContacter(rGroups);
        expandAdapter.notifyDataSetChanged();
    }

    /**
     * 添加到分组
     * @param user
     */
    private void addToGroup(final User user){
       LayoutInflater inflater1=LayoutInflater.from(context);
        View dialogView=inflater1.inflate(R.layout.yd_group_dialog,null);
        final Spinner spinner=(Spinner)dialogView.findViewById(R.id.spinner_list);
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,groupNames);
        spinner.setAdapter(adapter);

        new AlertDialog.Builder(context).setTitle("移动"+"至分组")
                .setView(dialogView).setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName=(spinner.getSelectedItem().toString());
                if(StringUtil.notEmpty(groupName)){
                    groupName=StringUtil.doEmpty(groupName);
                    if(newNames.contains(groupName)){
                        newNames.remove(groupName);
                    }
                    // UI级把用户移到某组
                    addUserGroupUI(user,groupName);

                    //api移入组
                    addUserToOtherGroup(user,groupName);
                }
            }
        }).setNegativeButton("取消",null).show();
    }

    /**
     * UI级移动，把用户加入某组
     * @param user
     * @param groupName
     */
   private void addUserGroupUI(User user,String groupName){
       for(ContacterManager.MRosterGroup g:rGroups){
           if(groupName.equals(g.getName())){
               List<User>users=g.getUsers();
               users.add(user);
               g.setUsers(users);
           }
       }
       expandAdapter.setContacter(rGroups);
       expandAdapter.notifyDataSetChanged();
   }
    @Override
    protected void addUserReceive(User user) {
      refreshList();
    }

    /**
     * 删除用户
     * @param user
     */
    @Override
    protected void deleteUserReceive(User user) {
              if(user==null){
                  return;
              }
        Toast.makeText(context,user.getName()==null ? user.getJID():user.getName()+"被删除了",Toast.LENGTH_SHORT).show();
        refreshList();
    }

    /**
     * 改变用户状态
     * @param user
     */
    @Override
    protected void changePresenceReceive(User user) {
        if(user==null){
            return ;
        }
        if(ContacterManager.contacters.get(user.getJID())==null)
            return;
        //下线
        if(!user.isAvailable()){
            if(ContacterManager.contacters.get(user.getJID()).isAvailable()){
                Toast.makeText(
                        context,
                        (user.getName() == null) ? user.getJID() : user
                                .getName() + "上线了", Toast.LENGTH_SHORT).show();
            }
        }
        //上线
        if(user.isAvailable()){
            if (!ContacterManager.contacters.get(user.getJID()).isAvailable())
                Toast.makeText(
                        context,
                        (user.getName() == null) ? user.getJID() : user
                                .getName() + "下线了", Toast.LENGTH_SHORT).show();
        }
        refreshList();
    }

    @Override
    protected void updateUserReceive(User user) {
         refreshList();
    }
    /**
     * 收到一个好友添加请求
     * @param subFrom
     */
    @Override
    protected void subscripUserReceive(String subFrom) {
        Notice notice=new Notice();
        notice.setFrom(subFrom);
        notice.setNoticeType(Notice.CHAT_MSG);
    }

    /**
     * 有新消息进来
     * @param notice
     */
    @Override
    protected void msgReceive(Notice notice) {
       for(ChartHisBean chartHisBean:inviteNotices){
           if (chartHisBean.getFrom().equals(notice.getFrom())){
               chartHisBean.setContent(notice.getContent());
               chartHisBean.setNoticeTime(notice.getNoticTime());
               chartHisBean.setNoticeType(notice.getNoticeType());
               Integer x=chartHisBean.getNoticeSum()==null ? 0:chartHisBean.getNoticeSum();
               chartHisBean.setNoticeSum(x+1);
           }
           noticeAdapter.getNoticeList(inviteNotices);
           noticeAdapter.notifyDataSetChanged();
           setPaoPao();
       }
    }

    @Override
    protected void handReConnect(boolean isSuccess) {
           //成功连接
        if(Constant.RECONNECT_STATE_SUCCESS==isSuccess){
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_online));
        }else if (Constant.RECONNECT_STATE_FAIL==isSuccess){
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_offline));
        }
    }

    @Override
    public void doChange(int lastIndex, int currentIndex) {

        if (lastIndex != currentIndex) {
            TranslateAnimation animation = null;
            LinearLayout layout = null;
            switch (currentIndex) {
                case 0:
                    if (lastIndex == 1) {
                        layout = (LinearLayout) tab1.getParent();
                        animation = new TranslateAnimation(0, -layout.getWidth(),
                                0, 0);
                    } else if (lastIndex == 2) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(layout.getLeft(),
                                -((LinearLayout) tab1.getParent()).getWidth(), 0, 0);
                    }
                    break;
                case 1:
                    if (lastIndex < 1) {
                        // 左到中
                        layout = (LinearLayout) tab1.getParent();
                        animation = new TranslateAnimation(-layout.getWidth(), 0,
                                0, 0);
                    } else if (lastIndex > 1) {
                        // 右到中
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(layout.getLeft(), 0, 0,
                                0);
                    }
                    break;
                case 2:
                    if (lastIndex == 1) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(0, layout.getLeft(), 0,
                                0);
                    } else if (lastIndex == 0) {
                        layout = (LinearLayout) tab2.getParent();
                        animation = new TranslateAnimation(
                                -((LinearLayout) tab1.getParent()).getWidth(),
                                layout.getLeft(), 0, 0);
                    }
                    break;
            }
            animation.setDuration(300);
            animation.setFillAfter(true);
            imageView.startAnimation(animation);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==tab1){
            layout.snapToScreen(0);
        }else if(v==tab2){
            layout.snapToScreen(1);
        }else if (v==tab3){
            layout.snapToScreen(2);
        }
    }
    private AdapterView.OnItemClickListener inviteListClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Notice notice=(Notice)view.findViewById(R.id.new_content).getTag();
            if(notice.getNoticeType()==Notice.CHAT_MSG){
                User user=new User();
                user.setJID("admin@zkost.com");
                createChat(user);
            }else{
                final String subFrom=notice.getFrom();
                new AlertDialog.Builder(context).setMessage(subFrom+"请求添加您为好友")
                        .setTitle("title").setPositiveButton("添加",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //接收请求
                        sendSubscribe(Presence.Type.subscribe,subFrom);
                        sendSubscribe(Presence.Type.subscribe,
                                subFrom);
                        refreshList();
                    }
                }).setNegativeButton("拒绝",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSubscribe(Presence.Type.unsubscribe,subFrom);
                    }
                }).show();
            }
        }
    };

    /**
     * 修改状态
     */
   private void modifyState(){
       String[] states=new String[]{"在线", "隐身", "吃饭", "睡觉" ,"忙碌"};
       new AlertDialog.Builder(this).setItems(states,new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Presence presence=new Presence(Presence.Type.available);
               switch (which){
                   case 0:
                       break;
                   case 1:
                       presence.setType(Presence.Type.unavailable);
                       break;
                   case 2:
                       presence.setStatus("吃饭");
                       break;
                   case 3:
                       presence.setStatus("睡觉");
                       break;
                   case 4:
                       presence.setStatus("忙碌");
               }
               XmppConnectionManager.getInstance().getConnection().sendPacket(presence);
           }
       }).setNegativeButton("取消",null).setTitle("修改状态").show();
   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater1=getMenuInflater();
        inflater1.inflate(R.menu.contacter_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent();
        switch (item.getItemId()){
            case R.id.menu_add_subscriber:
                addSubscriber();
                break;
            case R.id.menu_modify_state:
                modifyState();
                break;
            case R.id.menu_relogin:
                intent.setClass(context,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
