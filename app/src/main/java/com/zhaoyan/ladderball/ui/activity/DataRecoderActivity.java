package com.zhaoyan.ladderball.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
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
import com.zhaoyan.ladderball.ui.adapter.RecordPlayerNumberAdapter;
import com.zhaoyan.ladderball.ui.dialog.BaseDialog;
import com.zhaoyan.ladderball.ui.dialog.ReplaceDialog;
import com.zhaoyan.ladderball.util.DensityUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Bind(R.id.recent_event_recyclerview)
    RecyclerView mRecentRecyclerView;
    @Bind(R.id.event_record_recyclerview)
    RecyclerView mRecordRecyclerView;

    @Bind(R.id.record_player_recyclerview)
    RecyclerView mPlayerNumberRecyclerView;

    private EventRecentAdapter mRecentAdapter;
    private EventRecordAdapter mRecordAdapter;

    private RecordPlayerNumberAdapter mPlayerNumberAdapter;

    private long mMatchId;
    private long mTeamId;
    private int mPartNumber;

    private List<Player> mOnPitchPlayerList;//场上球员
    private List<Player> mUnOnPitchPlayerList;//场下球员

    private ProgressDialog mProgressDialog;

//    private List<PlayerEvent> mOnPitchPlayerEventList;//改用hashmap

    private HashMap<Long, PlayerEvent> mPlayerEventMap;

    private Observable<RxBusTag.DataRecord> mDataObserverable;

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

        GridLayoutManager playerNumberLayoutManager = new GridLayoutManager(this, 2);
        mPlayerNumberRecyclerView.setLayoutManager(playerNumberLayoutManager);

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
        mUnOnPitchPlayerList = new ArrayList<>();
        Log.d("=============");
        for (Player player : allPlayerList) {
//            Log.d(player.toString());
            Log.d("isFirst:" + player.isFirst + ",isOnPitch:" + player.isOnPitch);
            if (player.isOnPitch) {
                mOnPitchPlayerList.add(player);
            } else {
                mUnOnPitchPlayerList.add(player);
            }
        }
        Log.d("=============");
        int width = getRecyclerViewWidth(getApplicationContext(), mOnPitchPlayerList.size());
        Log.d("width:" + width);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        mPlayerNumberRecyclerView.setLayoutParams(params);

        mPlayerNumberAdapter = new RecordPlayerNumberAdapter(getApplicationContext(), mOnPitchPlayerList);
        mPlayerNumberRecyclerView.setAdapter(mPlayerNumberAdapter);

//        mOnPitchPlayerEventList = new ArrayList<>();
        initRecentList();
        initEvents(allPlayerList);

        mDataObserverable = RxBus.get().register(RxBusTag.DATA_RECORD_ACTIVITY, RxBusTag.DataRecord.class);
        mDataObserverable.subscribe(new Action1<RxBusTag.DataRecord>() {
            @Override
            public void call(RxBusTag.DataRecord dataRecord) {
                int type = dataRecord.itemType;
                switch (type) {
                    case RxBusTag.DataRecord.ITEM_EVENT_RECORD_CLICK:
                        onEventItemClick(dataRecord.position);
                        break;
                    case RxBusTag.DataRecord.ITEM_EVENT_RECENT_REMOVE:
                        onRecentItemRemove(dataRecord.position);
                        break;
                    case RxBusTag.DataRecord.ITEM_PLAYER_CLICK:
                        onPlayerItemClick(dataRecord.position);
                        break;
                }
            }
        });
    }

    /**
     * 初始化事件历史纪录，这种情况一般会在程序异常崩溃等一节比赛还未提交的时候，再次进来恢复上一次的数据记录
     */
    private void initRecentList() {
        Observable.create(new Observable.OnSubscribe<List<EventItem>>() {
            @Override
            public void call(Subscriber<? super List<EventItem>> subscriber) {
                //读取当前比赛第一个球员
                List<EventItem> eventItemList = new Select().from(EventItem.class)
                        .where("matchId=? and teamId=?", mMatchId, mTeamId).execute();
                subscriber.onNext(eventItemList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<EventItem>>() {
                    @Override
                    public void call(List<EventItem> result) {
                        if (result != null && result.size() > 0 && mRecentAdapter != null) {
                            mRecentAdapter.setDataList(result);
                            mRecentAdapter.notifyDataSetChanged();
                        }
                        Log.d("init recent list over");
                    }
                });
    }

    private void initEvents(final List<Player> allPlayerList) {
        mPlayerEventMap = new HashMap<>();
        Observable<PlayerEvent> observable = Observable.create(new Observable.OnSubscribe<PlayerEvent>() {
            @Override
            public void call(Subscriber<? super PlayerEvent> subscriber) {
                //初始化场上球员的数据
                PlayerEvent playerEvent = null;
                PlayerEvent firsPlayerEvent = null;
                for (Player player : allPlayerList) {
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

//                    mOnPitchPlayerEventList.add(playerEvent);

                    if (firsPlayerEvent == null) {
                        firsPlayerEvent = playerEvent;
                    }

                    mPlayerEventMap.put(player.playerId, playerEvent);
                }
                subscriber.onNext(firsPlayerEvent);
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
                .subscribe(new Observer<PlayerEvent>() {
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
                    public void onNext(PlayerEvent firstPlayerEvent) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }
//                        ToastUtil.showToast(getApplicationContext(), s);
                        Log.d("player.size:" + mPlayerEventMap.size());
                        //默认加载第一个球员的Event
//                        PlayerEvent playerEvent = mOnPitchPlayerEventList.get(0);
                        mRecordAdapter.setDataList(firstPlayerEvent);
                        mRecordAdapter.notifyDataSetChanged();

                        new AlertDialog.Builder(DataRecoderActivity.this)
                                .setTitle("第" + mPartNumber + "节比赛")
                                .setMessage("比赛开始？")
                                .setPositiveButton("比赛开始", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mMatchStartTime = System.currentTimeMillis();
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

    private void onEventItemClick(final int position) {
        Log.d("position:" + position);
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 12:
            case 13:
            case 14:
            case 15:
                String[] menus = getItemMenu(position);
                new AlertDialog.Builder(DataRecoderActivity.this)
                        .setItems(menus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlayerEvent playerEvent = mRecordAdapter.getItem();
                                handleEvent(position, which, playerEvent);
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
                handleEvent(position, -1, playerEvent);
                break;
        }
    }

    /**
     * 从历史记录中移除一个事件
     * @param position
     */
    private void onRecentItemRemove(int position) {
        EventItem eventItem = mRecentAdapter.getItem(position);
        //首先从EventItem数据表中删除这条数据
        eventItem.delete();

        //然后总表也要删除数据
        PlayerEvent playerEvent = mPlayerEventMap.get(eventItem.playerId);
        Log.d("playerevent:" + playerEvent.toString());
        playerEvent.rollback(eventItem.eventCode);

        if (mPlayerNumberAdapter != null &&
                mPlayerNumberAdapter.getSelectItemId() == eventItem.playerId) {
            if (mRecordAdapter != null) {
                mRecordAdapter.setDataList(playerEvent);
                mRecordAdapter.notifyDataSetChanged();
            }
        }

        if (mRecentAdapter != null) {
            mRecentAdapter.removeItem(position);
        }
    }

    private void onPlayerItemClick(int position) {
        Player player = mOnPitchPlayerList.get(position);
        PlayerEvent playerEvent = mPlayerEventMap.get(player.playerId);
        Log.d("matchId:" + playerEvent.matchId + ",teamId:" + playerEvent.teamId + ",playerId:" + playerEvent.playerId);
        mRecordAdapter.setDataList(playerEvent);
        mRecordAdapter.notifyDataSetChanged();
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
                    repickPlayer(position, playerEvent);
                } else {
                    //提交数据
                    gameOver();
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
     *
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

    private void createHuanRenEventRecord(PlayerEvent downEvent, PlayerEvent upEvent) {
        EventItem eventItem = new EventItem();
        eventItem.eventCode = EventCode.EVENT_HUAN_REN;
        eventItem.matchId = downEvent.matchId;
        eventItem.teamId = downEvent.teamId;
        eventItem.playerId = downEvent.playerId;
        eventItem.playerNumber = downEvent.playerNumber;
        eventItem.partNumber = downEvent.partNumber;
        eventItem.timeSecond = System.currentTimeMillis() - mMatchStartTime;
        eventItem.uuid = UUID.randomUUID().toString();

        //换人的event需要在additionalData添加被换上场球员的基本信息
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", upEvent.playerId);
            jsonObject.put("playerNumber", upEvent.playerNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventItem.additionalData = jsonObject.toString();
        Log.d("additionalData:" + eventItem.additionalData);

        eventItem.save();

        if (mRecentAdapter != null) {
            mRecentAdapter.addItem(eventItem);
            mRecentRecyclerView.scrollToPosition(mRecentAdapter.getItemCount() - 1);
        }
    }

    private void doCommitEventData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在提交数据...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

//        final EventCollectionRequest requeset = new EventCollectionRequest(getApplicationContext());

        Observable<EventCollectionRequest> requestObservable =
                Observable.create(new Observable.OnSubscribe<EventCollectionRequest>() {
                    @Override
                    public void call(Subscriber<? super EventCollectionRequest> subscriber) {
                        //查询本场比赛，本支队伍，本节的数据事件，然后提交
                        List<EventItem> events = new Select().from(EventItem.class).where("matchId=? and teamId=?" +
                                " and partNumber=?", mMatchId, mTeamId, mPartNumber).execute();
                        Log.d("total evet size:" + events.size());

                        EventCollectionRequest requeset = new EventCollectionRequest(getApplicationContext());

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
                            httpEvent.additionalData = eventItem.additionalData;
//                            subscriber.onNext(httpEvent);
                            requeset.events.add(httpEvent);
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
                        requeset.events.add(httpEvent);

                        subscriber.onNext(requeset);
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
//                .map(new Func1<EventCollectionRequest.HttpEvent, EventCollectionRequest>() {
//                    @Override
//                    public EventCollectionRequest call(EventCollectionRequest.HttpEvent httpEvent) {
//                        Log.d("httpEvent:" + httpEvent.eventCode);
//                        requeset.events.add(httpEvent);
//                        return requeset;
//                    }
//                })
                .flatMap(new Func1<EventCollectionRequest, Observable<BaseResponse>>() {
                    @Override
                    public Observable<BaseResponse> call(EventCollectionRequest request) {
                        Log.d("start commit event data");
                        return mLadderBallApi.doCommitEventData(request);
                    }
                })
                .map(new Func1<BaseResponse, Boolean>() {
                    @Override
                    public Boolean call(BaseResponse baseResponse) {
                        Log.d("commit data over:" + baseResponse.header.resultCode);
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
                            setResult(RESULT_OK);
                            ToastUtil.showToast(getApplicationContext(), "提交成功");
                            DataRecoderActivity.this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "请重试");
                        }
                    }
                });
    }

    /**
     * 换人
     *
     * @param position
     * @param downPlayerEvent 被换下场的球员playerEvent
     */
    private void repickPlayer(final int position, final PlayerEvent downPlayerEvent) {
        ReplaceDialog replaceDialog = new ReplaceDialog(this, mUnOnPitchPlayerList);
        replaceDialog.setOnAddNewClickListener(new ReplaceDialog.OnAddNewPlayerClickListener() {
            @Override
            public void onAddNew(Dialog dialog) {
                Log.d();

            }
        });
        replaceDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                //这个是将要换上场的球员
                Player upPlayer = ((ReplaceDialog) dialog).getSelectPlayer();
                if (upPlayer == null) {
                    ToastUtil.showToast(getApplicationContext(), "请至少选择一个球员");
                } else {
                    //这个是将要下场的球员
                    int currentPlayerPosition = mPlayerNumberAdapter.getSelectPosition();
                    Player downPlayer = mOnPitchPlayerList.get(currentPlayerPosition);
                    Log.d("upPlayer:" + upPlayer.number + ",downPlayer:" + downPlayer.number);
                    downPlayer.isOnPitch = false;
                    downPlayer.save();//保存换人状态，这样其他节比赛能够保持换人后的球员列表

                    upPlayer.isOnPitch = true;
                    upPlayer.save();

//                    Log.d("============换人之前场上球员列表=========");
//                    for (int i = 0; i < mOnPitchPlayerList.size(); i++) {
//                        Log.d(">>" + i + ":" + mOnPitchPlayerList.get(i).number);
//                    }
//
//                    Log.d("============换人之前场下球员列表=========");
//                    for (int i = 0; i < mUnOnPitchPlayerList.size(); i++) {
//                        Log.d(">>" + i + ":" + mUnOnPitchPlayerList.get(i).number);
//                    }
                    //将换下的人员从界面上拿走
                    mPlayerNumberAdapter.changeItem(currentPlayerPosition, upPlayer);

                    mUnOnPitchPlayerList.remove(upPlayer);
                    mUnOnPitchPlayerList.add(downPlayer);

//                    Log.d("============换人之后场上球员列表=========");
//                    for (int i = 0; i < mOnPitchPlayerList.size(); i++) {
//                        Log.d(">>" + i + ":" + mOnPitchPlayerList.get(i).number);
//                    }
//
//                    Log.d("============换人之后场下球员列表=========");
//                    for (int i = 0; i < mUnOnPitchPlayerList.size(); i++) {
//                        Log.d(">>" + i + ":" + mUnOnPitchPlayerList.get(i).number);
//                    }

                    PlayerEvent upPlayerEvent = mPlayerEventMap.get(upPlayer.playerId);
                    mRecordAdapter.setDataList(upPlayerEvent);
                    mRecordAdapter.notifyDataSetChanged();

//                    createEventRecord(EventCode.EVENT_HUAN_REN, downPlayerEvent);//添加一条换人记录
                    createHuanRenEventRecord(downPlayerEvent, upPlayerEvent);//添加一条换人记录
                    //将被换上场的球员显示在界面上

                    dialog.dismiss();
                }
            }
        });
        replaceDialog.setNegativeButton("取消", null);
        replaceDialog.show();
    }

//    private void showAddNewDialog()

//    private void doAdd(String playerNumber, String name) {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("正在新增球员...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setCancelable(false);
//
//        AddPlayerRequest request = new AddPlayerRequest(getApplicationContext());
//        request.matchId = mMatchId;
//        request.teamId = mTeamId;
//
//        final AddPlayerRequest.HttpPlayer httpPlayer = new AddPlayerRequest.HttpPlayer();
//        httpPlayer.isFirst = false;//默认不首发
//        httpPlayer.name = name;
//        httpPlayer.number = Integer.valueOf(playerNumber);
//        request.player = httpPlayer;
//
//        mLadderBallApi.doAddPlayer(request)
//                .map(new Func1<AddPlayerResponse, ResponseHeader>() {
//                    @Override
//                    public ResponseHeader call(AddPlayerResponse response) {
//                        if (response.header.resultCode == 0) {
//                            Player player = new Player();
//                            player.matchId = mMatchId;
//                            player.teamId = mTeamId;
//                            player.playerId = response.player.id;
//                            player.number = response.player.number;
//                            player.name = response.player.name;
//                            player.isFirst = response.player.isFirst;
//
//                            if (mMatch.teamHome.isAssiged) {
//                                mMatch.teamHome.players.add(player);
//                            } else {
//                                mMatch.teamVisitor.players.add(player);
//                            }
//                            mMatch.save();
//                        }
//                        return response.header;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        progressDialog.show();
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseHeader>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Log.e(e.toString());
//                        progressDialog.cancel();
//                        ToastUtil.showToast(getApplicationContext(), "网络连接超时，请重试");
//                    }
//
//                    @Override
//                    public void onNext(ResponseHeader responseHeader) {
//                        progressDialog.cancel();
//                        if (responseHeader.resultCode == 0) {
//                            ToastUtil.showToast(getApplicationContext(), "添加球员成功");
//                            //添加成功，重新加载界面
//                            initData(true);
//                        } else {
//                            ToastUtil.showToast(getApplicationContext(), responseHeader.resultText);
//                        }
//                    }
//                });
//    }

    private void gameOver() {
        new AlertDialog.Builder(this)
                .setMessage("本节比赛已结束，开始提交数据？")
                .setPositiveButton("提交数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        doCommitEventData();
                    }
                })
                //test
                .setNeutralButton("强行退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataRecoderActivity.this.finish();
                    }
                })
                //test
                .setNegativeButton("取消", null)
                .create().show();
    }

    public int getRecyclerViewWidth(Context context, int itemCount) {
        float height = DensityUtil.getHeightInPx(context);
        Log.d("Screenheight:" + height);

        int statusBarHeight = DensityUtil.getStatusBarHeight(context.getResources());
        Log.d("statusBarHeight:" + statusBarHeight);

        int column;
        if (itemCount <= 8) {
            column = 4;
        } else {
            double size = itemCount / 2.0;
            column = (int) Math.ceil(size);
        }



        Log.d("column:" + column);
        int itemHeight = (int) ((height - statusBarHeight) / column);
        int px = DensityUtil.dip2px(context, 1);
//        itemHeight = itemHeight - column * 2 * 1;
        Log.d("itemHeight:" + itemHeight + ",column:" + column + ",px:" + px + ",msize:" + mOnPitchPlayerList.size());
        return itemHeight * 2;
    }

    @Override
    public void onBackPressed() {
        gameOver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(RxBusTag.DATA_RECORD_ACTIVITY, mDataObserverable);
    }
}
