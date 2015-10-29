package com.peter.vladimir.concordance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Volodya on 30-Sep-15.
 */
public class MyArrayPhraseAdapter extends ArrayAdapter<PhraseData> {
    private static final int ID_WORD_DATA = R.layout.list_item_word_data;
    private ArrayList<PhraseData> _listPhraseData;
    static Context _context;
    static int _resource;

    public MyArrayPhraseAdapter(Context context, int resource, List<PhraseData> objects) {
        super(context, resource, objects);
        _listPhraseData = (ArrayList<PhraseData>) objects;
        _context = context;
        _resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(_resource, parent, false);
        ViewHolderWordData viewHolder = new ViewHolderWordData(rowView);
        HashMap<Integer, String> hashMap = SQLfunctions.getTextMap();
        PhraseData curPhrase = _listPhraseData.get(position);
        String text_name = hashMap.get(curPhrase.get_text_id());
        viewHolder.tv_in_text_name.setText(text_name);
        String lineStr;
        if (curPhrase.get_lineStart() != curPhrase.get_lineEnd())
            lineStr = curPhrase.get_lineStart() + "-" + curPhrase.get_lineEnd();
        else lineStr = curPhrase.get_lineStart() + "";
        viewHolder.tv_line.setText(lineStr);
        String positionStr = curPhrase.get_positionStart() + "-" + curPhrase.get_positionEnd();
        viewHolder.tv_position.setText(positionStr);
        viewHolder.textId=curPhrase.get_text_id();
        viewHolder.position=position;
        viewHolder.lineStart=curPhrase.get_lineStart();
        rowView.setTag(viewHolder);
        return rowView;
    }

    private class ViewHolderWordData {
        public TextView tv_in_text_name;
        public TextView tv_line;
        public TextView tv_position;
        public ImageButton ibtn_show_text_section;
        public TextView tv_context;
        public String str_context;
        public View lay_context;
        public int position;
        public int textId;
        public int lineStart;
        public ImageButton ibtn_viewText;

        public ViewHolderWordData(View view) {

            tv_in_text_name = (TextView) view.findViewById(R.id.tv_in_text_name);
            tv_line = (TextView) view.findViewById(R.id.tv_line);
            tv_position = (TextView) view.findViewById(R.id.tv_position);
            tv_context = (TextView) view.findViewById(R.id.tv_context);
            lay_context = (View) view.findViewById(R.id.lay_context);
            ibtn_show_text_section = (ImageButton) view.findViewById(R.id.ibtn_show_text_section);
            ibtn_viewText = (ImageButton)view.findViewById(R.id.ibtn_viewText);
            ibtn_show_text_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    str_context = SQLfunctions.getWordPhraseContext(lineStart, textId);
                    tv_context.setText(str_context);
                    lay_context.setVisibility(View.VISIBLE);
                }
            });
            ibtn_viewText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(_context, ViewTextActivity.class);
                    intent.putExtra("text_id", textId);
                    intent.putExtra("text_name", tv_in_text_name.getText().toString());
                    intent.putExtra("position", lineStart);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                    // Toast.makeText(_context,"text_id="+ _id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
