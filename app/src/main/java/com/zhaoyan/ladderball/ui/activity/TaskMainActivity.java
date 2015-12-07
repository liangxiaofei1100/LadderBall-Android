package com.zhaoyan.ladderball.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhaoyan.ladderball.R;

import butterknife.ButterKnife;

public class TaskMainActivity extends AppCompatActivity {

    public static final String EXTRA_MATCH_ID = "match_id";

    private int mMatchId;

    public static Intent getStartIntent(Context context, int matchId) {
        Intent intent = new Intent();
        intent.setClass(context, TaskMainActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);
        ButterKnife.bind(this);

        mMatchId = getIntent().getIntExtra(EXTRA_MATCH_ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //判断任务是否设置
        Intent intent = new Intent();
        intent.setClass(this, TaskSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_commit_task) {

        }

        return super.onOptionsItemSelected(item);
    }

}
