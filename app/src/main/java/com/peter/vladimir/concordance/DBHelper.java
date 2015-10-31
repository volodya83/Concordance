package com.peter.vladimir.concordance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Volodya on 19-Sep-15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    static  final String DATABASE_NAME="concordance.db";
private Context _context;
    public DBHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //onUpgrade(db, 1, 1);
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE_WORDS ="CREATE TABLE Words (" +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "word text UNIQUE," +
                "word_type text" +
                ");";
        final String CREATE_TABLE_TEXTS = "CREATE TABLE Texts (" +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "text_name text NOT NULL," +
                "text_date date" +
                ");";
        final String CREATE_TABLE_RELATIONS = "CREATE TABLE Relations (" +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "relation_word1 text," +
                "relation_word2 text," +
                "relation_id integer NOT NULL," +
                "relation_name text" +
                ");";
        final String CREATE_TABLE_GROUPS = "CREATE TABLE Groups ( " +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "group_name text NOT NULL, " +
                "word_str text NOT NULL " +
                ");";
        final String CREATE_TABLE_PHRASES = "CREATE TABLE Phrases ( " +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "phrase_str text NOT NULL " +
                ");";
        final String CREATE_TABLE_AUTHORS = "CREATE TABLE Authors (" +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "text_id integer NOT NULL," +
                "author_name text NOT NULL," +
                "FOREIGN KEY(text_id) REFERENCES Texts (_id) ON DELETE CASCADE ON UPDATE CASCADE " +
                ");";
        final String CREATE_TABLE_WORD_TEXT_REL = "CREATE TABLE Word_Text_Rel ( " +
                "_id integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "word_id integer NOT NULL, " +
                "text_id integer NOT NULL, " +
                "word_text_line integer, " +
                "word_text_type integer, " +
                "word_position integer, " +
                "FOREIGN KEY(text_id) REFERENCES Texts ( text_id ) " +
                ");";
        final String CREATE_TRIGGER_WORD_TEXT_REL = "CREATE TRIGGER Word_Text_Rel_Trigger01 " +
                " AFTER INSERT " +
                " ON Word_Text_Rel " +
                "WHEN NEW.word_id>14 " +
                "BEGIN " +
                "INSERT OR IGNORE INTO Words(word) VALUES (NEW.word_id); " +
                "UPDATE  Word_Text_Rel " +
                "SET word_id=(SELECT _id FROM Words WHERE word=new.word_id) " +
                "WHERE rowid=new.rowid; " +
                "END ";
        final String CREATE_TRIGGER_TEXTS="CREATE TRIGGER TEXTS_DELETE_TRIGGER01 " +
                "AFTER DELETE ON Texts " +
                "BEGIN " +
                "DELETE FROM Word_Text_Rel WHERE text_id= old._id; " +
                "DELETE FROM Words WHERE _id NOT IN (SELECT word_id FROM Word_Text_Rel) AND _id>14; " +
                "END";
        sqLiteDatabase.execSQL(CREATE_TABLE_WORDS);
        sqLiteDatabase.execSQL(CREATE_TABLE_TEXTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_AUTHORS);
        sqLiteDatabase.execSQL(CREATE_TABLE_RELATIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_GROUPS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PHRASES);
        sqLiteDatabase.execSQL(CREATE_TABLE_WORD_TEXT_REL);
        sqLiteDatabase.execSQL(CREATE_TRIGGER_WORD_TEXT_REL);
        sqLiteDatabase.execSQL(CREATE_TRIGGER_TEXTS);
        initSymbols(sqLiteDatabase);
    }

    private void initSymbols(SQLiteDatabase sqLiteDatabase) {
        String symbols = ".,(){}\"!?:;@&";
        ContentValues contentValues;
        for (int i = 0; i < symbols.length(); i++) {
            contentValues = new ContentValues();
            contentValues.put("word", (symbols.charAt(i)) + "\0");
            sqLiteDatabase.insert("Words", "", contentValues);
        }
        contentValues = new ContentValues();
        contentValues.put("word", ". ");
        int last_symbol_id=(int)sqLiteDatabase.insert("Words", "", contentValues);
        if (last_symbol_id!=14)
            Toast.makeText(_context,"Last symbol_id="+last_symbol_id,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Words");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Texts");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Authors");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Relations");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Groups");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Word_Text_Rel");
        onCreate(sqLiteDatabase);
    }
}
