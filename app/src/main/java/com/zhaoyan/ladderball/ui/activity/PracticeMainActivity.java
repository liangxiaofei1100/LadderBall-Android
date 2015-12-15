package com.zhaoyan.ladderball.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.PracticeDetailRequest;
import com.zhaoyan.ladderball.http.response.PracticeDetailResponse;
import com.zhaoyan.ladderball.model.PartData;
import com.zhaoyan.ladderball.model.TmpMatch;
import com.zhaoyan.ladderball.model.TmpPartData;
import com.zhaoyan.ladderball.model.TmpPlayer;
import com.zhaoyan.ladderball.model.TmpTeam;
import com.zhaoyan.ladderball.ui.adapter.PracticeMainAdapter;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.DensityUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.MatchUtil;
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

public class PracticeMainActivity extends BaseActivity {

    public static final String EXTRA_MATCH_ID = "match_id";
    public static final String EXTRA_IS_COMPLETE = "is_complete";
    private static final int REQUEST_CODE_SETTING = 0;
    private static final int REQUEST_CODE_DATA_RECORD = 1;

    @Bind(R.id.task_main_recyclerview)
    RecyclerView mRecyclerView;

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

    @Bind(R.id.bar_loading)
    ProgressBar mLoadingBar;

    @Bind(R.id.ll_task_main_part)
    LinearLayout mPartLayout;

    @Bind(R.id.task_main_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.tv_task_main_load_fail)
    TextView mLoadFailView;

    @Bind(R.id.tv_task_main_setting)
    TextView mSettingView;

    @Bind(R.id.btn_task_main_check_data)
    Button mCheckDataBtn;

    LinearLayoutManager mLinearLayoutManager;

    private PracticeMainAdapter mAdapter;

    private long mMatchId;
    private long mTeamId;
    private int mPartMinutes;

    private boolean mIsComplete;

    public static final String EXTRA_HAS_SETTED = "has_setted";

//    private List<PartData> mPartDataList = new ArrayList<>();

    public static Intent getStartIntent(Context context, long matchId, boolean isComplete) {
        Intent intent = new Intent();
        intent.setClass(context, PracticeMainActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_IS_COMPLETE, isComplete);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);
        ButterKnife.bind(this);

        mMatchId = getIntent().getLongExtra(EXTRA_MATCH_ID, -1);
        mIsComplete = getIntent().getBooleanExtra(EXTRA_IS_COMPLETE, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new PracticeMainAdapter(this, new ArrayList<TmpPlayer>());
        mRecyclerView.setAdapter(mAdapter);

        if (mIsComplete) {
            mSettingView.setEnabled(false);
            mCheckDataBtn.setText("查看数据结果");
        }

        getMatchDetail();
    }

    private void getMatchDetail() {
        PracticeDetailRequest request = new PracticeDetailRequest(getApplicationContext());
        request.matchId = mMatchId;
        Observable<PracticeDetailResponse> observable = mLadderBallApi.doGetPracticeDetail(request)
                .subscribeOn(Schedulers.io());
        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mLoadingBar.setVisibility(View.VISIBLE);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<PracticeDetailResponse, TmpMatch>() {
                    @Override
                    public TmpMatch call(PracticeDetailResponse response) {
                        TmpMatch match = new Select().from(TmpMatch.class).where("matchId=?", response.id)
                                .executeSingle();
                        if (match == null) {
                            match = new TmpMatch();
                        }
                        match.matchId = response.id;
                        match.playerNumber = response.playerNumber;
                        match.startTime = response.startTime;
                        match.address = response.address;
                        match.totalPart = response.totalPart;
                        match.partMinutes = response.partMinutes;

                        mPartMinutes = response.partMinutes;

                        TmpTeam teamHome = new Select().from(TmpTeam.class).where("matchId=? and teamId=?",
                                response.id, response.teamHome.id).executeSingle();
                        if (teamHome == null) {
                            teamHome = new TmpTeam();
                        }
                        teamHome.matchId = response.id;
                        teamHome.teamId = response.teamHome.id;
                        teamHome.name = response.teamHome.name;
                        teamHome.isAssiged = response.teamHome.isAsigned;
                        teamHome.color = response.teamHome.color;
                        teamHome.logoUrl = response.teamHome.logoURL;
                        teamHome.save();

                        match.teamHome = teamHome;

                        TmpTeam teamVisitor = new Select().from(TmpTeam.class).where("matchId=? and teamId=?",
                                response.id, response.teamVisitor.id).executeSingle();
                        if (teamVisitor == null) {
                            teamVisitor = new TmpTeam();
                        }

                        teamVisitor.matchId = response.id;
                        teamVisitor.teamId = response.teamVisitor.id;
                        teamVisitor.name = response.teamVisitor.name;
                        teamVisitor.isAssiged = response.teamVisitor.isAsigned;
                        teamVisitor.color = response.teamVisitor.color;
                        teamVisitor.logoUrl = response.teamVisitor.logoURL;
                        teamVisitor.save();

                        match.teamVisitor = teamVisitor;

                        TmpPlayer player;
                        if (teamHome.isAssiged) {
                            mTeamId = teamHome.teamId;
                            match.teamHome.players.clear();
                            for (PracticeDetailResponse.HttpPlayer httpPlayer : response.teamHome.players) {
                                player = new Select().from(TmpPlayer.class).where("matchId=? and teamId=?" +
                                                " and playerId=?",
                                        response.id, response.teamHome.id, httpPlayer.id).executeSingle();
                                if (player == null) {
                                    player = new TmpPlayer();
                                    //默认情况，首发球员即上场球员
                                    player.isOnPitch = httpPlayer.isFirst;
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
                            mTeamId = teamVisitor.teamId;
                            match.teamVisitor.players.clear();
                            for (PracticeDetailResponse.HttpPlayer httpPlayer : response.teamVisitor.players) {
                                player = new Select().from(TmpPlayer.class).where("matchId=? and teamId=?" +
                                                " and playerId=?",
                                        response.id, response.teamVisitor.id, httpPlayer.id).executeSingle();
                                if (player == null) {
                                    player = new TmpPlayer();
                                    //默认情况，首发球员即上场球员
                                    player.isOnPitch = httpPlayer.isFirst;
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

                        TmpPartData partData;
                        match.partDatas.clear();
                        for (PracticeDetailResponse.HttpPartData httpPartData : response.partDatas) {
                            partData = new Select().from(TmpPartData.class).where("matchId=? and partNumber=?",
                                    mMatchId, httpPartData.partNumber).executeSingle();
                            if (partData == null) {
                                partData = new TmpPartData();
                            }
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
                .subscribe(new Subscriber<TmpMatch>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                        mLoadingBar.setVisibility(View.GONE);

                        if (!MatchUtil.hasSetPractice(getApplicationContext(), mMatchId)) {
                            doTaskSetting();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(e);
                        mLoadingBar.setVisibility(View.GONE);
                        ToastUtil.showToast(getApplicationContext(), "获取失败，请重试");

                        mLoadFailView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onNext(TmpMatch match) {
                        Log.d("match:" + match.matchId);
                        mCheckDataBtn.setEnabled(true);
                        mScrollView.setVisibility(View.VISIBLE);
                        showData(match);
                    }
                });
    }

    @OnClick(R.id.tv_task_main_load_fail)
    void reload() {
        mLoadFailView.setVisibility(View.GONE);
        getMatchDetail();
    }

    private void showData(TmpMatch match) {
        Log.d("parts:" + match.totalPart + "," + match.partDatas.size());
        //show top
        mTeamHomeAssignedView.setVisibility(match.teamHome.isAssiged ?
                View.VISIBLE : View.GONE);
        mTeamVisitorAssignedView.setVisibility(match.teamVisitor.isAssiged ?
                View.VISIBLE : View.GONE);
        if (TextUtils.isEmpty(match.teamHome.color)) {
            mTeamHomeName.setText(match.teamHome.name);
        } else {
            mTeamHomeName.setText(match.teamHome.name + "\n" + "(" + match.teamHome.color + ")");
        }

        if (TextUtils.isEmpty(match.teamVisitor.color)) {
            mTeamVisitorName.setText(match.teamVisitor.name);
        } else {
            mTeamVisitorName.setText(match.teamVisitor.name + "\n" + "(" + match.teamVisitor.color + ")");
        }

        mTeamScoreView.setText("VS");
        mTeamRuleView.setVisibility(View.GONE);
        mAddressView.setText(match.address);
        mDateView.setText(TimeUtil.getFormatterDate(match.startTime));
        if (mAdapter != null) {
            mAdapter.clear();
            if (match.teamHome.isAssiged) {
                mAdapter.setDataList(match.teamHome.players);
            } else if (match.teamVisitor.isAssiged) {
                mAdapter.setDataList(match.teamVisitor.players);
            }
            mAdapter.notifyDataSetChanged();
            //显示首发阵容情况
            mStartingLineupView.setText(getString(R.string.starting_lineup, mAdapter.getItemCount()));
        }
        SettingItemView itemView;
        View view;
        String[] matchParts = getResources().getStringArray(R.array.match_parts);

        int height = DensityUtil.dip2px(getApplicationContext(), 48);
        Log.d("height:" + height);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

        PartItemClickListener itemClickListener = new PartItemClickListener();

        mPartLayout.removeAllViews();

        //title
        itemView = new SettingItemView(this);
        itemView.setLayoutParams(params);
        itemView.setTitleText(getString(R.string.record_title, match.totalPart, match.partMinutes));
        itemView.setRightArrowVisiblily(View.GONE);
        mPartLayout.addView(itemView);

        boolean hasPartComplete = false;
        for (TmpPartData pardData : match.partDatas) {
            if (pardData.isComplete) {
                hasPartComplete = true;
            }

            itemView = new SettingItemView(this);
            itemView.setLayoutParams(params);
            itemView.setId(pardData.partNumber);
            //itemView.setTitleText(matchParts[pardData.partNumber - 1]);
            itemView.setTitleText("第" + pardData.partNumber + "节");
            itemView.setSummaryText(pardData.isComplete ? "已完成录入" : "未开始");
            itemView.setOnClickListener(itemClickListener);
            mPartLayout.addView(itemView);
        }

        //但是为了能测试，这个功能暂时不开启
        //TODO
        //只要有一个小节比赛已提交，就不能再去修改任务设置了
//        if (hasPartComplete) {
//            mSettingView.setEnabled(false);
//        }
    }

    private class PartItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Log.d("partNumber:" + id);

            if (mIsComplete) {
                startActivity(DataRepairActivity.getStartIntent(getApplicationContext(), mMatchId, mTeamId, id, true));
                return;
            }

            PartData partData = new Select().from(PartData.class).where("matchId=? and partNumber=?",
                    mMatchId, id).executeSingle();
            if (partData == null) {
                Log.e("Can not find this pard data");
                return;
            }

            Log.d("isPardComplete:" + partData.isComplete);
            if (partData.isComplete) {
                startActivity(DataRepairActivity.getStartIntent(getApplicationContext(), mMatchId, mTeamId, id, false));
            } else {
                startActivityForResult(DataRecoderActivity.getStartIntent(PracticeMainActivity.this,
                        mMatchId, mTeamId, id, mPartMinutes), REQUEST_CODE_DATA_RECORD);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.tv_task_main_setting)
    void doTaskSetting() {
        startActivityForResult(PracticeSettingActivity.getStartIntent(this, mMatchId), REQUEST_CODE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SETTING) {
                getMatchDetail();
            } else if (requestCode == REQUEST_CODE_DATA_RECORD) {
                getMatchDetail();
            }
        }
    }
}
