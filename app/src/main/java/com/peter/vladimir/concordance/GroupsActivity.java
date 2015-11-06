package com.peter.vladimir.concordance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GroupsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = GroupsActivity.class.toString();
    private static final int GROUP = 1;
    private static final int PHRASE = 2;
    private static final int RELATION = 3;
    private EditText et_grp_str;
    public static EditText et_grp_name;
    public static TextView tv_grp_name_title;
    public static TextView tv_grp_1lst_title;
    public static TextView tv_grp_2lst_title;
    private static RadioButton rbtn_grp_group;
    private static RadioButton rbtn_grp_relation;
    private static RadioButton rbtn_grp_phrase;
    private ImageButton ibtn_grp_plus;
    private static ListView lv_grp_1lst;
    private static ListView lv_grp_2lst;
    protected static View selectedRbtn;
    private static Context _context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        _context = this;
        et_grp_str = (EditText) findViewById(R.id.et_grp_str);
        et_grp_name = (EditText) findViewById(R.id.et_grp_name);
        tv_grp_name_title = (TextView) findViewById(R.id.tv_grp_name_title);
        tv_grp_1lst_title = (TextView) findViewById(R.id.tv_grp_1lst_title);
        tv_grp_2lst_title = (TextView) findViewById(R.id.tv_grp_2lst_title);
        rbtn_grp_group = (RadioButton) findViewById(R.id.rbtn_grp_group);
        rbtn_grp_group.setOnClickListener(this);
        rbtn_grp_group.setChecked(true);
        selectedRbtn = rbtn_grp_group;
        rbtn_grp_relation = (RadioButton) findViewById(R.id.rbtn_grp_relation);
        rbtn_grp_relation.setOnClickListener(this);
        rbtn_grp_phrase = (RadioButton) findViewById(R.id.rbtn_grp_phrase);
        rbtn_grp_phrase.setOnClickListener(this);
        ibtn_grp_plus = (ImageButton) findViewById(R.id.ibtn_grp_plus);
        ibtn_grp_plus.setOnClickListener(this);
        lv_grp_1lst = (ListView) findViewById(R.id.lv_grp_1lst);
        refreshFirstList();
        lv_grp_2lst = (ListView) findViewById(R.id.lv_grp_2lst);
    }

    @Override
    public void onClick(View v) {

        String grpStr = et_grp_str.getText().toString();
        String grpName = et_grp_name.getText().toString();
        if (v != ibtn_grp_plus) {
            selectedRbtn = v;
        }
        if (v == rbtn_grp_phrase) {
            Log.d(LOG_TAG, "rbtn_grp_phrase clicked");
            changeLayout(PHRASE);
            refreshSecondList(0,"",0);
        } else if (v == rbtn_grp_group) {
            changeLayout(GROUP);
            refreshFirstList();
        } else if (v==rbtn_grp_relation){
            changeLayout(RELATION);
            refreshFirstList();
        }else Toast.makeText(this, "Rbtn error", Toast.LENGTH_SHORT).show();  //Error

        if (v == ibtn_grp_plus) {
            if (grpStr.length() > 0 && grpName.length() > 0) {
                if (selectedRbtn == rbtn_grp_group) {
                    SQLfunctions.insertWordToGroup(grpName, grpStr);
                    refreshFirstList();
                    Cursor cursor = SQLfunctions.getGroupContent(grpName);
                    lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(this, cursor, R.layout.list_item_group_content, grpName));
                } else if (selectedRbtn == rbtn_grp_phrase) {
                    SQLfunctions.insertPhrase(grpStr);
                    refreshSecondList(0,"",0);
                }else if (selectedRbtn==rbtn_grp_relation){
                    String[] words=grpStr.split("-");
                    if(words.length!=2){
                        Toast.makeText(this, "Pair is not correct", Toast.LENGTH_SHORT).show();
                    }else {
                        SQLfunctions.insertWordsToRelations(grpName, words);
                        refreshFirstList();
                        Cursor cursor = SQLfunctions.getRelationContent(grpName);
                        lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(this, cursor, R.layout.list_item_group_content, grpName));
                    }
                }
            }
        }
    }

    private void changeLayout(int status) {
        if (status == GROUP || status==RELATION) {
            ViewGroup.LayoutParams params = tv_grp_1lst_title.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tv_grp_1lst_title.setLayoutParams(params);
            //tv_grp_1lst_title.setText("Groups");

            params = tv_grp_name_title.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tv_grp_name_title.setLayoutParams(params);

            params = et_grp_name.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            et_grp_name.setLayoutParams(params);

           // tv_grp_2lst_title.setText("Words in group:");

            params = lv_grp_2lst.getLayoutParams();
            params.height = 0;
            lv_grp_2lst.setLayoutParams(params);
        } else if (status == PHRASE) {
            ViewGroup.LayoutParams params = tv_grp_1lst_title.getLayoutParams();
            params.height = 0;
            tv_grp_1lst_title.setLayoutParams(params);

            params = tv_grp_name_title.getLayoutParams();
            params.height = 0;
            tv_grp_name_title.setLayoutParams(params);

            params = et_grp_name.getLayoutParams();
            params.height = 0;
            et_grp_name.setLayoutParams(params);
            tv_grp_2lst_title.setText("Phrases");

            params = lv_grp_2lst.getLayoutParams();
            params.height = params.MATCH_PARENT;
            lv_grp_2lst.setLayoutParams(params);


        }
    }

    public static class MyClickListener implements View.OnClickListener {
        private String _group_name;
        public Context _context;
        private int _idContent;
        private String _strContent;

        public MyClickListener(View view, String group_name, Context context, int idContent) {
            _group_name = group_name;
            _context = context;
            _idContent = idContent;
        }

        public MyClickListener(View view, String group_name, Context context, int idContent, String strContent) {
            _group_name = group_name;
            _context = context;
            _idContent = idContent;
            _strContent = strContent;
        }

        @Override
        public void onClick(View v) {
            if (selectedRbtn == rbtn_grp_group) {
                if (v.getId() == R.id.tv_grp_item_name) {
                    et_grp_name.setText(_group_name);
                    refreshSecondList(R.id.tv_grp_item_name, _group_name, 0);
                } else if (v.getId() == R.id.ibtn_grp_content_item_delete) {
                    SQLfunctions.deleteContentInGroup(_idContent);// RowId
                    refreshFirstList();
                    tv_grp_2lst_title.setText("Words in group: " + _group_name);
                    Cursor cursor = SQLfunctions.getGroupContent(_group_name);
                    if (cursor.getCount() == 0) {
                        et_grp_name.setText("");
                    }
                    lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_content, _group_name));

                } else if (v.getId() == R.id.ibtn_grp_content_item_search) {
                    Intent intent = new Intent(_context, WordActivity.class);
                    intent.putExtra("strContent", _strContent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                }
            }
            else if (selectedRbtn==rbtn_grp_phrase)
            {
              if(v.getId()==R.id.ibtn_grp_content_item_delete)
              {
                  SQLfunctions.deletePhrase(_idContent);// RowId
                  refreshSecondList(0,"",0);
              }else if (v.getId() == R.id.ibtn_grp_content_item_search) {
                  Intent intent = new Intent(_context, WordActivity.class);
                  intent.putExtra("strContent", _strContent);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  _context.startActivity(intent);
              }
            else if (selectedRbtn==rbtn_grp_relation){
                  if (v.getId() == R.id.tv_grp_item_name) {
                      et_grp_name.setText(_group_name);
                      refreshSecondList(R.id.tv_grp_item_name, _group_name, 0);
                  } else if (v.getId() == R.id.ibtn_grp_content_item_delete) {
                      SQLfunctions.deletePairFromRelation(_idContent);// RowId
                      refreshFirstList();
                      tv_grp_2lst_title.setText("Pairs in Relation: " + _group_name);
                      Cursor cursor = SQLfunctions.getRelationContent(_group_name);
                      if (cursor.getCount() == 0) {
                          et_grp_name.setText("");
                      }
                      lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_content, _group_name));

                  } else if (v.getId() == R.id.ibtn_grp_content_item_search) {
                      Intent intent = new Intent(_context, WordActivity.class);
                      intent.putExtra("strContent", _strContent);
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      _context.startActivity(intent);
                  }
              }
            }
        }
    }

    public static void refreshFirstList() {
        if (selectedRbtn == rbtn_grp_group) {
            Cursor cursor = SQLfunctions.getGroups();
            tv_grp_1lst_title.setText("Groups");
            tv_grp_2lst_title.setText("Words in group: ");
            lv_grp_1lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_name, ""));
        }else {
            Cursor cursor = SQLfunctions.getRelations();
            tv_grp_1lst_title.setText("Relations");
            tv_grp_2lst_title.setText("Pairs in Relation: ");
            lv_grp_1lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_name, ""));
        }
    }

    public static void refreshSecondList(int viewId, String group_name, int status) {
        if (selectedRbtn == rbtn_grp_group) {
            if (viewId == R.id.tv_grp_item_name) {
                if (status == 1)
                    tv_grp_2lst_title.setText("Words in group: ");
                else {
                    tv_grp_2lst_title.setText("Words in group: " + group_name);
                }
                Cursor cursor = SQLfunctions.getGroupContent(group_name);
                lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_content, group_name));
            }
        } else if (selectedRbtn==rbtn_grp_phrase) {
            Cursor cursor = SQLfunctions.getPhrases();
            lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_content, ""));
        }else if (selectedRbtn==rbtn_grp_relation){
            if (status == 1)
                tv_grp_2lst_title.setText("Pairs in Relation: ");
            else {
                tv_grp_2lst_title.setText("Pairs in Relation: " + group_name);
            }
            Cursor cursor = SQLfunctions.getRelationContent(group_name);
            lv_grp_2lst.setAdapter(new MyGroupCursorAdapter(_context, cursor, R.layout.list_item_group_content, group_name));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.mbtn_texts: {
                this.startActivity(new Intent(this, MainActivity.class));
                break;
            }
            case R.id.mbtn_words: {
                this.startActivity(new Intent(this, WordActivity.class));
                break;
            }
            case R.id.mbtn_groups: {
                this.startActivity(new Intent(this, GroupsActivity.class));
                break;
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
