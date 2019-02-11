package com.bnn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bnn.R;
import com.bnn.Modal.ExpandedMenuModel;

import java.util.HashMap;
import java.util.List;


public class MenuExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ExpandedMenuModel> mListDataHeader; // header titles
    private List<Integer> menuList;

    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> mListDataChild;

    public MenuExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        List<ExpandedMenuModel> child = this.mListDataChild.get(this.mListDataHeader.get(groupPosition));
        if (child != null) {
            childCount = child.size();
        }
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
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
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listheader, null);
        }

        LinearLayout layoutHeader = (LinearLayout) convertView.findViewById(R.id.layoutHeader);
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        ImageView imgArrowIcon = (ImageView) convertView.findViewById(R.id.imgArrowIcon);
        View separatorViewTop = (View) convertView.findViewById(R.id.separatorView);
        View separatorViewBottom = (View) convertView.findViewById(R.id.separatorView1);

        lblListHeader.setText(headerTitle.getMenuName());
        headerIcon.setImageResource(headerTitle.getMenuIconImg());

        if (headerTitle.getMenuId() != 226) {
            separatorViewTop.setVisibility(View.GONE);
        } else {
            separatorViewTop.setVisibility(View.VISIBLE);
        }

        if (headerTitle.getMenuId() != 201) {
            separatorViewBottom.setVisibility(View.GONE);
        } else {
            separatorViewBottom.setVisibility(View.VISIBLE);
        }

        if (getChildrenCount(groupPosition) == 0) {
            imgArrowIcon.setVisibility(View.INVISIBLE);
        } else {
            imgArrowIcon.setVisibility(View.VISIBLE);
            int imgIcon = (isExpanded) ? R.drawable.arrow_down : R.drawable.arrow_up;
            imgArrowIcon.setImageResource(imgIcon);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ExpandedMenuModel childData = (ExpandedMenuModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.submenu);
        txtListChild.setText(childData.getMenuName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
