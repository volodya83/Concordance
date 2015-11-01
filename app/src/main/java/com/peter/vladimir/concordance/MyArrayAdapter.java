package com.peter.vladimir.concordance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodya on 23-Sep-15.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {
    ArrayList<String> _objects;
    static Context _context;
    static int _resource;

    public MyArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        _objects = (ArrayList<String>) objects;
        _context = context;
        _resource = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(_resource, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        viewHolder.tv_text.setText(_objects.get(position));
        rowView.setTag(viewHolder);
        return rowView;

    }

    private static class ViewHolder {
        public final TextView tv_text;

        public ViewHolder(View view) {
            tv_text = (TextView) view.findViewById(R.id.tv_text);
        }
    }


}
