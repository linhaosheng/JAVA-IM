package com.example.administrator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.model.IMMessage;
import com.example.administrator.model.User;
import com.example.administrator.util.StringUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/10/6.
 */
public class MsgHisListAdapter extends BaseAdapter {
    private List<IMMessage>items;
    private Context context;
    private LayoutInflater inflater;
    private User user;   //聊天对象
    private User me;  //聊天人自己
    private String to;
    public MsgHisListAdapter(Context context,List<IMMessage>items,User user,String to,User me){
        this.items=items;
        this.context=context;
        this.user=user;
        this.me=me;
        this.to=to;
    }

    public void refreshList(List<IMMessage>items){
        Collections.sort(items);
        this.items=items;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return items==null ? 0 :items.size();
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
        inflater=LayoutInflater.from(context);
        IMMessage message=items.get(position);
        ViewHolder holder=null;
        if(convertView==null){
            convertView=this.inflater.inflate(R.layout.chathistoryitem,null);
            holder=new ViewHolder();
            holder.name = (TextView) convertView
                    .findViewById(R.id.tvHistoryName);
            holder.time = (TextView) convertView
                    .findViewById(R.id.tvHistoryTime);
            holder.content = (TextView) convertView
                    .findViewById(R.id.tvMsgItem);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        if (message.getMsgType() == 0) {
            if (user == null) {
                holder.name.setText(StringUtil.getUserNameByJid(to));
            } else {
                holder.name.setText(user.getName());
            }

        } else {
            holder.name.setText("我");
        }
        holder.time.setText(message.getTime().substring(0,19));
        holder.content.setText(message.getContent());

        return convertView;
    }
    class ViewHolder {
        TextView name;
        TextView time;
        TextView content;
    }
}
