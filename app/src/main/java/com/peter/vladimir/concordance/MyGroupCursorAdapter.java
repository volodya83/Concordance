package com.peter.vladimir.concordance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Volodya on 31-Oct-15.
 */
public class MyGroupCursorAdapter extends CursorAdapter {
    private static final int LIST_ITEM_GROUP_NAME = R.layout.list_item_group_name;
    private static final int COL_GROUP_NAME = 1;
    int _resource;

    public MyGroupCursorAdapter(Context context, Cursor c, int resource) {
        super(context, c);
        _resource = resource;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(_resource, parent, false);
        if (_resource == LIST_ITEM_GROUP_NAME) {
            ViewHolderList viewHolder = new ViewHolderList(view);
            view.setTag(viewHolder);
        }
        return view;
    }

        @Override
        public void bindView (View view, Context context, Cursor cursor) {
            if (_resource == LIST_ITEM_GROUP_NAME) {
                ViewHolderList viewHolder = (ViewHolderList) view.getTag();
                String group_name = cursor.getString(COL_GROUP_NAME);
                viewHolder.tv_grp_item_name.setText(group_name);
            }
        }

    private class ViewHolderList {
        public TextView tv_grp_item_name;
        public ImageButton ibtn_grp_item_delete;

        public ViewHolderList(View view) {
            tv_grp_item_name = (TextView)view.findViewById(R.id.tv_grp_item_name);
            ibtn_grp_item_delete = (ImageButton)view.findViewById(R.id.ibtn_grp_item_delete);
            ibtn_grp_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}