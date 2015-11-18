package com.peter.vladimir.concordance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

public class StatisticActivity extends AppCompatActivity {
    private Integer _textSize;
    private int[] _textIds;
    private String _statistic;

    private TextView tv_statistic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        tv_statistic = (TextView)findViewById(R.id.tv_statistic);
        _textSize = getIntent().getExtras().getInt("text_size");
        _textIds = getIntent().getExtras().getIntArray("text_id");
        displayStatistic();
        tv_statistic.setText(_statistic);
    }

    private void displayStatistic() {
        if (_textSize==0){
            _statistic = SQLfunctions.getTextStatistic(null);
        }else {

            _statistic = SQLfunctions.getTextStatistic(Arrays.copyOf(_textIds, _textIds.length-1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistic, menu);
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
