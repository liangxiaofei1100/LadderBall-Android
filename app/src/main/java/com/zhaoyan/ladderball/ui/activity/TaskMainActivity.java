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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.MatchDetailRequest;
import com.zhaoyan.ladderball.http.response.MatchDetailResponse;
import com.zhaoyan.ladderball.model.Match;
import com.zhaoyan.ladderball.model.PartData;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.model.Team;
import com.zhaoyan.ladderball.ui.adapter.PlayerHorizontalAdapter;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.DensityUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.TimeUtil;
import com.zhaoyan.ladderball.util.ToastUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TaskMainActivity extends BaseActivity {

    public static final String EXTRA_MATCH_ID = "match_id";
    private static final int REQUEST_CODE_SETTING = 0;

    @Bind(R.id.task_main_recyclerview)
    RecyclerView mRecyclerView;

//    @Bind(R.id.stv_task_main_first)
//    SettingItemView mSectionOneView;
//    @Bind(R.id.stv_task_main_second)
//    SettingItemView mSectionTwoView;
//    @Bind(R.id.stv_task_main_third)
//    SettingItemView mSectionThreeView;

    //最上面的队伍对阵情况
    @Bind(R.id.tv_task_item_home_team_name_color)
    TextView mTeamHomeName;
    @Bind(R.id.tv_task_item_visitor_team_name_color)
    TextView mTeamVisitorName;
    @Bind(R.id.tv_task_item_date)
    TextView mDateView;
    @Bind(R.id.tv_task_item_address)
    TextView mAddressView;
    @Bind(R.id.tv_task_item_home_team_status)
    TextView mTeamHomeAssignedView;
    @Bind(R.id.tv_task_item_visitor_team_status)
    TextView mTeamVisitorAssignedView;
    @Bind(R.id.tv_task_item_score)
    TextView mTeamScoreView;
    @Bind(R.id.tv_task_item_rule)
    TextView mTeamRuleView;

    @Bind(R.id.tv_player_title)
    TextView mStartingLineupView;//首发阵容标题

    @Bind(R.id.tv_task_main_record_title)
    TextView mRecordTitleView;//比赛记录标题

    @Bind(R.id.bar_loading)
    ProgressBar mLoadingBar;

    @Bind(R.id.ll_task_main_record)
    LinearLayout mRecordLayout;

    LinearLayoutManager mLinearLayoutManager;

    private PlayerHorizontalAdapter mAdapter;

    private long mMatchId;

    public static Intent getStartIntent(Context context, long matchId) {
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

        mMatchId = getIntent().getLongExtra(EXTRA_MATCH_ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter= new PlayerHorizontalAdapter(this, new ArrayList<Player>());
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
                .map(new Func1<MatchDetailResponse, Match>() {
                    @Override
                    public Match call(MatchDetailResponse response) {
                        Match match = new Select().from(Match.class).where("matchId=?", response.id)
                                .executeSingle();
                        if (match == null) {
                            match = new Match();
                        }
                        match.matchId = response.id;
                        match.playerNumber  = response.playerNumber;
                        match.startTime = response.startTime;
                        match.address = response.address;
                        match.totalPart = response.totalPart;
                        match.partMinutes = response.partMinutes;

                        Team teamHome = new Select().from(Team.class).where("matchId=? and teamId=?",
                                response.id, response.teamHome.id).executeSingle();
                        if (teamHome == null) {
                            teamHome = new Team();
                        }
                        teamHome.matchId = response.id;
                        teamHome.teamId = response.teamHome.id;
                        teamHome.name = response.teamHome.name;
                        teamHome.isAssiged = response.teamHome.isAsigned;
                        teamHome.color = response.teamHome.color;
                        teamHome.logoUrl = response.teamHome.logoURL;
                        teamHome.save();

                        match.teamHome = teamHome;

                        Team teamVisitor = new Select().from(Team.class).where("matchId=? and teamId=?",
                                response.id, response.teamVisitor.id).executeSingle();
                        if (teamVisitor == null) {
                            teamVisitor = new Team();
                        }

                        teamVisitor.matchId = response.id;
                        teamVisitor.teamId = response.teamVisitor.id;
                        teamVisitor.name = response.teamVisitor.name;
                        teamVisitor.isAssiged = response.teamVisitor.isAsigned;
                        teamVisitor.color = response.teamVisitor.color;
                        teamVisitor.logoUrl = response.teamVisitor.logoURL;
                        teamVisitor.save();

                        match.teamVisitor = teamVisitor;

                        Player player;
                        if (teamHome.isAssiged) {
                            match.teamHome.players.clear();
                            for (MatchDetailResponse.HttpPlayer httpPlayer : response.teamHome.players) {
                                player = new Select().from(Player.class).where("matchId=? and teamId=?" +
                                        " and playerId=?",
                                        response.id, response.teamHome.id, httpPlayer.id).executeSingle();
                                if (player == null) {
                                    player = new Player();
                                }
                                player.matchId = response.id;
                                player.teamId = response.teamHome.id;
                                player.playerId = httpPlayer.id;
                                player.number = httpPlayer.number;
                                player.name = httpPlayer.name;
                                player.isFirst = httpPlayer.isFirst;
                                player.save();

                                teamHome.players.add(player);
                            }
                        }

                        if (teamVisitor.isAssiged) {
                            match.teamVisitor.players.clear();
                            for (MatchDetailResponse.HttpPlayer httpPlayer : response.teamVisitor.players) {
                                player = new Select().from(Player.class).where("matchId=? and teamId=?" +
                                                " and playerId=?",
                                        response.id, response.teamVisitor.id, httpPlayer.id).executeSingle();
                                if (player == null) {
                                    player = new Player();
                                }
                                player.matchId = response.id;
                                player.teamId = response.teamVisitor.id;
                                player.playerId = httpPlayer.id;
                                player.number = httpPlayer.number;
                                player.name = httpPlayer.name;
                                player.isFirst = httpPlayer.isFirst;
                                player.save();

                                teamVisitor.players.add(player);
                            }
                        }

                        PartData partData;
                        match.partDatas.clear();
                        for (MatchDetailResponse.HttpPartData httpPartData : response.partDatas) {
                            partData = new PartData();
                            partData.matchId = response.id;
                            partData.partNumber = httpPartData.partNumber;
                            partData.isComplete = httpPartData.isComplete;
                            partData.save();

                            match.partDatas.add(partData);
                        }
                        match.save();
                        Log.d("finish saved");
                        return match;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Match>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                        mLoadingBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(e);
                        mLoadingBar.setVisibility(View.GONE);
                        ToastUtil.showToast(getApplicationContext(), "获取失败，请重试");
                        TaskMainActivity.this.finish();
                    }

                    @Override
                    public void onNext(Match match) {
                        Log.d("match:" + match.matchId);
                        showData(match);
                    }
                });
    }

    private void showData(Match match) {
        Log.d("parts:" + match.totalPart + "," + match.partDatas.size());
        //show top
        mTeamHomeAssignedView.setVisibility(match.teamHome.isAssiged?
                View.VISIBLE : View.GONE);
        mTeamVisitorAssignedView.setVisibility(match.teamVisitor.isAssiged?
                View.VISIBLE : View.GONE);
        mTeamHomeName.setText(match.teamHome.name + "\n" + "(" + match.teamHome.color + ")");
        mTeamVisitorName.setText(match.teamVisitor.name + "\n" + "(" + match.teamVisitor.color + ")");
        mTeamScoreView.setText("VS");
        mTeamRuleView.setVisibility(View.GONE);
        mAddressView.setText(match.address);
        mDateView.setText(TimeUtil.getFormatterDate(match.startTime));
        //显示首发阵容情况
        mStartingLineupView.setText(getString(R.string.starting_lineup, match.playerNumber));
        if (mAdapter != null) {
            mAdapter.clear();
            if (match.teamHome.isAssiged) {
                mAdapter.setDataList(match.teamHome.players);
            } else if (match.teamVisitor.isAssiged) {
                mAdapter.setDataList(match.teamVisitor.players);
            }
            mAdapter.notifyDataSetChanged();
        }
        //显示比赛记录
        mRecordTitleView.setText(getString(R.string.record_title, match.totalPart, match.partMinutes));

        SettingItemView itemView;
        View view;
        String[] matchParts = getResources().getStringArray(R.array.match_parts);

        int height = DensityUtil.dip2px(getApplicationContext(), 48);
        Log.d("height:" + height);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        PartItemClickListener itemClickListener = new PartItemClickListener();

        mRecordLayout.removeAllViews();
        for (PartData pardData: match.partDatas) {
            if (pardData.partNumber > matchParts.length) {
                //暂时只内置了6个小节的标题文本，一场比赛不会分成这么多小节吧
                break;
            }
            itemView = new SettingItemView(this);
            itemView.setLayoutParams(params);
            itemView.setId(pardData.partNumber);
            itemView.setTitleText(matchParts[pardData.partNumber - 1]);
            itemView.setSummaryText(pardData.isComplete ? "已完成录入" : "未开始");
            itemView.setOnClickListener(itemClickListener);
            mRecordLayout.addView(itemView);
        }
    }

    private class PartItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case 1:
                    ToastUtil.showToast(getApplicationContext(), "click " + 1);
                    doRecordSectionOne();
                    break;
                case 2:
                    doRecordSectionTwo();
                    ToastUtil.showToast(getApplicationContext(), "click " + 2);
                    break;
                case 3:
                    doRecordSectionThree();
                    ToastUtil.showToast(getApplicationContext(), "click " + 3);
                    break;
                default:
                    break;
            }
        }
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

//    @OnClick(R.id.stv_task_main_first)
    void doRecordSectionOne() {
        Intent intent = new Intent();
        intent.setClass(this, DataRecoderActivity.class);
        startActivity(intent);
    }

//    @OnClick(R.id.stv_task_main_second)
    void doRecordSectionTwo() {

    }

//    @OnClick(R.id.stv_task_main_third)
    void doRecordSectionThree() {

    }

    @OnClick(R.id.tv_task_main_setting)
    void doTaskSetting() {
        startActivityForResult(TaskSettingActivity.getStartIntent(this, mMatchId), REQUEST_CODE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING && resultCode == RESULT_OK) {
            getMatchDetail();
        }
    }
}
