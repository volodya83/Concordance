package com.peter.vladimir.concordance;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static com.peter.vladimir.concordance.SQLfunctions.listOfWords;

public class WordActivity extends AppCompatActivity implements View.OnClickListener {
    private static EditText et_search_word;
    private static TextView tv_word_data_info;
    private ImageButton ibtn_search_word;
    private ListView lv_texts;
    private ListView lv_found_words;
    private Cursor cursor_texts, cursor_words, cursor_word_data, cursor_phrase_data;
    private MyCursorAdapter listAdapter;
    private MyCursorAdapter wordListAdapter;
    private MyCursorAdapter wordDataAdapter;
    private MyArrayPhraseAdapter phraseAdapter;
    private EditText et_line_in_source;
    private EditText et_word_in_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        setTitle(R.string.words_title);

        et_search_word = (EditText) findViewById(R.id.et_search_word);
        tv_word_data_info = (TextView) findViewById(R.id.tv_word_data_info);
        et_line_in_source = (EditText)findViewById(R.id.et_line_in_source);
        et_word_in_source = (EditText)findViewById(R.id.et_word_in_sorce);
        ibtn_search_word = (ImageButton) findViewById(R.id.ibtn_search_word);
        ibtn_search_word.setOnClickListener(this);
        lv_texts = (ListView) findViewById(R.id.lv_texts_in_words);
        lv_found_words = (ListView) findViewById(R.id.lv_found_words);
        refreshTextList();
    }

    public void refreshTextList() {
        cursor_texts = SQLfunctions.searchAllTexts();
        listAdapter = new MyCursorAdapter(getApplicationContext(), cursor_texts, MyCursorAdapter.ID_EX_LIST_TEXT_ITEM);
        lv_texts.setAdapter(listAdapter);
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


    @Override
    public void onClick(View v) {
        String[] search_str = new String[1];
        String line_source, word_source;
        search_str = getArrPhrase(et_search_word.getText().toString());
        line_source = et_line_in_source.getText().toString();
        word_source = et_word_in_source.getText().toString();

        int[] text_id_arr = listAdapter.get_text_id_arr();
        Integer text_size = text_id_arr[text_id_arr.length - 1];
        if (line_source.length()>0 && word_source.length()>0){
            if (text_size!=1){
                Toast.makeText(this,"To search word by source data you need select only one text", Toast.LENGTH_SHORT).show();
            }else{
                tv_word_data_info.setText("Word by source");
                    cursor_words = SQLfunctions.findWordBySource(text_id_arr[0], line_source, word_source);
                    wordListAdapter = new MyCursorAdapter(getApplicationContext(), cursor_words, MyCursorAdapter.ID_LIST_FOUND_WORD_ITEM);
                    lv_found_words.setAdapter(wordListAdapter);
            }
        }
        else if (search_str.length == 0) {
            tv_word_data_info.setText("Words list");
            if (text_size != 0) {
                String arg = SQLfunctions.listOfTexts(text_id_arr);
                Toast.makeText(this, "Selected text count=" + text_size, Toast.LENGTH_SHORT).show();
                cursor_words = SQLfunctions.allWordsInTexts(arg);
                wordListAdapter = new MyCursorAdapter(getApplicationContext(), cursor_words, MyCursorAdapter.ID_LIST_FOUND_WORD_ITEM);
                lv_found_words.setAdapter(wordListAdapter);
            } else {
                cursor_words = SQLfunctions.allWords();
                wordListAdapter = new MyCursorAdapter(getApplicationContext(), cursor_words, MyCursorAdapter.ID_LIST_FOUND_WORD_ITEM);
                lv_found_words.setAdapter(wordListAdapter);
            }
        } else if (search_str.length == 1) {
            tv_word_data_info.setText("Word position");
            if (text_size != 0) {
                String arg = SQLfunctions.listOfTexts(text_id_arr);
                cursor_word_data = SQLfunctions.wordDataInTexts(arg, search_str);
                if (cursor_word_data.getCount() == 0)
                    Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show();
                wordDataAdapter = new MyCursorAdapter(this, cursor_word_data, MyCursorAdapter.ID_LIST_WORD_DATA_ITEM);
                lv_found_words.setAdapter(wordDataAdapter);
            } else {
                cursor_word_data = SQLfunctions.wordDataInAllText(search_str);
                if (cursor_word_data.getCount() == 0)
                    Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show();
                wordDataAdapter = new MyCursorAdapter(this, cursor_word_data, MyCursorAdapter.ID_LIST_WORD_DATA_ITEM);
                lv_found_words.setAdapter(wordDataAdapter);
            }
        } else {
            tv_word_data_info.setText("Phrase position");
            ArrayList<PhraseData> listPhraseData;
            if (text_size != 0) {
                String texts_id = SQLfunctions.listOfTexts(text_id_arr);
                listPhraseData = SQLfunctions.phraseDataInTexts(search_str, texts_id);
                if (listPhraseData.size() == 0)
                    Toast.makeText(this, "Phrase not found", Toast.LENGTH_SHORT).show();
                //adapter
                phraseAdapter = new MyArrayPhraseAdapter(this, R.layout.list_item_word_data, listPhraseData);
                lv_found_words.setAdapter(phraseAdapter);
            } else {
                listPhraseData = SQLfunctions.phraseDataInAllTexts(search_str);
                if (listPhraseData.size() == 0)
                    Toast.makeText(this, "Phrase not found", Toast.LENGTH_SHORT).show();

                //adapter
                phraseAdapter = new MyArrayPhraseAdapter(this, R.layout.list_item_word_data, listPhraseData);
                lv_found_words.setAdapter(phraseAdapter);
            }
        }
    }

    private String[] getArrPhrase(String str) {
        str = str.toLowerCase() + " ";

        char curChar;
        int wordCount = 0;
        String[] wordArr = new String[20];
        for (int start = 0, finish = 0; finish < str.length(); finish++) {
            curChar = str.charAt(finish);
            if (Character.isWhitespace(curChar) && finish == start) {
                start++;
            } else if (Character.isWhitespace(curChar)) {
                wordArr[wordCount] = str.substring(start, finish);
                wordCount++;
                start = finish + 1;
            } else if (Character.isLetterOrDigit(curChar) || curChar == '\'') {
            } else if (curChar == '.' || curChar == ',') {
                if (Character.isDigit(str.charAt(finish + 1)) && Character.isDigit(str.charAt(finish - 1))) {
                } else if (start != finish) {
                    wordArr[wordCount] = str.substring(start, finish);
                    wordCount++;
                    start = finish;
                    finish--;
                } else {
                    wordArr[wordCount] = str.substring(start, finish + 1);
                    wordCount++;
                    start = finish + 1;
                }
            } else {
                char[] symbolChar = {';', ':', '"', '!', '?', '(', ')', '{', '}', '@', '&'};
                boolean isSymbol = false;
                for (int i = 0; (i < symbolChar.length) && (!isSymbol); i++) {
                    if (curChar == symbolChar[i]) {
                        isSymbol = true;
                    }
                }
                if (isSymbol) {
                    if (start != finish) {
                        wordArr[wordCount] = str.substring(start, finish);
                        wordCount++;
                        start = finish;
                        finish--;
                    } else {
                        wordArr[wordCount] = str.substring(start, finish + 1);
                        wordCount++;
                        start = finish + 1;
                    }
                } else {
                    Toast.makeText(this, "Not expected symbol", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
        }
        return Arrays.copyOf(wordArr, wordCount);
    }


    public static class MyClickListener implements View.OnClickListener {
        private String _word;

        public MyClickListener(View view, String word) {
            _word = word;
        }

        @Override
        public void onClick(View v) {
            et_search_word.setText(_word);
        }
    }
}
