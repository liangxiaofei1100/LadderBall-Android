package com.zhaoyan.ladderball.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.http.request.EventCollectionRequest;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.model.EventItem;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.model.PlayerEvent;
import com.zhaoyan.ladderball.ui.adapter.EventRecentAdapter;
import com.zhaoyan.ladderball.ui.adapter.EventRecordAdapter;
import com.zhaoyan.ladderball.ui.view.DataRecordPlayerLayout;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
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

    private List<PlayerEvent> mOnPitchPlayerEventList;

    private Observable<Integer> mRecordObserverable;
    private Observable<Integer> mRecentObserverable;

    private long mMatchStartTime;

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
                PlayerEvent playerEvent = mOnPitchPlayerEventList.get(position);
                Log.d("matchId:" + playerEvent.matchId + ",teamId:" + playerEvent.teamId + ",playerId:" + playerEvent.playerId);
                mRecordAdapter.setDataList(playerEvent);
                mRecordAdapter.notifyDataSetChanged();
            }
        });

        mOnPitchPlayerEventList = new ArrayList<>();

        initEvents();

        mRecordObserverable = RxBus.get().register(RxBusTag.EVENT_RECORD_ITEM, Integer.class);
        mRecordObserverable.subscribe(new Action1<Integer>() {
            @Override
            public void call(final Integer integer) {
                Log.d("position:" + integer);

                switch (integer) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                        String[] menus = getItemMenu(integer);
                        new AlertDialog.Builder(DataRecoderActivity.this)
                                .setItems(menus, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PlayerEvent playerEvent = mRecordAdapter.getItem();
                                        handleEvent(integer, which, playerEvent);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create().show();
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                        PlayerEvent playerEvent = mRecordAdapter.getItem();
                        handleEvent(integer, -1, playerEvent);
                        break;
                }
            }
        });

        mRecentObserverable = RxBus.get().register(RxBusTag.EVENT_REMOVE_RECENT_ITEM, Integer.class);
        mRecentObserverable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (mRecentAdapter != null) {
                            mRecentAdapter.removeItem(integer);
                        }
                    }
                });
    }

    private void initEvents() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //初始化场上球员的数据
                PlayerEvent playerEvent = null;
                for (Player player : mOnPitchPlayerList) {
                    playerEvent = new Select().from(PlayerEvent.class).where("matchId=? and teamId=? and playerId=?",
                            mMatchId, mTeamId, player.playerId).executeSingle();
                    if (playerEvent == null) {
                        playerEvent = new PlayerEvent();
                    }
                    playerEvent.matchId = mMatchId;
                    playerEvent.teamId = mTeamId;
                    playerEvent.playerId = player.playerId;
                    playerEvent.playerNumber = player.number;
                    playerEvent.partNumber = mPartNumber;
                    playerEvent.save();

                    mOnPitchPlayerEventList.add(playerEvent);
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

                        DataRecoderActivity.this.finish();
                    }

                    @Override
                    public void onNext(String s) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }
//                        ToastUtil.showToast(getApplicationContext(), s);
                        Log.d("player.size:" + mOnPitchPlayerEventList.size());
                        //默认加载第一个球员的Event
                        PlayerEvent playerEvent = mOnPitchPlayerEventList.get(0);
                        mRecordAdapter.setDataList(playerEvent);
                        mRecordAdapter.notifyDataSetChanged();

                        new AlertDialog.Builder(DataRecoderActivity.this)
                                .setTitle("第" + mPartNumber + "节比赛")
                                .setMessage("比赛开始？")
                                .setPositiveButton("比赛开始", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mMatchStartTime = System.currentTimeMillis();
                                        if (mRecordAdapter != null) {
                                            mRecordAdapter.setStartTime(mMatchStartTime);
                                        }
                                    }
                                })
                                .setNegativeButton("还没开始", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataRecoderActivity.this.finish();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();
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
        return null;
    }

    private void handleEvent(int position, int menuPosition, PlayerEvent playerEvent) {
        int eventCode = -1;
        switch (position) {
            case 0:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_JIN_QIU;
                    playerEvent.jinQiu += 1;
                } else {
                    eventCode = EventCode.EVENT_ZHU_GONG;
                    playerEvent.zhuGong += 1;
                }
                break;
            case 1:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_JIAO_QIU;
                    playerEvent.jiaoQiu += 1;
                } else if (menuPosition == 1) {
                    eventCode = EventCode.EVENT_REN_YI_QIU;
                    playerEvent.renYiQiu += 1;
                } else {
                    eventCode = EventCode.EVENT_BIAN_JIE_QIU;
                    playerEvent.bianJieQiu += 1;
                }
                break;
            case 2:
                if (menuPosition == 0) {
                    playerEvent.yueWei += 1;
                    eventCode = EventCode.EVENT_YUE_WEI;
                } else {
                    eventCode = EventCode.EVENT_SHI_WU;
                    playerEvent.shiWu += 1;
                }
                break;
            case 3:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_GUO_REN_CHENG_GONG;
                    playerEvent.guoRenChengGong += 1;
                } else {
                    eventCode = EventCode.EVENT_GUO_REN_SHI_BAI;
                    playerEvent.guoRenShiBai += 1;
                }
                break;
            case 4:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_SHE_ZHENG;
                    playerEvent.sheZheng += 1;
                } else if (menuPosition == 1) {
                    eventCode = EventCode.EVENT_SHE_PIAN;
                    playerEvent.shePian += 1;
                } else {
                    eventCode = EventCode.EVENT_SHE_MEN_BEI_DU;
                    playerEvent.sheMenBeiDu += 1;
                }
                break;
            case 5:
                eventCode = EventCode.EVENT_CHUAN_QIU_CHENG_GONG;
                playerEvent.chuanQiuChengGong += 1;
                break;
            case 6:
                eventCode = EventCode.EVENT_WEI_XIE_QIU;
                playerEvent.weiXieQiu += 1;
                break;
            case 7:
                eventCode = EventCode.EVENT_CHUAN_QIU_SHI_BAI;
                playerEvent.chuanQiuShiBai += 1;
                break;
            case 8:
                eventCode = EventCode.EVENT_FENG_DU_SHE_MEN;
                playerEvent.fengDuSheMen += 1;
                break;
            case 9:
                eventCode = EventCode.EVENT_LAN_JIE;
                playerEvent.lanJie += 1;
                break;
            case 10:
                eventCode = EventCode.EVENT_QIANG_DUAN;
                playerEvent.qiangDuan += 1;
                break;
            case 11:
                eventCode = EventCode.EVENT_JIE_WEI;
                playerEvent.jieWei += 1;
                break;
            case 12:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_BU_JIU_SHE_MEN;
                    playerEvent.puJiuSheMen += 1;
                } else {
                    eventCode = EventCode.EVENT_BU_JIU_DAN_DAO;
                    playerEvent.danDao += 1;
                }
                break;
            case 13:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_SHOU_PAO_QIU;
                    playerEvent.shouPaoQiu += 1;
                } else {
                    eventCode = EventCode.EVENT_QIU_MEN_QIU;
                    playerEvent.qiuMenQiu += 1;
                }
                break;
            case 14:
                if (menuPosition == 0) {
                    eventCode = EventCode.EVENT_HUANG_PAI;
                    playerEvent.huangPai += 1;
                } else if (menuPosition == 1) {
                    eventCode = EventCode.EVENT_HONG_PAI;
                    playerEvent.hongPai += 1;
                } else if (menuPosition == 2) {
                    eventCode = EventCode.EVENT_FAN_GUI;
                    playerEvent.fanGui += 1;
                } else {
                    eventCode = EventCode.EVENT_WU_LONG_QIU;
                    playerEvent.wuLongQiu += 1;
                }
                break;
            case 15:
                if (menuPosition == 0) {
                    //换人
                } else {
                    //提交数据
                    new AlertDialog.Builder(this)
                            .setMessage("确定提交本节数据")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doCommitEventData();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                }
                break;
        }

        if (eventCode != -1) {
            createEventRecord(eventCode, playerEvent);
        }

        if (position != 15) {
            playerEvent.save();
            mRecordAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 每一次事件都是一条记录
     * @param eventCode
     * @param playerEvent
     */
    private void createEventRecord(final int eventCode, final PlayerEvent playerEvent) {
        Observable.create(new Observable.OnSubscribe<EventItem>() {
            @Override
            public void call(Subscriber<? super EventItem> subscriber) {
                EventItem eventItem = new EventItem();
                eventItem.eventCode = eventCode;
                eventItem.matchId = playerEvent.matchId;
                eventItem.teamId = playerEvent.teamId;
                eventItem.playerId = playerEvent.playerId;
                eventItem.playerNumber = playerEvent.playerNumber;
                eventItem.partNumber = playerEvent.partNumber;
                eventItem.timeSecond = System.currentTimeMillis() - mMatchStartTime;
                eventItem.uuid = UUID.randomUUID().toString();
                eventItem.save();

                subscriber.onNext(eventItem);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<EventItem>() {
                    @Override
                    public void call(EventItem eventItem) {
                        Log.d();
                        //生成一个事件就在界面下方显示出来
                        if (mRecentAdapter != null) {
                            mRecentAdapter.addItem(eventItem);
                            mRecentRecyclerView.scrollToPosition(mRecentAdapter.getItemCount() - 1);
                        }
                    }
                });
    }

    private void doCommitEventData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在提交数据...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        final EventCollectionRequest requeset = new EventCollectionRequest(getApplicationContext());

        Observable<EventCollectionRequest.HttpEvent> requestObservable =
                Observable.create(new Observable.OnSubscribe<EventCollectionRequest.HttpEvent>() {
                    @Override
                    public void call(Subscriber<? super EventCollectionRequest.HttpEvent> subscriber) {
                        //查询本场比赛，本支队伍，本节的数据事件，然后提交
                        List<EventItem> events = new Select().from(EventItem.class).where("matchId=? and teamId=?" +
                                " and partNumber=?", mMatchId, mTeamId, mPartNumber).execute();
                        Log.d("total evet size:" + events.size());

                        EventCollectionRequest.HttpEvent httpEvent;
                        for (EventItem eventItem : events) {
                            httpEvent = new EventCollectionRequest.HttpEvent();
                            httpEvent.eventCode = eventItem.eventCode;
                            httpEvent.matchId = eventItem.matchId;
                            httpEvent.teamId = eventItem.teamId;
                            httpEvent.playerId = eventItem.playerId;
                            httpEvent.partNumber = eventItem.partNumber;
                            httpEvent.timeSecond = eventItem.timeSecond;
                            httpEvent.uuid = eventItem.uuid;
                            httpEvent.additionalData = null;
                            subscriber.onNext(httpEvent);
                        }

                        //生成一个小节结束的事件
                        httpEvent = new EventCollectionRequest.HttpEvent();
                        httpEvent.eventCode = EventCode.EVENT_PART_OVER;
                        httpEvent.matchId = mMatchId;
                        httpEvent.teamId = mTeamId;
                        httpEvent.playerId = -1;
                        httpEvent.partNumber = mPartNumber;
                        httpEvent.timeSecond = System.currentTimeMillis() - mMatchStartTime;
                        httpEvent.uuid = UUID.randomUUID().toString();
                        httpEvent.additionalData = null;
                        subscriber.onNext(httpEvent);

                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io());

        requestObservable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<EventCollectionRequest.HttpEvent, EventCollectionRequest>() {
                    @Override
                    public EventCollectionRequest call(EventCollectionRequest.HttpEvent httpEvent) {
                        Log.d("httpEvent:" + httpEvent.eventCode);
                        Log.d("111  isMainLoop:" + (Looper.myLooper() == Looper.getMainLooper()));
                        requeset.events.add(httpEvent);
                        return requeset;
                    }
                })
                .flatMap(new Func1<EventCollectionRequest, Observable<BaseResponse>>() {
                    @Override
                    public Observable<BaseResponse> call(EventCollectionRequest request) {
                        Log.d("2222  isMainLoop:" + (Looper.myLooper() == Looper.getMainLooper()));
                        return mLadderBallApi.doCommitEventData(request);
                    }
                })
                .map(new Func1<BaseResponse, Boolean>() {
                    @Override
                    public Boolean call(BaseResponse baseResponse) {
                        if (baseResponse.header.resultCode == 0) {
                            //上传成功，删除本地记录
                            List<EventItem> events = new Select().from(EventItem.class).where("matchId=? and teamId=?" +
                                    " and partNumber=?", mMatchId, mTeamId, mPartNumber).execute();
                            for (EventItem eventItem : events) {
                                eventItem.delete();
                            }

                            return true;
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        progressDialog.cancel();
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(e.toString());
                        progressDialog.cancel();
                        ToastUtil.showToast(getApplicationContext(), "网络连接错误，请重试");
                    }

                    @Override
                    public void onNext(Boolean result) {
                        Log.d("result:" + result);
                        progressDialog.cancel();
                        if (result) {
                            ToastUtil.showToast(getApplicationContext(), "提交成功");
                            DataRecoderActivity.this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "请重试");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(RxBusTag.EVENT_RECORD_ITEM, mRecordObserverable);
    }
}
