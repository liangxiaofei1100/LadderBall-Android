package com.zhaoyan.ladderballmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.zhaoyan.ladderballmanager.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateTimeActivity extends AppCompatActivity {

    @Bind(R.id.datepicker)
    DatePicker mDatePicker;
    @Bind(R.id.timepicker)
    TimePicker mTimePicker;

    public static final int TYPE_PICKER_DATE = 0;
    public static final int TYPE_PICKER_TIME = 1;
    private int mType = TYPE_PICKER_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item0 = menu.findItem(0);
        MenuItem item1 = menu.findItem(1);

        if (mType == TYPE_PICKER_DATE) {
            item1.setTitle("下一步");
            item0.setVisible(false);
        } else {
            item0.setVisible(true);
            item1.setTitle("完成");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0, "上一步").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,1,1,"下一步").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                mTimePicker.setVisibility(View.INVISIBLE);
                mDatePicker.setVisibility(View.VISIBLE);

                setType(TYPE_PICKER_DATE);
                break;
            case 1:
                if (mType == TYPE_PICKER_DATE) {
                    mTimePicker.setVisibility(View.VISIBLE);
                    mDatePicker.setVisibility(View.INVISIBLE);

                    setType(TYPE_PICKER_TIME);
                } else {
                    //finish
                    getTime();
                }
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mDatePicker.getYear(), mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());

        Intent intent = new Intent();
        intent.putExtra("datetime", calendar.getTimeInMillis());
        setResult(RESULT_OK, intent);

        this.finish();
    }

    private void setType(int type) {
        mType = type;
        invalidateOptionsMenu();
    }
}
