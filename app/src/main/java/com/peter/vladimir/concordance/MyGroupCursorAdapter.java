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
    private static final int LIST_ITEM_GROUP_CONTENT = R.layout.list_item_group_content;
    private static final int COL_GROUP_NAME = 1;
    private static final int COL_GROUP_CONTENT = 1;
    private static final int COL_ID = 0;
    private int _resource;
    public Context _context;

    public MyGroupCursorAdapter(Context context, Cursor c, int resource) {
        super(context, c);
        _resource = resource;
        _context = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(_resource, parent, false);
        if (_resource == LIST_ITEM_GROUP_NAME) {
            ViewHolderList1 viewHolder = new ViewHolderList1(view);
            view.setTag(viewHolder);
        }else if (_resource==LIST_ITEM_GROUP_CONTENT){
            ViewHolderList2 viewHolder = new ViewHolderList2(view);
            view.setTag(viewHolder);
        }
        return view;
    }

        @Override
        public void bindView (View view, Context context, Cursor cursor) {
            if (_resource == LIST_ITEM_GROUP_NAME) {
                ViewHolderList1 viewHolder1 = (ViewHolderList1)view.getTag();
                String group_name = cursor.getString(COL_GROUP_NAME);
                viewHolder1.tv_grp_item_name.setText(group_name);
                viewHolder1.tv_grp_item_name.setOnClickListener(new GroupsActivity.MyClickListener(view, group_name, _context));
            }else if (_resource == LIST_ITEM_GROUP_CONTENT){
                ViewHolderList2 viewHolder2 = (ViewHolderList2)view.getTag();
                String group_content = cursor.getString(COL_GROUP_CONTENT);
                viewHolder2.tv_grp_item_content.setText(group_content);
                String idContent=String.valueOf(cursor.getInt(COL_ID));

                viewHolder2.ibtn_grp_content_item_delete.setOnClickListener(new GroupsActivity.MyClickListener(view, idContent, _context ));
            }
        }

    private class ViewHolderList1 {
        public TextView tv_grp_item_name;
        public ImageButton ibtn_grp_item_delete;

        public ViewHolderList1(View view) {
            tv_grp_item_name = (TextView)view.findViewById(R.id.tv_grp_item_name);
            ibtn_grp_item_delete = (ImageButton)view.findViewById(R.id.ibtn_grp_item_delete);
            ibtn_grp_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private class ViewHolderList2 {
        public TextView tv_grp_item_content;
        public ImageButton ibtn_grp_content_item_search;
        public ImageButton ibtn_grp_content_item_delete;
        public ViewHolderList2(View view) {
            tv_grp_item_content = (TextView)view.findViewById(R.id.tv_grp_item_content);
            ibtn_grp_content_item_search = (ImageButton)view.findViewById(R.id.ibtn_grp_content_item_search);
            ibtn_grp_content_item_delete = (ImageButton)view.findViewById(R.id.ibtn_grp_content_item_delete);
        }
    }
}