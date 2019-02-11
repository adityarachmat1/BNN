package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnn.Modal.ExpandedSubmenu;
import com.bnn.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by USER on 10/27/2017.
 */

public class SubMenuPencegahan extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ExpandedSubmenu> mListDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<ExpandedSubmenu, List<ExpandedSubmenu>> mListDataChild;

    public SubMenuPencegahan (Context context, List<ExpandedSubmenu> listDataHeader, HashMap<ExpandedSubmenu, List<ExpandedSubmenu>> listChildData) {
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
        List<ExpandedSubmenu> child = this.mListDataChild.get(this.mListDataHeader.get(groupPosition));
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
        ExpandedSubmenu headerTitle = (ExpandedSubmenu) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_listkategoripencegahan, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.namakategory);
        ImageView imgArrowIcon = (ImageView) convertView.findViewById(R.id.imgArrowIcon2);

        lblListHeader.setText(headerTitle.getMenuName());

        if (getChildrenCount(groupPosition) == 0) {
            imgArrowIcon.setVisibility(View.INVISIBLE);
        } else {
            imgArrowIcon.setVisibility(View.VISIBLE);
            int imgIcon = (isExpanded) ? R.drawable.menu_down : R.drawable.menu_right_copy;
            imgArrowIcon.setImageResource(imgIcon);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ExpandedSubmenu childData = (ExpandedSubmenu) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu_pencegahan, null);
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
