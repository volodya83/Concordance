package com.peter.vladimir.concordance;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = IndexActivity.class.toString();
    private static final int COL_WORD = 0;
    private static final int COL_TEXT_NAME = 1;
    private static final int COL_LINE = 2;
    private static final int COL_POSITION = 3;

    private EditText et_index_file_name;
    private ImageButton ibtn_save_file;
    private TextView tv_index_grp_name;
    private ImageButton ibtn_index_grp_search;
    private ListView lv_texts_in_words;
    private TextView tv_index_title;
    private TextView tv_index_file;
    private Cursor cursor_texts;
    private MyCursorAdapter listAdapter;
    private String groupName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        et_index_file_name = (EditText)findViewById(R.id.et_index_file_name);
        tv_index_file = (TextView)findViewById(R.id.tv_index_file);
        tv_index_title = (TextView)findViewById(R.id.tv_index_title);
        tv_index_grp_name = (TextView)findViewById(R.id.tv_index_grp_name);
        ibtn_index_grp_search = (ImageButton)findViewById(R.id.ibtn_index_grp_search);
        ibtn_index_grp_search.setOnClickListener(this);
        ibtn_save_file = (ImageButton)findViewById(R.id.ibtn_save_file);
        lv_texts_in_words = (ListView)findViewById(R.id.lv_texts_in_words);
        refreshTextList();
        if (getIntent().hasExtra("groupName")) {
            groupName = getIntent().getExtras().getString("groupName");
            tv_index_grp_name.setText(groupName);
            tv_index_title.setText(groupName+" index");
            et_index_file_name.setText(groupName + "-" + DateFormat.getDateInstance().format(new Date()));
        }

    }

    @Override
    public void onClick(View v) {
        if (v==ibtn_index_grp_search){
            int[] text_id_arr = listAdapter.get_text_id_arr();
            Integer text_size = text_id_arr[text_id_arr.length - 1];
            if (text_size != 0) {
                String groupName = tv_index_grp_name.getText().toString();
                tv_index_file.setText(txtBuilder(groupName, text_id_arr));
            } else {
                Log.d(LOG_TAG, "onClick grpSearch");
                String groupName = tv_index_grp_name.getText().toString();
                tv_index_file.setText(txtBuilder(groupName, null));
            }
        }
    }

    private StringBuffer txtBuilder(String groupName, int[] text_id_arr){
        StringBuffer str = new StringBuffer("");
        Cursor cursor;
        if (text_id_arr == null) {
            cursor = SQLfunctions.groupDataInAllText(groupName);
        }else{
            int[] textIdArr = Arrays.copyOf(text_id_arr, text_id_arr.length);
            textIdArr[text_id_arr.length-1]=0;
            String arg = SQLfunctions.listOfTexts(textIdArr);
            cursor = SQLfunctions.groupDataInText(groupName, arg);
        }
        //Toast.makeText(this, ""+cursor.getCount(), Toast.LENGTH_SHORT).show();
        if (cursor!=null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                str.append(cursor.getString(COL_WORD)+": ");
                if (!cursor.isNull(1)) {
                    str.append("in text: " + cursor.getString(COL_TEXT_NAME))
                            .append(", line: " + cursor.getInt(COL_LINE)).append(", position: " + cursor.getInt(COL_POSITION)).append('\n');
                }else str.append("not found \n");
                cursor.moveToNext();
            }
        }else Toast.makeText(this, "Cursor is null", Toast.LENGTH_SHORT).show();
        return str;
    }

    public void refreshTextList() {
        cursor_texts = SQLfunctions.searchAllTexts();
        listAdapter = new MyCursorAdapter(getApplicationContext(), cursor_texts, MyCursorAdapter.ID_EX_LIST_TEXT_ITEM);
        lv_texts_in_words.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
