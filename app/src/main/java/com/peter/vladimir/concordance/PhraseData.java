package com.peter.vladimir.concordance;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Volodya on 30-Sep-15.
 */
public class PhraseData {
    private static final String TAG = "PhraseData";
    private static final int COL_ROW_ID = 0;
    private static final int COL_WORD_ID = 1;
    private static final int COL_TEXT_ID = 2;
    private static final int COL_LINE = 3;
    private static final int COL_POSITION = 5;
    private int _lineStart;
    private int _lineEnd;
    private int _positionStart;
    private int _positionEnd;
    private int _text_id;
    private String _textName;
    private int _rowIdStart;
    private int _rowIdEND;
    //private String _phrase;
    //private Cursor _cursor;

//    public PhraseData(Cursor cursor)
//    {
//        _cursor=cursor;
//    }

    public PhraseData() {

    }

    public PhraseData(int textId, int lineStart, int positionStart, int lineEnd, int positionEnd) {
        _text_id = textId;
        _lineStart = lineStart;
        _positionStart = positionStart;
        _lineEnd = lineEnd;
        _positionEnd = positionEnd;
    }

    public ArrayList<PhraseData> cursorToArrPhrase(Cursor cursor, int[] phraseIdsArr) {
        cursor.moveToFirst();
        int curCursor;
        int lineStart;
        int positionStart;
        int curTextId;
        int cur_id, next_id;
        ArrayList<PhraseData> phraseDataArrayList = new ArrayList<PhraseData>();

        for (int j; !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.getInt(COL_WORD_ID) == phraseIdsArr[0]) {
                curCursor = cursor.getPosition();
                lineStart = cursor.getInt(COL_LINE);
                positionStart= cursor.getInt(COL_POSITION);
                curTextId = cursor.getInt(COL_TEXT_ID);
                cur_id = cursor.getInt(COL_ROW_ID);

                for (j = 1; j < phraseIdsArr.length; j++) {
                    cursor.moveToNext();
                    if(cursor.isAfterLast()){
                        break;
                    }
                    next_id = cursor.getInt(COL_ROW_ID);
                    if (cursor.getInt(COL_WORD_ID) != phraseIdsArr[j] || next_id-cur_id!=1) {
                        break;
                    }
                }
                if (cursor.isAfterLast()) {
                    break;
                }
                if (j == phraseIdsArr.length && cursor.getInt(COL_TEXT_ID) == curTextId) {
                    phraseDataArrayList.add(new PhraseData(curTextId, lineStart, positionStart,
                            cursor.getInt(COL_LINE), cursor.getInt(COL_POSITION)));
                }
                cursor.moveToPosition(curCursor);
            }
        }
        if (phraseDataArrayList == null)
            Log.d(TAG, "cursorToArrPhrase return null");
        return phraseDataArrayList;

    }

    public int get_lineStart() {
        return _lineStart;
    }

    public int get_lineEnd() {
        return _lineEnd;
    }

    public int get_positionStart() {
        return _positionStart;
    }

    public int get_positionEnd() {
        return _positionEnd;
    }

    public int get_text_id() {
        return _text_id;
    }

    public String get_textName() {
        return _textName;
    }

    public void set_textName(String _textName) {
        this._textName = _textName;
    }
}
