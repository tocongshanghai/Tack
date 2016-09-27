package com.tocong.tack.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tocong.tack.R;
import com.tocong.tack.entity.FoodInfo;
import com.tocong.tack.entity.FoodType;

import java.util.List;

/**
 * Created by 陶聪
 * 创作时间: 2016-09-27.15:13
 * 该类的作用:
 * 公司：上海家乐宝真好电子商务公司
 */
public class FoodTypeAdapter extends BaseExpandableListAdapter {
    private List<FoodType> firstType;
    private List<List<FoodInfo>> secondType;
    private LayoutInflater mInflater;

    public FoodTypeAdapter(Context context, List<FoodType> firstType, List<List<FoodInfo>> secondType) {
        this.firstType = firstType;
        this.secondType = secondType;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return firstType == null ? 0 : firstType.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return secondType == null ? 0 : secondType.get(groupPosition) == null ? 0 : secondType.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return firstType.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return secondType.get(groupPosition) == null ? null : secondType.get(groupPosition).get(childPosition);
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_elv_group, parent, false);
        }
        ImageView group_pic = (ImageView) convertView.findViewById(R.id.group_pic);
        TextView group_name = (TextView) convertView.findViewById(R.id.group_name);
        TextView group_num = (TextView) convertView.findViewById(R.id.group_num);
        FoodType foodType = (FoodType) getGroup(groupPosition);

        group_pic.setBackgroundResource(R.mipmap.group_right);
        if (isExpanded) {
            group_pic.setBackgroundResource(R.mipmap.group_bottom);
        }
        int sortNum = 0;
        int sortAllNum = 0;
        if (foodType != null) {
            List<FoodInfo> foodInfoList = foodType.getFoodInfoList();
            if (foodInfoList.size() > 0) {
                sortAllNum = foodInfoList.size();
                for (FoodInfo f : foodInfoList) {
                    if (f.getNoIsPickCount() == 0) {
                        sortNum++;
                    }
                }
            }
            group_name.setText(foodType.getType_name() == null ? "" : foodType.getType_name());
            group_num.setText(sortNum + "/" + sortAllNum);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_elv_child, parent, false);
        }
        TextView food_name = (TextView) convertView.findViewById(R.id.food_name);
        TextView food_standard = (TextView) convertView.findViewById(R.id.food_standard);
        TextView sort_num = (TextView) convertView.findViewById(R.id.sort_num);
        TextView no_sort_num = (TextView) convertView.findViewById(R.id.no_sort_num);
        FoodInfo foodInfo = (FoodInfo) getChild(groupPosition, childPosition);
        if (foodInfo != null) {
            food_name.setText(foodInfo.getFood_name() == null ? "" : foodInfo.getFood_name());
            sort_num.setText("全部" + foodInfo.getPickCount() + "份");
            String s = "未投" + foodInfo.getNoIsPickCount() + "份";
            int color = Color.RED;
            if (foodInfo.getNoIsPickCount() == 0) {
                s = "已投完";
                color = Color.GREEN;
            }
            no_sort_num.setTextColor(color);
            no_sort_num.setText(s);
            food_standard.setText("规格：" + foodInfo.getSale_describe());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
