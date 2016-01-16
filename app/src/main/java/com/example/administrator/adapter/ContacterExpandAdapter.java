package com.example.administrator.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.activity.R;
import com.example.administrator.manager.ContacterManager;
import com.example.administrator.model.User;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class ContacterExpandAdapter extends BaseExpandableListAdapter {
    private List<ContacterManager.MRosterGroup>groups=null;
    private LayoutInflater inflater;
    private Context context;

    public ContacterExpandAdapter(Context context,List<ContacterManager.MRosterGroup>groups){
        this.context=context;
        this.groups=groups;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setContacter(List<ContacterManager.MRosterGroup>groups){
        this.groups=groups;
    }
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getUsers().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getUsers().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder groupHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.chat_group,null);
            groupHolder=new GroupHolder();
            groupHolder.groupName=(TextView)convertView.findViewById(R.id.groupName);
            groupHolder.onlineno=(TextView)convertView.findViewById(R.id.online);
            convertView.setTag(groupHolder);
        }else{
            groupHolder=(GroupHolder)convertView.getTag();
        }
        groupHolder.groupName.setText(groups.get(groupPosition).getName());
        StringBuilder builder=new StringBuilder();
        builder.append("[");
        builder.append(groups.get(groupPosition).getCount());
        builder.append("]");
        groupHolder.onlineno.setText(builder.toString());
        groupHolder.groupName.setTag(groups.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        User user=groups.get(groupPosition).getUsers().get(childPosition);
        ChilderHolder childerHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.chat_item,null);
            childerHolder=new ChilderHolder();
            childerHolder.image=(ImageView)convertView.findViewById(R.id.child_item_head);
            childerHolder.mood=(TextView)convertView.findViewById(R.id.mood);
            childerHolder.userNmae=(TextView)convertView.findViewById(R.id.userName);
            convertView.setTag(childerHolder);
        }else {
            childerHolder=(ChilderHolder)convertView.getTag();
        }
        user.setGroupNmae(groups.get(groupPosition).getName());
        //通过设置tag从而在主界面知道是哪个user
        childerHolder.userNmae.setTag(user);
        childerHolder.mood.setTag(groupPosition);
        childerHolder.image.setTag(childPosition);

        childerHolder.mood.setText(user.getStatus()==null ? "":user.getStatus());
        childerHolder.userNmae.setText(user.getName()+"---"+(user.isAvailable() ? "在线":"离线"));
        if(user.isAvailable()){
            childerHolder.userNmae.setTextColor(Color.BLACK);
            childerHolder.mood.setTextColor(Color.BLACK);
        }else {
            childerHolder.mood.setTextColor(Color.GRAY);
            childerHolder.userNmae.setTextColor(Color.GRAY);
        }
        return convertView;
    }

    /**
     * 设置哪个二级目录被默认选中
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class GroupHolder{
        TextView onlineno;
        TextView groupName;
    }

    public class ChilderHolder{
        TextView mood;
        TextView userNmae;
        ImageView image;
    }

}
