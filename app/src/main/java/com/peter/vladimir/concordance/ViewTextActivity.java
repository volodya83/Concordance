package com.peter.vladimir.concordance;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

public class ViewTextActivity extends AppCompatActivity {

    private TextView tv_text;
    private ArrayList<String> lines_arr = new ArrayList<String>();
    private ListView lv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text);
        setTitle(getIntent().getExtras().getString("text_name"));
        int id = getIntent().getExtras().getInt("text_id");
        int position = getIntent().getExtras().getInt("position");
        tv_text = (TextView) findViewById(R.id.tv_text);
        Cursor cursor = SQLfunctions.viewText(id);
        String text = "", word = "";
        cursor.moveToFirst();
        Toast.makeText(this, "SQL Query complete", Toast.LENGTH_SHORT).show();
        int cur_line = 1, line;
        int cursor_size=cursor.getCount();
        int emptyLines=0;
        for (int i = 0; i < cursor_size ; i++) {
            word = cursor.getString(0);

            switch (cursor.getInt(1)) {
                case SQLfunctions.ONE_CAPITAL: {
                    word = word.substring(0, 1).toUpperCase() + word.substring(1);
                    break;
                }
                case SQLfunctions.ALL_CAPITAL: {
                    word = word.toUpperCase();
                    break;
                }
                default:
                    break;
            }
            if ((line = cursor.getInt(2)) != cur_line || cursor.isLast()) {
                lines_arr.add(text);
                emptyLines=line-cur_line;
                text = "";
                for (int j=1; j<emptyLines; j++){
                    lines_arr.add(text);
                }
                cur_line=line;
            }
            cursor.moveToNext();
            if (!cursor.isAfterLast()) {
                if (cursor.getInt(1) != SQLfunctions.SYMBOL) {
                    word = word + " ";
                }
            }
            text = text + word;

        }
        lines_arr.set(lines_arr.size() - 1, lines_arr.get(lines_arr.size() - 1) + word);
        lv_text = (ListView) findViewById(R.id.lv_text);
        lv_text.setAdapter(new MyArrayAdapter(this, R.layout.list_item_text_view, lines_arr));
        if (position!=0) {
            lv_text.setSelection(position - 1);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_text, menu);
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
