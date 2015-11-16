package com.peter.vladimir.concordance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.IntBuffer;

/**
 * Created by Volodya on 21-Sep-15.
 */
public class MyCursorAdapter extends CursorAdapter {
    public static final int ID_LIST_TEXT_ITEM = R.layout.list_item_text;
    public static final int ID_EX_LIST_TEXT_ITEM = R.layout.ex_list_view_item;
    public static final int ID_LIST_FOUND_WORD_ITEM = R.layout.list_item_found_word;
    public static final int ID_LIST_WORD_DATA_ITEM = R.layout.list_item_word_data;
    private static final String TAG = "MyCursorAdapter";

    private static final int COL_TEXT_ID = 0;
    private static final int COL_TEXT_NAME = 1;
    private static final int COL_AUTHOR_NAME = 2;
    private static final int COL_TEXT_DATE = 3;

    private static final int COL_WORD = 1;
    private static final int COL_TEXT_LINE = 2;
    private static final int COL_WORD_POSITION = 3;

    private static Context _context;
    private int _resource;
    private int[] _text_id_arr;
    private int _coursor_size;


    public MyCursorAdapter(Context context, Cursor c, int resource) {
        super(context, c, 0);
        _resource = resource;
        _context = context;
        _coursor_size = c.getCount();
        _text_id_arr = new int[_coursor_size + 1];
    }

    public int[] get_text_id_arr() {
        return _text_id_arr;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(_resource, parent, false);
        if (_resource == ID_LIST_TEXT_ITEM) {
            ViewHolderList viewHolder = new ViewHolderList(view);
            view.setTag(viewHolder);
        } else if (_resource == ID_EX_LIST_TEXT_ITEM) {
            ViewHolderExp viewHolder = new ViewHolderExp(view);
            view.setTag(viewHolder);
        } else if (_resource == ID_LIST_FOUND_WORD_ITEM) {
            ViewHolderWord viewHolder = new ViewHolderWord(view);
            view.setTag(viewHolder);
        } else {
            ViewHolderWordData viewHolder = new ViewHolderWordData(view);
            view.setTag(viewHolder);
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (_resource == ID_LIST_TEXT_ITEM) {
            ViewHolderList viewHolder = (ViewHolderList) view.getTag();
            String text_name = cursor.getString(COL_TEXT_NAME);
            viewHolder.tv_text_name.setText("Title: "+text_name);
            String text_date = cursor.getString(COL_TEXT_DATE);
            viewHolder.tv_text_date.setText("Publish : "+text_date);
            String text_author = cursor.getString(COL_AUTHOR_NAME).replaceAll(",", ", ");
            viewHolder.tv_author.setText("Author: "+text_author);
            viewHolder._id = cursor.getInt(COL_TEXT_ID);
        } else if (_resource == ID_EX_LIST_TEXT_ITEM) {
            ViewHolderExp viewHolder = (ViewHolderExp) view.getTag();
            String text_name = cursor.getString(COL_TEXT_NAME);
            viewHolder.tvcheck_texts.setText(text_name);
            viewHolder._id = cursor.getInt(COL_TEXT_ID);
            viewHolder._position = cursor.getPosition();
        } else if (_resource==ID_LIST_FOUND_WORD_ITEM){
            ViewHolderWord viewHolder = (ViewHolderWord) view.getTag();
            String word = cursor.getString(COL_WORD);
            //Log.d(TAG, "bindView->word="+word);
            viewHolder.tv_found_word.setText(word);
            viewHolder.tv_found_word.setOnClickListener(new WordActivity.MyClickListener(view, word));
        }else {
            ViewHolderWordData viewHolder = (ViewHolderWordData)view.getTag();
            String text_name = cursor.getString(COL_TEXT_NAME); //1
            viewHolder.tv_in_text_name.setText(text_name);
            int text_line = cursor.getInt(COL_TEXT_LINE); //2
            viewHolder.tv_line.setText(""+text_line);
            int word_position = cursor.getInt(COL_WORD_POSITION); //3
            viewHolder.tv_position.setText(""+word_position);
            viewHolder.lineStart = text_line;
            viewHolder.textId = cursor.getInt(COL_TEXT_ID);
        }
    }

    private static class ViewHolderList {
        public final TextView tv_text_name;
        public final TextView tv_author;
        public final TextView tv_text_date;
        public final ImageButton ibtn_view;
        public final ImageButton ibtn_delete;
        public int _id;

        public ViewHolderList(View view) {
            tv_text_name = (TextView) view.findViewById(R.id.tv_text_data);
            tv_author = (TextView) view.findViewById(R.id.tv_author);
            tv_text_date = (TextView) view.findViewById(R.id.tv_text_date);
            ibtn_view = (ImageButton) view.findViewById(R.id.ibtn_view);
            ibtn_view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(_context, ViewTextActivity.class);
                    intent.putExtra("text_id", _id);
                    intent.putExtra("text_name", tv_text_name.getText().toString());
                    intent.putExtra("position", 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                    // Toast.makeText(_context,"text_id="+ _id, Toast.LENGTH_SHORT).show();
                }
            });
            ibtn_delete = (ImageButton) view.findViewById(R.id.ibtn_delete);
            ibtn_delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SQLfunctions.deleteText(_id);
                    MainActivity.refreshTextList();
                }
            });
        }
    }

    private class ViewHolderExp {
        public CheckedTextView tvcheck_texts;
        public int _id;
        public int _position;

        public ViewHolderExp(View view) {
            tvcheck_texts = (CheckedTextView) view.findViewById(R.id.tvcheck_texts);
            tvcheck_texts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvcheck_texts.isChecked()) {
                        tvcheck_texts.setChecked(false);
                        _text_id_arr[_position] = 0;
                        _text_id_arr[_coursor_size]--;
                    } else {
                        tvcheck_texts.setChecked(true);
                        _text_id_arr[_position] = _id;
                        _text_id_arr[_coursor_size]++;
                    }
                }
            });
        }
    }

    private class ViewHolderWord {
        public final TextView tv_found_word;

        public ViewHolderWord(View view) {
            tv_found_word = (TextView) view.findViewById(R.id.tv_found_word);
        }
    }

    private class ViewHolderWordData {
        public TextView tv_in_text_name;
        public TextView tv_line;
        public TextView tv_position;
        public ImageButton ibtn_show_text_section;
        public String str_context;
        public TextView tv_context;
        public View lay_context;
        public int lineStart, textId;
        public ImageButton ibtn_viewText;



        public ViewHolderWordData(View view) {
            tv_in_text_name = (TextView) view.findViewById(R.id.tv_in_text_name);
            tv_line = (TextView) view.findViewById(R.id.tv_line);
            tv_position = (TextView) view.findViewById(R.id.tv_position);
            ibtn_show_text_section=(ImageButton)view.findViewById(R.id.ibtn_show_text_section);
            tv_context = (TextView)view.findViewById(R.id.tv_context);
            lay_context = view.findViewById(R.id.lay_context);
            ibtn_viewText=(ImageButton)view.findViewById(R.id.ibtn_viewText);
            ibtn_show_text_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    str_context = SQLfunctions.getWordPhraseContext(lineStart, textId);
                    tv_context.setText(str_context);
                    ViewGroup.LayoutParams params = lay_context.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    lay_context.setLayoutParams(params);
                    //lay_context.setVisibility(View.VISIBLE);
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
