package com.zhaoyan.ladderball.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.MatchDetailRequest;
import com.zhaoyan.ladderball.http.response.MatchDetailResponse;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.PlayerHorizontalAdapter;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class TaskMainActivity extends BaseActivity {

    public static final String EXTRA_MATCH_ID = "match_id";

    @Bind(R.id.task_main_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.stv_task_main_first)
    SettingItemView mSectionOneView;
    @Bind(R.id.stv_task_main_second)
    SettingItemView mSectionTwoView;
    @Bind(R.id.stv_task_main_third)
    SettingItemView mSectionThreeView;

    @Bind(R.id.bar_loading)
    ProgressBar mLoadingBar;

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

        getMatchDetail();
        //判断任务是否设置,是个问题，怎么判断
    }

    private void getMatchDetail() {
        MatchDetailRequest request = new MatchDetailRequest(getApplicationContext());
        request.matchId = mMatchId;
        Observable<MatchDetailResponse> observable = mLadderBallApi.doGetMatchDetail(request)
                .subscribeOn(Schedulers.io());
        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mLoadingBar.setVisibility(View.VISIBLE);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MatchDetailResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.toString());
                        ToastUtil.showToast(getApplicationContext(), "获取失败，请重试");
                    }

                    @Override
                    public void onNext(MatchDetailResponse matchDetailResponse) {

                    }
                });
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
