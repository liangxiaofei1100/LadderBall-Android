package com.zhaoyan.ladderball.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
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

    private Observable<Integer> mItemObservable;

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

        playerLayout.setOnItemClickListener(new DataRecordPlayerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Event event = mOnPitchPlayerEventList.get(position);
                Log.d("matchId:" + event.matchId + ",teamId:" + event.teamId + ",playerId:" + event.playerId);
                mRecordAdapter.setDataList(event);
                mRecordAdapter.notifyDataSetChanged();
            }
        });

        mOnPitchPlayerEventList = new ArrayList<>();

//        registerForContextMenu(mRecordRecyclerView);

        initEvents();

        mItemObservable = RxBus.get().register(RxBusTag.EVENT_RECORD_ITEM, Integer.class);
        mItemObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(final Integer integer) {
                Log.d("position:" + integer);

                String[] menus = getItemMenu(integer);
                new AlertDialog.Builder(DataRecoderActivity.this)
                        .setItems(menus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Event event = mRecordAdapter.getItem();
                                handleEvent(integer, which, event);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create().show();
            }
        });
    }

    private void initEvents() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //初始化场上球员的数据
                Event event = null;
                for (Player player : mOnPitchPlayerList) {
                    event = new Select().from(Event.class).where("matchId=? and teamId=? and playerId=?",
                            mMatchId, mTeamId, player.playerId).executeSingle();
                    if (event == null) {
                        event = new Event();
                    }
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
                        //默认加载第一个球员的Event
                        Event event = mOnPitchPlayerEventList.get(0);
                        mRecordAdapter.setDataList(event);
                        mRecordAdapter.notifyDataSetChanged();
                    }
                });
    }

    private String[] getItemMenu(int position) {
        switch (position) {
            case 0:
                return getResources().getStringArray(R.array.event_jinqiu);
            case 1:
                return getResources().getStringArray(R.array.event_jiaoqiu);
            case 2:
                return getResources().getStringArray(R.array.event_yuewei);
            case 3:
                return getResources().getStringArray(R.array.event_guoren);
            case 4:
                return getResources().getStringArray(R.array.event_shezheng);
            case 12:
                return getResources().getStringArray(R.array.event_pujiushemen);
            case 13:
                return getResources().getStringArray(R.array.event_shoupaoqiu);
            case 14:
                return getResources().getStringArray(R.array.event_huangpai);
            case 15:
                return getResources().getStringArray(R.array.event_function);
        }
        return  null;
    }

    private void handleEvent(int position, int menuPosition, Event event) {
        switch (position) {
            case 0:
                if (menuPosition == 0) {
                    event.jinQiu += 1;
                } else {
                    event.zhuGong += 1;
                }
                break;
            case 1:
                if (menuPosition == 0) {
                    event.jiaoQiu += 1;
                } else if (menuPosition == 1) {
                    event.renYiQiu += 1;
                } else {
                    event.bianJieQiu += 1;
                }
                break;
            case 2:
                if (menuPosition == 0) {
                    event.yueWei += 1;
                } else {
                    event.shiWu += 1;
                }
                break;
            case 3:
                if (menuPosition == 0) {
                    event.guoRenChengGong += 1;
                } else {
                    event.guoRenShiBai += 1;
                }
                break;
            case 4:
                if (menuPosition == 0) {
                    event.sheZheng += 1;
                } else if (menuPosition == 1) {
                    event.shePian += 1;
                } else {
                    event.sheMenBeiDu += 1;
                }
                break;
            case 12:
                if (menuPosition == 0) {
                    event.puJiuSheMen += 1;
                } else {
                    event.danDao += 1;
                }
                break;
            case 13:
                if (menuPosition == 0) {
                    event.shouPaoQiu += 1;
                } else {
                    event.qiuMenQiu += 1;
                }
                break;
            case 14:
                if (menuPosition == 0) {
                    event.huangPai += 1;
                } else if (menuPosition == 1){
                    event.hongPai += 1;
                } else if (menuPosition == 2) {
                    event.fanGui += 1;
                } else {
                    event.wuLongQiu += 1;
                }
                break;
            case 15:
                if (menuPosition == 0) {
                    //换人
                } else {
                    //提交数据
                }
                break;
        }
        if (position != 15) {
            event.save();
            mRecordAdapter.notifyItemChanged(position);
        }
    }

    private void doCommitEventData() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(RxBusTag.EVENT_RECORD_ITEM, mItemObservable);
    }
}
