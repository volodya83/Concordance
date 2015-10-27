package com.peter.vladimir.concordance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Volodya on 19-Sep-15.
 */
public abstract class SQLfunctions {

    private static final String TAG = "SQLfunctions";
    private static final int COL_TEXT_NAME = 1;
    final static String TABLE_WORD_TEXT_REL = "Word_Text_Rel", TABLE_TEXTS = "Texts", TABLE_WORDS = "Words",
            TABLE_AUTHORS = "Authors", TABLE_GROUPS = "Groups", TABLE_RELATIONS = "Relations";
    final static int ALL_CAPITAL = 1, ONE_CAPITAL = 2, ALL_SMALL = 3, SYMBOL = 4, NUMBER = 5;
    private static final int NO_ID = 0;
    private static DBHelper dbHelper;
    private static int _line;
    private static int _word_position;
    private static SQLiteDatabase _sqLiteDatabase;
    private static long _cur_text_id;
    private static Context _context;
    private static HashMap<Integer, String> _textMap;

    public static void setContext(Context context) {
        dbHelper = new DBHelper(context);
        _context = context;
        _sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public static void loadText(StringBuilder text, StringBuilder[] text_data) {
        _cur_text_id = addTextData(text_data);
        _line = 1;
        String word = "";
        char curChar;
        int word_type = 0;
        int end_word = 0;
        int cur_size = 0;
        _sqLiteDatabase.beginTransaction();
        for (int i = 0; i < text.length(); i++) {
            curChar = text.charAt(i);
            if (curChar >= '0' && curChar <= '9') {
                word = word + curChar;
                cur_size++;
                if (text.charAt(i + 1) == '.' || text.charAt(i + 1) == ',')
                    if (text.charAt(i + 2) >= '0' && text.charAt(i + 2) <= '9') {
                        word = word + text.charAt(i + 1);
                        i++;
                        cur_size++;
                    }
            } else if (curChar >= 'A' && curChar <= 'Z') {
                if (cur_size == 0)
                    word_type = ONE_CAPITAL;
                else word_type = ALL_CAPITAL;
                word = word + curChar;
                cur_size++;
            } else if (curChar >= 'a' && curChar <= 'z') {
                if (cur_size == 0)
                    word_type = ALL_SMALL;
                word = word + curChar;
                cur_size++;
            } else if (curChar == '\'') {
                word = word + curChar;
                cur_size++;
            } else {
                if (cur_size > 0) {
                    if (word_type == SYMBOL)
                        word_type = NUMBER;
                    _word_position++;
                    addWordToDB(word, word_type);
                    word = "";
                }
                cur_size = 0;
                word_type = SYMBOL;
                switch (curChar) {
                    case '\n': {
                        _line++;
                        _word_position=0;
                        break;
                    }
                    case '.': {
                        if (text.charAt(i + 1) == ' ') {
                            addSymbolToDB(13);
                        } else addSymbolToDB(0);
                        break;
                    }
                    case ',': {
                        addSymbolToDB(1);
                        break;
                    }
                    case '(': {
                        addSymbolToDB(2);
                        break;
                    }
                    case ')': {
                        addSymbolToDB(3);
                        break;
                    }
                    case '{': {
                        addSymbolToDB(4);
                        break;
                    }
                    case '}': {
                        addSymbolToDB(5);
                        break;
                    }
                    case '"': {
                        addSymbolToDB(6);
                        break;
                    }
                    case '!': {
                        addSymbolToDB(7);
                        break;
                    }
                    case '?': {
                        addSymbolToDB(8);
                        break;
                    }
                    case ':': {
                        addSymbolToDB(9);
                        break;
                    }
                    case ';': {
                        addSymbolToDB(10);
                        break;
                    }
                    case '@': {
                        addSymbolToDB(11);
                        break;
                    }
                    case '&': {
                        addSymbolToDB(12);
                        break;
                    }
                    default: {
                        break;
                    }
                }

            }
        }
        _sqLiteDatabase.setTransactionSuccessful();
        _sqLiteDatabase.endTransaction();
    }

    private static long addTextData(StringBuilder[] text_data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("text_name", text_data[0].toString());
        contentValues.put("text_date", text_data[1].toString());
        long text_id = _sqLiteDatabase.insert(TABLE_TEXTS, "", contentValues);
//        if (text_id == (-1))
//            Toast.makeText(_context, "addTextData=-1 text_name=" + text_data[0], Toast.LENGTH_SHORT).show();

        String[] authors = text_data[2].toString().split(",");
        for (int i = 0; i < authors.length; i++) {
            contentValues = new ContentValues();
            contentValues.put("author_name", authors[i].trim());
            contentValues.put("text_id", text_id);
            long answer = _sqLiteDatabase.insert(TABLE_AUTHORS, "", contentValues);
//            if (answer == (-1))
//                Toast.makeText(_context, "addTextData=-1 author=" + authors[i], Toast.LENGTH_SHORT).show();

        }
        return text_id;
    }

    private static void addSymbolToDB(long symbol_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("word_id", symbol_id + 1);
        contentValues.put("text_id", _cur_text_id);
        contentValues.put("word_text_type", SYMBOL);
        contentValues.put("word_text_line", _line);
        contentValues.put("word_position", 0);
        long answer = _sqLiteDatabase.insert(TABLE_WORD_TEXT_REL, "", contentValues);
//        if (answer == (-1))
//            Toast.makeText(_context, "addSymbolToDB=-1", Toast.LENGTH_SHORT).show();
    }

    private static void addWordToDB(String word, int word_type) {
        word = word.toLowerCase();
        Cursor word_id_cursor;
        ContentValues contentValues = new ContentValues();
//        String[] arg=new String[1];
//        arg[0]=word;
//        contentValues.put("word", word);
//        long word_id = _sqLiteDatabase.insert(TABLE_WORDS, "", contentValues);
//        if (word_id == (-1)) {
//            word_id_cursor = _sqLiteDatabase.rawQuery("SELECT _id   FROM Words WHERE word = ?;", arg);
//            word_id_cursor.moveToFirst();
//            word_id = word_id_cursor.getLong(0);
//        }
//        //Toast.makeText(_context, "addWordToDB word_id=" + word_id, Toast.LENGTH_SHORT).show();
//
//        contentValues = new ContentValues();
        contentValues.put("word_id", word);
        contentValues.put("text_id", _cur_text_id);
        contentValues.put("word_text_type", word_type);
        contentValues.put("word_text_line", _line);
        contentValues.put("word_position", _word_position);
        long answer2 = _sqLiteDatabase.insert(TABLE_WORD_TEXT_REL, "", contentValues);
//        if (answer2 == (-1))
//            Toast.makeText(_context, "addWordToDB WORD_TEXT_REL=-1 word=" + word, Toast.LENGTH_SHORT).show();
    }

    public static Cursor searchAllTexts() {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT _id, text_name, author_name, text_date " +
                                                 "FROM ( SELECT text_id, GROUP_CONCAT(author_name) AS author_name " +
                                                        "FROM Authors GROUP BY text_id) JOIN Texts ON text_id=_id", null);
        setTextMap(cursor);
        return cursor;
    }

    public static Cursor searchTexts(String[] arg) {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT _id, text_name, author_name, text_date " +
                                                 "FROM ( SELECT text_id, GROUP_CONCAT(author_name) AS author_name " +
                                                        "FROM Authors GROUP BY text_id) JOIN Texts ON text_id=_id " +
                                                        "WHERE text_name LIKE ? AND author_name LIKE ?", arg);
        return cursor;
    }

    public static Cursor viewText(Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT word, word_text_type, word_text_line " +
                                                 "FROM Words JOIN Word_Text_Rel ON Words._id=Word_Text_Rel.word_id " +
                                                 "WHERE Word_Text_Rel.text_id=? ", arg);
//                Cursor cursor=_sqLiteDatabase.rawQuery("SELECT _id, word_id, text_id "+
//                                                "FROM Word_Text_Rel ", null);
        return cursor;
    }

    public static Cursor allWordsInTexts(String arg) {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, word " +
                                                 "FROM Words JOIN (SELECT DISTINCT word_id " +
                                                                  "FROM Word_Text_Rel " +
                                                                  "WHERE text_id IN " + arg + ")ON Words._id=word_id " +
                                                 "WHERE _id>14 " +
                                                 "ORDER BY word ASC", null);

        return cursor;
    }

    public static Cursor allWords() {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, word " +
                                                 "FROM Words " +
                                                 "WHERE _id>14 " +
                                                 "ORDER BY word ASC ", null);
        return cursor;
    }

    public static int deleteText(Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        return _sqLiteDatabase.delete(TABLE_TEXTS, "_id=?", arg);
    }

    public static Cursor wordDataInTexts(String arg, String[] search_str) {
        Cursor cursor=_sqLiteDatabase.rawQuery( "SELECT text_id, text_name, word_text_line, word_position, Word_Text_Rel._id " +
                                                "FROM Word_Text_Rel JOIN Texts ON text_id=Texts._id "+
                                                "WHERE word_id IN (SELECT _id " +
                                                                  "FROM Words " +
                                                                  "WHERE word=? AND text_id IN "+arg+")" , search_str);
        return cursor;
    }

    public static Cursor wordDataInAllText(String[] search_str) {
        Cursor cursor=_sqLiteDatabase.rawQuery( "SELECT text_id, text_name, word_text_line, word_position, Word_Text_Rel._id " +
                                                "FROM Word_Text_Rel JOIN Texts ON text_id=Texts._id "+
                                                "WHERE word_id IN (SELECT _id " +
                                                                  "FROM Words " +
                                                                  "WHERE word=?)" , search_str);
        return cursor;
    }

    public static ArrayList<PhraseData> phraseDataInTexts(String[] search_str, String texts) {
        String phrase = listOfWords(search_str);
        int[] phraseIdsArr=cursorToArrInt(_sqLiteDatabase.rawQuery("SELECT _id " +
                "FROM Words " +
                "WHERE word IN " + phrase, null), search_str);
        if (phraseIdsArr==null)
        {
            Toast.makeText(_context, "Phrase not found", Toast.LENGTH_SHORT).show();
            //return null;
        }
        String phraseIds = listOfTexts(phraseIdsArr);
        Cursor cursor=_sqLiteDatabase.rawQuery("SELECT * " +
                "FROM Word_Text_Rel " +
                "WHERE text_id IN " + texts + "AND word_id IN " + phraseIds, null);
        return new PhraseData().cursorToArrPhrase(cursor, phraseIdsArr);
    }

    public static ArrayList<PhraseData> phraseDataInAllTexts(String[] search_str) {
        String phrase = listOfWords(search_str);
        int[] phraseIdsArr=cursorToArrInt(_sqLiteDatabase.rawQuery("SELECT _id, word " +
                                                                         "FROM Words " +
                                                                         "WHERE word IN "+phrase, null), search_str);
        if (phraseIdsArr==null)
        {
            Toast.makeText(_context, "Phrase not found", Toast.LENGTH_SHORT).show();
            //return null;
        }
        String phraseIds = listOfTexts(phraseIdsArr);
        Cursor cursor=_sqLiteDatabase.rawQuery( "SELECT * " +
                                                "FROM Word_Text_Rel " +
                                                "WHERE word_id IN "+phraseIds, null);
        return new PhraseData().cursorToArrPhrase(cursor, phraseIdsArr);
    }

    public static String listOfTexts(int[] text_id_arr) {
        String arg;
        arg = Arrays.toString(Arrays.copyOf(text_id_arr, text_id_arr.length));
        arg = arg.replace("[", "(");
        arg = arg.replace("]", ")");
        return arg;
    }
    public static String listOfWords(String[] words_arr) {
        String arg;
        arg = Arrays.toString(Arrays.copyOf(words_arr, words_arr.length ));
        arg=arg.replace(", ", "', '");
        arg = arg.replace("[", "('");
        arg = arg.replace("]", "')");
        return arg;
    }

    private static int[] cursorToArrInt(Cursor cursor, String[] search_str) {
        int[] phraseIdsArr = new int[cursor.getCount()];
        if(cursor.getCount()<search_str.length)
        {
            return null;
        }
        else {
            cursor.moveToFirst();
            int countWord = 0;
            String curWord;
            for (int i = 0; !cursor.isAfterLast(); i++, cursor.moveToNext()) {
                curWord = cursor.getString(1);
                for (int j = 0; j < search_str.length; j++) {
                    if (search_str[j].compareTo(curWord) == 0) {
                        phraseIdsArr[j] = cursor.getInt(0);
                        countWord++;
                    }
                }
            }
            return phraseIdsArr;
        }
    }

    public static HashMap<Integer, String> getTextMap() {
        return _textMap;
    }

    public static void setTextMap(Cursor cursor) {
        HashMap<Integer, String> textMap=new HashMap<Integer, String>();
        cursor.moveToFirst();
        for (;!cursor.isAfterLast(); cursor.moveToNext())
        {
            textMap.put(cursor.getInt(0),cursor.getString(COL_TEXT_NAME));
        }

        SQLfunctions._textMap = textMap;
    }

    public static String getWordContext(Integer line, Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        String lines = "("+(line-1)+","+line+","+(line+1)+")";
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT word, word_text_type " +
                "FROM Words JOIN Word_Text_Rel ON Words._id=Word_Text_Rel.word_id " +
                "WHERE Word_Text_Rel.text_id=? AND Word_Text_Rel.word_text_line IN "+lines, arg);

        return buildContext(cursor);
    }


    private static String buildContext(Cursor cursor) {
        String str_context = "...", word = "";
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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
            cursor.moveToNext();
            if (!cursor.isAfterLast()) {
                if (cursor.getInt(1) != SQLfunctions.SYMBOL) {
                    word = word + " ";
                }
            }
            str_context = str_context + word;
        }
        return str_context + "...";
    }


}






