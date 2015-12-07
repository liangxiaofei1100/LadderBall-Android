package com.zhaoyan.ladderball.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.PlayerHorizontalAdapter;
import com.zhaoyan.ladderball.ui.view.SettingItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskMainActivity extends AppCompatActivity {

    public static final String EXTRA_MATCH_ID = "match_id";

    @Bind(R.id.task_main_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.stv_task_main_first)
    SettingItemView mSectionOneView;
    @Bind(R.id.stv_task_main_second)
    SettingItemView mSectionTwoView;
    @Bind(R.id.stv_task_main_third)
    SettingItemView mSectionThreeView;

    LinearLayoutManager mLinearLayoutManager;

    private PlayerHorizontalAdapter mAdapter;

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

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        List<Player> playerList = new ArrayList<>();
        //test
        //test
        Player player;
        for (int i=0; i<6; i++) {
            player = new Player();
            player.number = i;
            playerList.add(player);
        }
        //test
        //test
        mAdapter= new PlayerHorizontalAdapter(this, playerList);
        mRecyclerView.setAdapter(mAdapter);

        //判断任务是否设置
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

    @OnClick(R.id.stv_task_main_first)
    void doRecordSectionOne() {
        Intent intent = new Intent();
        intent.setClass(this, DataRecoderActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.stv_task_main_second)
    void doRecordSectionTwo() {

    }

    @OnClick(R.id.stv_task_main_third)
    void doRecordSectionThree() {

    }

    @OnClick(R.id.tv_task_main_setting)
    void doTaskSetting() {
        Intent intent = new Intent();
        intent.setClass(this, TaskSettingActivity.class);
        startActivity(intent);
    }

}
