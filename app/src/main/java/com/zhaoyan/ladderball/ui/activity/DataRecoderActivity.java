package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Event;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.EventRecentAdapter;
import com.zhaoyan.ladderball.ui.adapter.EventRecordAdapter;
import com.zhaoyan.ladderball.ui.view.DataRecordPlayerLayout;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class DataRecoderActivity extends BaseActivity {

    public static final String EXTRA_MATCH_ID = "matchId";
    public static final String EXTRA_TEAM_ID = "teamId";
    public static final String EXTRA_PART_NUMBER = "partNumber";

    @Bind(R.id.fl_players)
    FrameLayout mPlayersLayout;
    //    @Bind(R.id.fl_player_record)
//    FrameLayout mItemRecordLayout;
    @Bind(R.id.recent_event_recyclerview)
    RecyclerView mRecentRecyclerView;
    @Bind(R.id.event_record_recyclerview)
    RecyclerView mRecordRecyclerView;

    private EventRecentAdapter mRecentAdapter;
    private EventRecordAdapter mRecordAdapter;

    private long mMatchId;
    private long mTeamId;
    private int mPartNumber;

    private List<Player> mOnPitchPlayerList;

    private ProgressDialog mProgressDialog;

    private List<Event> mOnPitchPlayerEventList;

    public static Intent getStartIntent(Context context, long matchId, long teamId, int partNumber) {
        Intent intent = new Intent();
        intent.setClass(context, DataRecoderActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_PART_NUMBER, partNumber);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_recoder);

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecentRecyclerView.setLayoutManager(layoutManager);
        mRecentAdapter = new EventRecentAdapter(getApplicationContext());
        mRecentRecyclerView.setAdapter(mRecentAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecordRecyclerView.setLayoutManager(gridLayoutManager);
        mRecordAdapter = new EventRecordAdapter(getApplicationContext());
        mRecordRecyclerView.setAdapter(mRecordAdapter);

        Intent intent = getIntent();
        mMatchId = intent.getLongExtra(EXTRA_MATCH_ID, -1);
        mTeamId = intent.getLongExtra(EXTRA_TEAM_ID, -1);
        mPartNumber = intent.getIntExtra(EXTRA_PART_NUMBER, -1);
        Log.d("matchId:" + mMatchId + ",teamId:" + mTeamId + ",partNumber:" + mPartNumber);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        List<Player> allPlayerList = new Select().from(Player.class).where("matchId=? and teamId=?",
                mMatchId, mTeamId).execute();

        mOnPitchPlayerList = new ArrayList<>();
        for (Player player : allPlayerList) {
            Log.d(player.toString());
            if (player.isOnPitch) {
                mOnPitchPlayerList.add(player);
            }
        }
        //
        //左边球员号码区
        DataRecordPlayerLayout playerLayout = new DataRecordPlayerLayout(this, mOnPitchPlayerList);
        mPlayersLayout.addView(playerLayout);

        mOnPitchPlayerEventList = new ArrayList<>();

        initEvents();
    }

    private void initEvents() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //初始化场上球员的数据
                Event event = null;
                for (Player player : mOnPitchPlayerList) {
                    event = new Event();
                    event.matchId = mMatchId;
                    event.teamId = mTeamId;
                    event.playerId = player.playerId;
                    event.partNumber = mPartNumber;
                    event.save();

                    mOnPitchPlayerEventList.add(event);
                }
                subscriber.onNext("初始化球员事件完毕");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());

        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.setMessage("正在初始化球员数据...");
                        mProgressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showToast(getApplicationContext(), "初始化数据失败");
                    }

                    @Override
                    public void onNext(String s) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }
                        ToastUtil.showToast(getApplicationContext(), s);
                        Log.d("player.size:" + mOnPitchPlayerEventList.size());
                        mRecordAdapter.setDataList(mOnPitchPlayerEventList);
                        mRecordAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onBackPressed() {

    }
}
