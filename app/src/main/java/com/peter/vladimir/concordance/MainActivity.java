package com.peter.vladimir.concordance;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.FileNameMap;
import java.nio.Buffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //TODO show 3 lines with words , and show text
    //TODO add group and relations
    private static final String TAG = "MainActivity";
    private EditText et_texts_name;
    private EditText et_auth_name;
    private EditText et_file_name;
    private DatePicker dp_publish;
    private Button btn_search;
    private Button btn_load;
    private ListView list_texts;
    private Cursor cursor_texts;
    private DBHelper dbHelper;
    private MyCursorAdapter listAdapter;
    private StringBuilder[] text_data = new StringBuilder[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.texts_title);
        et_texts_name = (EditText) findViewById(R.id.et_txt_name);
        et_auth_name = (EditText) findViewById(R.id.et_auth_name);
        et_file_name = (EditText) findViewById(R.id.et_file_name);
        dp_publish = (DatePicker) findViewById(R.id.dp_publish);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_load = (Button) findViewById(R.id.btn_load);
        list_texts = (ListView) findViewById(R.id.list_texts);
        btn_search.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        SQLfunctions.setContext(getApplicationContext());
        refreshTextList();
    }


    public void refreshTextList() {
        cursor_texts = SQLfunctions.searchAllTexts();
        listAdapter = new MyCursorAdapter(getApplicationContext(), cursor_texts, MyCursorAdapter.ID_LIST_TEXT_ITEM);
        list_texts.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.mbtn_words:{
                this.startActivity(new Intent(this, WordActivity.class));
                break;
            }
            case R.id.mbtn_groups:{
               // this.startActivity(new Intent(this, MainActivity.class));
                break;
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_load) {
            if (et_file_name.getText().toString() != null) {
                if (!checkLoadFieldsOK()) {
                    return;
                }
                File file_txt = new File(Environment.getExternalStorageDirectory() + "/Download", et_file_name.getText().toString() + ".txt");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file_txt));
                    String line;
                    text_data[3] = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        text_data[3].append(line);
                        text_data[3].append('\n');
                    }

                    bufferedReader.close();
                    String date = DateFormat.getDateInstance().format(dp_publish.getCalendarView().getDate());
                    text_data[0] = new StringBuilder(et_texts_name.getText().toString());
                    text_data[1] = new StringBuilder(date);
                    text_data[2] = new StringBuilder(et_auth_name.getText().toString());
                    btn_search.setEnabled(false);
                    new AsyncTask<StringBuilder[], Void, Void>() {
                        @Override
                        protected Void doInBackground(StringBuilder[]... params) {
                            SQLfunctions.loadText(text_data[3], text_data);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            btn_search.setEnabled(true);
                            refreshTextList();
                            Toast.makeText(getApplicationContext(), "Text loaded", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else Toast.makeText(this, "Enter file name", Toast.LENGTH_SHORT).show();
        } else if (v == btn_search) {
            if (checkSearchField() != 0) {
                String[] arg = new String[2];
                arg[0] = "%" + et_texts_name.getText().toString() + "%";
                arg[1] = "%" + et_auth_name.getText().toString() + "%";
                cursor_texts = SQLfunctions.searchTexts(arg);
                listAdapter = new MyCursorAdapter(getApplicationContext(), cursor_texts, MyCursorAdapter.ID_LIST_TEXT_ITEM);
                list_texts.setAdapter(listAdapter);
            } else refreshTextList();
        }
    }

    private int checkSearchField() {
        int search_fields = 0;
        if (et_texts_name.getText().toString().length() > 0) {
            search_fields++;
        }
        if (et_auth_name.getText().toString().length() > 0) {
            search_fields++;
        }
        return search_fields;
    }

    private boolean checkLoadFieldsOK() {
        boolean noerror_status = true;
        if (et_texts_name.getText().toString().length() == 0) {
            Toast.makeText(this, "Must enter Text name!", Toast.LENGTH_SHORT).show();
            noerror_status = false;
        }
        if (et_auth_name.getText().toString().length() == 0) {
            Toast.makeText(this, "Must enter Author's name!", Toast.LENGTH_SHORT).show();
            noerror_status = false;
        }
        return noerror_status;
    }
}
