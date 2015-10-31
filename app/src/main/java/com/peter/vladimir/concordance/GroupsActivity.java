package com.peter.vladimir.concordance;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class GroupsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_grp_str;
    private EditText et_grp_name;
    private TextView tv_grp_name_title;
    private TextView tv_grp_1lst_title;
    private TextView tv_grp_2lst_title;
    private RadioButton rbtn_grp_group;
    private RadioButton rbtn_grp_relation;
    private RadioButton rbtn_grp_phrase;
    private ImageButton ibtn_grp_plus;
    private ListView lv_grp_1lst;
    private ListView lv_grp_2lst;
    private View selectedRbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        et_grp_str = (EditText)findViewById(R.id.et_grp_str);
        et_grp_name = (EditText)findViewById(R.id.et_grp_name);
        tv_grp_name_title = (TextView)findViewById(R.id.tv_grp_name_title);
        tv_grp_1lst_title = (TextView)findViewById(R.id.tv_grp_1lst_title);
        tv_grp_2lst_title = (TextView)findViewById(R.id.tv_grp_2lst_title);
        rbtn_grp_group = (RadioButton)findViewById(R.id.rbtn_grp_group);
        rbtn_grp_group.setOnClickListener(this);
        rbtn_grp_group.setChecked(true);
        selectedRbtn=rbtn_grp_group;
        rbtn_grp_relation = (RadioButton)findViewById(R.id.rbtn_grp_relation);
        rbtn_grp_relation.setOnClickListener(this);
        rbtn_grp_phrase = (RadioButton)findViewById(R.id.rbtn_grp_phrase);
        rbtn_grp_phrase.setOnClickListener(this);
        ibtn_grp_plus = (ImageButton)findViewById(R.id.ibtn_grp_plus);
        ibtn_grp_plus.setOnClickListener(this);
        lv_grp_1lst = (ListView)findViewById(R.id.lv_grp_1lst);
        refreshFirstList();
        lv_grp_2lst = (ListView)findViewById(R.id.lv_grp_2lst);
    }
    @Override
    public void onClick(View v) {
        String grpStr = et_grp_str.getText().toString();
        String grpName = et_grp_name.getText().toString();
        if(v!=ibtn_grp_plus){
            selectedRbtn=v;
        }

        if (v==ibtn_grp_plus) {
            if (grpStr.length() > 0 && grpName.length() > 0) {
                if (selectedRbtn == rbtn_grp_group) {
                    SQLfunctions.insertWordToGroup(grpName, grpStr);
                    refreshFirstList();
                }
            }
        }
    }

    private void refreshFirstList() {
        if (selectedRbtn==rbtn_grp_group){
            Cursor cursor = SQLfunctions.getGroups();
            lv_grp_1lst.setAdapter(new MyGroupCursorAdapter(this, cursor, R.layout.list_item_group_name));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groups, menu);
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
