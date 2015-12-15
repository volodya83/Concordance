package com.peter.vladimir.concordance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
            TABLE_AUTHORS = "Authors", TABLE_GROUPS = "Groups", TABLE_RELATIONS = "Relations", TABLE_PHRASES="Phrases";
    final static int ALL_CAPITAL = 1, ONE_CAPITAL = 2, ALL_SMALL = 3, SYMBOL = 4, NUMBER = 5;
    private static final int NO_ID = 0;
    private static final String LOG_TAG = SQLfunctions.class.toString();
    private static final int NUM_OF_COMMON = 10;
    private static DBHelper dbHelper;
    private static int _line;
    private static int _word_position;
    private static SQLiteDatabase _sqLiteDatabase;
    private static long _cur_text_id;
    private static Context _context;
    private static HashMap<Integer, String> _textMap;
    private static long startTimer, endTimer;

    public static void setContext(Context context) {
        dbHelper = new DBHelper(context);
        _context = context;
        _sqLiteDatabase = dbHelper.getWritableDatabase();
    }
        //Separating each word of the text and insert in one transaction
    public static void loadText(StringBuilder text, StringBuilder[] text_data) {
        _cur_text_id = addTextData(text_data);
        //test
//        startTimer=System.nanoTime();
        _line = 1;
        String word = "";
        char curChar;
        int word_type = 0;
        int end_word = 0;
        int cur_size = 0, sent_num=1;
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
                    addWordToDB(word, word_type, cur_size, sent_num);
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
                        sent_num++;
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
                        sent_num++;
                        break;
                    }
                    case '?': {
                        addSymbolToDB(8);
                        sent_num++;
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
//        endTimer = System.nanoTime();
//        Log.d(LOG_TAG, "Load text timer1 = "+(endTimer-startTimer));
        _sqLiteDatabase.setTransactionSuccessful();
        _sqLiteDatabase.endTransaction();
//        endTimer = System.nanoTime();
//        Log.d(LOG_TAG, "Load text timer2 = "+(endTimer-startTimer));
    }
        //Inserting text information in Texts and Authors tables
    private static long addTextData(StringBuilder[] text_data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("text_name", text_data[0].toString());
        contentValues.put("text_date", text_data[1].toString());
        long text_id = _sqLiteDatabase.insert(TABLE_TEXTS, "", contentValues);

        String[] authors = text_data[2].toString().split(",");
        for (int i = 0; i < authors.length; i++) {
            contentValues = new ContentValues();
            contentValues.put("author_name", authors[i].trim());
            contentValues.put("text_id", text_id);
            _sqLiteDatabase.insert(TABLE_AUTHORS, "", contentValues);
        }
        return text_id;
    }
        //Insert symbol to Word_text_rel table without check/insert Words table
    private static void addSymbolToDB(long symbol_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("word_id", symbol_id + 1);
        contentValues.put("text_id", _cur_text_id);
        contentValues.put("word_text_type", SYMBOL);
        contentValues.put("word_text_line", _line);
        contentValues.put("word_position", 0);
        _sqLiteDatabase.insert(TABLE_WORD_TEXT_REL, "", contentValues);
    }
        //Insert word to Word_text_rel and insert or ignore to Words
    private static void addWordToDB(String word, int word_type, int word_size, int sent_num) {
        word = word.toLowerCase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word_id", word);
        contentValues.put("text_id", _cur_text_id);
        contentValues.put("word_text_type", word_type);
        contentValues.put("word_size", word_size);
        contentValues.put("word_text_sentence", sent_num);
        contentValues.put("word_text_line", _line);
        contentValues.put("word_position", _word_position);
        _sqLiteDatabase.insert(TABLE_WORD_TEXT_REL, "", contentValues);
    }
        //Return list of all texts information
    public static Cursor searchAllTexts() {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT _id, text_name, author_name, text_date " +
                                                "FROM ( SELECT text_id, GROUP_CONCAT(author_name) AS author_name " +
                                                        "FROM Authors GROUP BY text_id) JOIN Texts ON text_id=_id", null);
        setTextMap(cursor);
        return cursor;
    }
        //Return list of texts that found by name of text or author
    public static Cursor searchTexts(String[] arg) {
        return  _sqLiteDatabase.rawQuery("SELECT _id, text_name, author_name, text_date " +
                                        "FROM ( SELECT text_id, GROUP_CONCAT(author_name) AS author_name " +
                                                "FROM Authors GROUP BY text_id) JOIN Texts ON text_id=_id " +
                                                "WHERE text_name LIKE ? AND author_name LIKE ?", arg);
    }
        //Return all words of specific text to view whole text
    public static Cursor viewText(Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        return _sqLiteDatabase.rawQuery(" SELECT word, word_text_type, word_text_line " +
                                         "FROM Words JOIN Word_Text_Rel ON Words._id=Word_Text_Rel.word_id " +
                                         "WHERE Word_Text_Rel.text_id=? ", arg);
    }
        //Return list of words from specific texts, sorted by alphabet
    public static Cursor allWordsInTexts(String arg) {
        return _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, word " +
                                         "FROM Words JOIN (SELECT DISTINCT word_id " +
                                                          "FROM Word_Text_Rel " +
                                                          "WHERE text_id IN " + arg + ")ON Words._id=word_id " +
                                         "WHERE _id>14 " +
                                         "ORDER BY word ASC", null);
    }
    //Return list of all words in db, sorted by alphabet
    public static Cursor allWords() {
        return  _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, word " +
                "FROM Words " +
                "WHERE _id>14 " +
                "ORDER BY word ASC ", null);
    }
        //Delete text(executed trigger that delete all words of text and all info)
    public static int deleteText(Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        return _sqLiteDatabase.delete(TABLE_TEXTS, "_id=?", arg);
    }
        //Return information about word in specific texts
    public static Cursor wordDataInTexts(String arg, String[] search_str) {
        return _sqLiteDatabase.rawQuery("SELECT text_id, text_name, word_text_line, word_position, Word_Text_Rel._id " +
                "FROM Word_Text_Rel JOIN Texts ON text_id=Texts._id " +
                "WHERE word_id IN ( SELECT _id " +
                "FROM Words " +
                "WHERE word=? AND text_id IN " + arg + ")", search_str);
    }

    //Return information about word in all texts
    public static Cursor wordDataInAllText(String[] search_str) {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT text_id, text_name, word_text_line, word_position, Word_Text_Rel._id " +
                                                 "FROM Word_Text_Rel JOIN Texts ON text_id=Texts._id " +
                                                 "WHERE word_id IN ( SELECT _id " +
                                                                    "FROM Words " +
                                                                    "WHERE word=?)", search_str);
        return cursor;
    }

    //Return information about phrase in specific texts
    public static ArrayList<PhraseData> phraseDataInTexts(String[] search_str, String texts) {
        String phrase = listOfWords(search_str);
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT _id, word " +
                                                "FROM Words " +
                                                "WHERE word IN " + phrase, null);
        int[] phraseIdsArr = cursorToArrInt(cursor, search_str);
        if (phraseIdsArr==null)
        {
            Toast.makeText(_context, "Phrase not found", Toast.LENGTH_SHORT).show();
        }
        String phraseIds = listOfTexts(phraseIdsArr);
        cursor=_sqLiteDatabase.rawQuery("SELECT * " +
                                        "FROM Word_Text_Rel " +
                                        "WHERE word_id IN " + phraseIds + " AND  text_id IN " + texts, null);
        return new PhraseData().cursorToArrPhrase(cursor, phraseIdsArr);
    }

    //Return information about phrase in all texts
    public static ArrayList<PhraseData> phraseDataInAllTexts(String[] search_str) {
        String phrase = listOfWords(search_str);
        int[] phraseIdsArr=cursorToArrInt(_sqLiteDatabase.rawQuery("SELECT _id, word " +
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
                "WHERE word_id IN " + phraseIds, null);
        return new PhraseData().cursorToArrPhrase(cursor, phraseIdsArr);
    }

    //Convert array of text ids to list(x, y,...)
    public static String listOfTexts(int[] text_id_arr) {
        String arg;
        arg = Arrays.toString(Arrays.copyOf(text_id_arr, text_id_arr.length));
        arg = arg.replace("[", "(");
        arg = arg.replace("]", ")");
        return arg;
    }

        //Convert array of words to list(x, y,...)
    public static String listOfWords(String[] words_arr) {
        String arg;
        arg = Arrays.toString(Arrays.copyOf(words_arr, words_arr.length ));
        arg=arg.replace(", ", "', '");
        arg = arg.replace("[", "('");
        arg = arg.replace("]", "')");
        return arg;
    }

        //Reorder word_id by order words in phrase
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

        //Return context of phrase
    public static String getWordPhraseContext(Integer line, Integer text_id) {
        String[] arg = new String[1];
        arg[0] = text_id.toString();
        String lines = "("+(line-1)+","+line+","+(line+1)+")";
        startTimer=System.nanoTime();
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT word, word_text_type " +
                                                "FROM Words JOIN Word_Text_Rel ON Words._id=Word_Text_Rel.word_id " +
                                                "WHERE Word_Text_Rel.word_text_line IN "+lines+" AND Word_Text_Rel.text_id=? ", arg);

//        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT word, word_text_type " +
//                "FROM Words JOIN (SELECT word_id, word_text_type " +
//                "FROM Word_Text_Rel " +
//                "WHERE text_id=? AND word_text_line IN "+lines+" ) ON _id=word_id ", arg);
        return buildContext(cursor);
    }

        //Return string with three rows include the phrase
    private static String buildContext(Cursor cursor) {
        String str_context = "...", word = "";
        cursor.moveToFirst();
        endTimer=System.nanoTime();
        Log.d(LOG_TAG, "Timer for query getWordPhraseContext="+(endTimer-startTimer));
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

        //Find and return word by source info
    public static Cursor findWordBySource(int text_id, String line_source, String word_source) {
        return  _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, word " +
                                        "FROM Words JOIN (SELECT DISTINCT word_id " +
                                                        "FROM Word_Text_Rel " +
                                                        "WHERE word_text_line="+line_source+" AND " +
                                                                "word_position="+word_source+" AND " +
                                                                "text_id="+text_id+")ON Words._id=word_id ", null);
    }
    // Insert new word to table Group
    public static void insertWordToGroup(String grpName, String grpStr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("group_name", grpName.trim());
        contentValues.put("word_str", grpStr.trim());
        _sqLiteDatabase.insert(TABLE_GROUPS, null, contentValues);
    }
    // Insert new pair of words to table Relations
    public static void insertWordsToRelations(String relName, String[] words) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("relation_name", relName.trim());
        contentValues.put("relation_word1", words[0].trim());
        contentValues.put("relation_word2", words[1].trim());
        _sqLiteDatabase.insert(TABLE_RELATIONS, null, contentValues);
    }
    // Insert new phrase to table Phrases
    public static void insertPhrase(String phrase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("phrase_str", phrase.trim());
        _sqLiteDatabase.insert(TABLE_PHRASES, null, contentValues);
    }

    // Return list of groups
    public static Cursor getGroups(){
        return _sqLiteDatabase.rawQuery("SELECT * " +
                                        "FROM Groups " +
                                        "GROUP BY group_name ", null);
    }

    // Return list of relations
    public static Cursor getRelations(){
        return _sqLiteDatabase.rawQuery("SELECT DISTINCT _id, relation_name " +
                                        "FROM Relations " +
                                        "GROUP BY relation_name ", null);
    }
    // Return list of phrases
    public static Cursor getPhrases(){
        return _sqLiteDatabase.rawQuery("SELECT * " +
                                        "FROM Phrases ", null);
    }

    //Return list of words from specific group
    public static Cursor getGroupContent(String group_name) {
        return _sqLiteDatabase.rawQuery("SELECT _id, word_str " +
                                        "FROM Groups " +
                                        "WHERE group_name='"+group_name+"'", null);
    }
    //Delete specific word
    public static void deleteContentInGroup(int id) {
        _sqLiteDatabase.delete(TABLE_GROUPS, "_id="+id,null );
    }
    //Delete group
    public static void deleteGroup(String group_name) {
        _sqLiteDatabase.delete(TABLE_GROUPS, "group_name='"+group_name+"'",null );
    }
    //Delete phrase
    public static void deletePhrase(int id) {
        _sqLiteDatabase.delete(TABLE_PHRASES, "_id="+id,null );
    }
    //Return list of pairs from specific relation
    public static Cursor getRelationContent(String relation_name) {
        return _sqLiteDatabase.rawQuery("SELECT _id, relation_word1, relation_word2 " +
                                        "FROM Relations " +
                                        "WHERE relation_name='"+relation_name+"'", null);
    }
    //Delete  Relation
    public static void deleteRelation(String relation_name) {
        _sqLiteDatabase.delete(TABLE_RELATIONS, "relation_name='"+relation_name+"'",null );
    }

    //Delete one pair of words from Relation
    public static void deletePairFromRelation(int id) {
        _sqLiteDatabase.delete(TABLE_RELATIONS, "_id="+id,null );
    }

    //Return list with information about words in specific group
    //from all texts
    public static Cursor groupDataInAllText(String groupName) {
        String wordIdList ="(SELECT Words._id, word_str " +
                            "FROM Groups LEFT OUTER JOIN Words ON word_str=word " +
                            "WHERE group_name='"+groupName+"' ) ";
        String wordRel="( SELECT text_name, word_text_line, word_position, word_id " +
                        " FROM Word_Text_Rel JOIN Texts ON Word_Text_Rel.text_id=Texts._id )";
        return _sqLiteDatabase.rawQuery("SELECT word_str, text_name, word_text_line, word_position " +
                                        "FROM "+wordIdList+" AS Wil LEFT OUTER JOIN "+wordRel+" AS Wrel ON Wil._id=Wrel.word_id ", null);

    }

    //Return list with information about words in specific group
    //from text
    public static Cursor groupDataInText(String groupName, String arg) {
        String wordIdList ="(SELECT Words._id, word_str " +
                            "FROM Groups LEFT OUTER JOIN Words ON word_str=word " +
                            "WHERE group_name='"+groupName+"' ) ";
        String wordRel="( SELECT text_name, word_text_line, word_position, word_id " +
                            " FROM Word_Text_Rel JOIN Texts ON Word_Text_Rel.text_id=Texts._id " +
                            " WHERE Texts._id IN "+arg+")";
        return _sqLiteDatabase.rawQuery("SELECT word_str, text_name, word_text_line, word_position " +
                                         "FROM "+wordIdList+" AS Wil LEFT OUTER JOIN "+wordRel+" AS Wrel ON Wil._id=Wrel.word_id ", null);

    }

    //Return statistic of texts
    public static String getTextStatistic(int[] textIds) {
        String statistic;
        String arg="";
        if(textIds!=null) {
            String textNames = "";
            arg = listOfTexts(textIds);

            Cursor cursor = _sqLiteDatabase.rawQuery("SELECT text_name " +
                                                    "FROM Texts " +
                                                    "WHERE _id IN " + arg, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                textNames = textNames.concat(cursor.getString(0) + ",");
                cursor.moveToNext();
            }
            textNames = textNames.substring(0, textNames.length()-1);
            statistic = new String("Statistic for texts "+textNames+ ": \n");
            arg=" AND text_id IN "+arg;
        }
        else {
            statistic = new String("Statistic for all texts: \n");
        }
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT AVG(word_size) AS average " +
                                                 "FROM Word_Text_Rel " +
                                                 "WHERE word_text_type <> 4 " + arg, null);
        cursor.moveToFirst();
        statistic = statistic.concat("average size of word: "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(line_size) " +
                "FROM " + "( SELECT SUM(word_size) AS line_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " + arg +
                "GROUP BY word_text_line, text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of letters in line: "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(sentence_size) " +
                "FROM " + "( SELECT SUM(word_size) AS sentence_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " + arg +
                "GROUP BY word_text_sentence, text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of letters in sentence: "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(text_size) " +
                "FROM ( SELECT SUM(word_size) AS text_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " +
                "GROUP BY text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of letters in all texts : "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(line_word_size) " +
                "FROM " + "( SELECT COUNT(_id) AS line_word_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " + arg +
                "GROUP BY word_text_line, text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of words in line: "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(sentence_word_size) " +
                "FROM " + "( SELECT COUNT(_id) AS sentence_word_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " + arg +
                "GROUP BY word_text_sentence, text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of words in sentence: "+cursor.getInt(0)+"\n");

        cursor = _sqLiteDatabase.rawQuery("SELECT AVG(text_words_size) " +
                "FROM ( SELECT COUNT(_id) AS text_words_size " +
                "FROM Word_Text_Rel " +
                "WHERE word_text_type <> 4 " +
                "GROUP BY text_id )", null);
        cursor.moveToFirst();
        statistic = statistic.concat("average number of words in all texts : "+cursor.getInt(0)+"\n");

        statistic = statistic.concat(commonness(arg) + "\n");

        return statistic;
    }

    private static String commonness(String arg) {
        Cursor cursor = _sqLiteDatabase.rawQuery("SELECT DISTINCT word, count " +
                                                "FROM " + "( SELECT COUNT(_id) AS count, word_id " +
                                                            "FROM Word_Text_Rel " +
                                                            "WHERE word_text_type <> 4 " + arg +
                                                            "GROUP BY word_id )     LEFT OUTER JOIN Words ON word_id=Words._id " +
                                                "ORDER BY count DESC", null);
        cursor.moveToFirst();
        int i = 0;
        String common = "The 10 commonness words in text/s is: \n";
        while (i<NUM_OF_COMMON && !cursor.isAfterLast()){
            common = common.concat(i+1+")" + cursor.getString(0) + "-" +cursor.getInt(1) + "\n");
            i++;
            cursor.moveToNext();
        }
        return common;
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


}






