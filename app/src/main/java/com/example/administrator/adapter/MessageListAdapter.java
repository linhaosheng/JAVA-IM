package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.User;
import com.example.administrator.util.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/9/24.
 */
public class MessageListAdapter extends BaseAdapter {
    private List<IMMessage>items;
    private Context context;
    private ListView adapterList;
    private LayoutInflater inflater;
    private User user;   //聊天人
    private String to;


    public MessageListAdapter(Context context,List<IMMessage>items,ListView adapterList,User user,String to){
        this.context=context;
        this.items=items;
        this.adapterList=adapterList;
        this.user=user;
        this.to=to;
    }
    //对消息进行精确的定位
    public void refreshList(List<IMMessage>items){
        this.items=items;
        this.notifyDataSetChanged();
        /**
         * 可以用户这种方法
         * Parcelable listState = listView.onSaveInstanceState();

         记住listState对象；

         再次进入页面的时候：

         listView.onRestoreInstanceState(listState);
         */
        adapterList.setSelection(items.size()-1);
    }
    @Override
    public int getCount() {
        return  items==null ? 0:items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
       inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        IMMessage message=items.get(position);
        if(message.getMsgType()==1){
            //convertView=this.inflater.inflate(R.layout.formclient_chat_in,null);
            convertView=this.inflater.inflate(R.layout.formclient_chat_out,null);
        }else{
          // convertView=this.inflater.inflate(R.layout.formclient_chat_out,null);
            convertView=this.inflater.inflate(R.layout.formclient_chat_in,null);
        }
        viewHolder=new ViewHolder();
        viewHolder.dateView=(TextView)convertView.findViewById(R.id.formclient_row_date);
        viewHolder.msgView=(TextView)convertView.findViewById(R.id.formclient_row_msg);
        viewHolder.userView=(TextView)convertView.findViewById(R.id.formclient_row_userid);
        if(message.getMsgType()==1){
            viewHolder.userView.setText("我");
       /*     if(null==user){
                viewHolder.userView.setText(StringUtil.getUserNameByJid(to));
            }else{
                viewHolder.userView.setText(user.getName());
            }
            */
        }else {
            System.out.println("user-------"+user);
            if(null==user){
                viewHolder.userView.setText(StringUtil.getUserNameByJid(to));
            }else{
                viewHolder.userView.setText(user.getName());
            }
        }
        viewHolder.dateView.setText(message.getTime());
        viewHolder.msgView.setText(message.getContent());
        return convertView;
    }

    public class ViewHolder{
        private TextView userView;
        private TextView dateView;
        private TextView msgView;
    }
}
