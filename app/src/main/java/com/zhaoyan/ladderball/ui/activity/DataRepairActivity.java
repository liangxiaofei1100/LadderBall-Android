package com.zhaoyan.ladderball.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.http.request.EventCollectionRequest;
import com.zhaoyan.ladderball.http.request.EventDeleteRequest;
import com.zhaoyan.ladderball.http.request.EventModifyRequest;
import com.zhaoyan.ladderball.http.request.EventPartListRequest;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.http.response.EventPartListResponse;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.DataRepairAdapter;
import com.zhaoyan.ladderball.ui.adapter.OnItemClickListener;
import com.zhaoyan.ladderball.ui.dialog.BaseDialog;
import com.zhaoyan.ladderball.ui.dialog.DataRepairDialog;
import com.zhaoyan.ladderball.ui.dialog.MenuDialog;
import com.zhaoyan.ladderball.ui.view.ItemDivider;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataRepairActivity extends BaseActivity implements OnItemClickListener {
    private static final String EXTRA_MATCH_ID = "matchId";
    private static final String EXTRA_TEAM_ID = "teamId";
    private static final String EXTRA_PART_NUMBER = "part_number";
    private static final String EXTRA_IS_COMPLETE = "is_complete";

    private long mMatchId;
    private long mTeamId;
    private int mPartNumber;
    private boolean mIsComplete;

    @Bind(R.id.data_repair_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_data_repair_retry)
    Button mRetryButton;
    @Bind(R.id.tv_data_repair_empty)
    TextView mEmptyView;

    DataRepairAdapter mAdapter;

    private List<String> mMenuList = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    public static Intent getStartIntent(Context context, long matchId, long teamId, int partNumber, boolean isComplete) {
        Intent intent=  new Intent();
        intent.setClass(context, DataRepairActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_PART_NUMBER, partNumber);
        intent.putExtra(EXTRA_IS_COMPLETE, isComplete);
        return  intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_repair);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mMatchId = intent.getLongExtra(EXTRA_MATCH_ID, -1);
        mTeamId = intent.getLongExtra(EXTRA_TEAM_ID, -1);
        mPartNumber = intent.getIntExtra(EXTRA_PART_NUMBER, -1);
        mIsComplete = intent.getBooleanExtra(EXTRA_IS_COMPLETE, false);

        Log.d("isComplete:" + mIsComplete);
        if (mIsComplete) {
            toolbar.setTitle("数据记录");
            setTitle("数据记录");
        } else {
            toolbar.setTitle("数据修复");
            setTitle("数据修复");
        }
        toolbar.setSubtitle("第" + mPartNumber + "节");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDivider(getApplicationContext()));

        mAdapter = new DataRepairAdapter(getApplicationContext(), new ArrayList<EventPartListResponse.HttpEvent>());
        mRecyclerView.setAdapter(mAdapter);

        if (!mIsComplete) {
            mAdapter.setOnItemClickListener(this);
        }

        mMenuList.add("编辑");
        mMenuList.add("删除");
        mMenuList.add("取消");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        doGetEventList();
    }

    private void doGetEventList() {
        EventPartListRequest request = new EventPartListRequest(getApplicationContext());
        request.matchId = mMatchId;
        request.teamId = mTeamId;
        request.partNumber = mPartNumber;

        Observable<EventPartListResponse> responseObservable
                = mLadderBallApi.doGetEvetPartList(request).subscribeOn(Schedulers.io());

        responseObservable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.setMessage("获取数据中...");
                        mProgressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<EventPartListResponse, List<EventPartListResponse.HttpEvent>>() {
                    @Override
                    public List<EventPartListResponse.HttpEvent> call(EventPartListResponse eventPartListResponse) {
                        List<EventPartListResponse.HttpEvent> events = new ArrayList<>();
                        if (eventPartListResponse.header.resultCode == 0) {
                            if (eventPartListResponse.events == null) {
                                return events;
                            }
                            EventPartListResponse.HttpEvent httpEvent;
                            for (int i = 0; i < eventPartListResponse.events.size(); i++) {
                                httpEvent = eventPartListResponse.events.get(i);
                                if (httpEvent.eventCode != EventCode.EVENT_PART_OVER) {
                                    events.add(httpEvent);
                                }
                            }
                            return events;
                        }
                        mFaiLResultString = eventPartListResponse.header.resultText;
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<EventPartListResponse.HttpEvent>>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        disMissProgressDialog();

                        e.printStackTrace();
                        Log.e(e.toString());

                        mRetryButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<EventPartListResponse.HttpEvent> result) {
                        disMissProgressDialog();

                        if (result != null) {
                            mRetryButton.setVisibility(View.GONE);
                            mAdapter.setDataList(result);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            ToastUtil.showToast(getApplicationContext(), mFaiLResultString);
                            mRetryButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private String mFaiLResultString;

    @OnClick(R.id.btn_data_repair_retry)
    public void doRetry() {
        doGetEventList();
    }

    private void disMissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void cancelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(0).setVisible(!mIsComplete);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"添加新记录")
                .setIcon(R.mipmap.ic_add_32)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            DataRepairActivity.this.finish();
        } else if (itemId == 0) {
            //add new record
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final int position) {
        if (mIsComplete) {
            return;
        }
        new MenuDialog(this, mMenuList)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int menuPosition) {
                        if (menuPosition == 0) {
                            //edit
                            showEditDialog(position);
                        } else if (menuPosition == 1) {
                            //delete
                            showDeleteDialog(position);
                        } else {
                            //cancel
                        }
                    }
                })
                .show();
    }

    private void showAddDialog() {
        List<Player> playerList = new Select().from(Player.class).where("matchId=? and teamId=?",
                mMatchId, mTeamId).execute();

        if (playerList == null) {
            playerList = new ArrayList<>();
        }

        final DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_ADD, playerList);
        repairDialog.setDialogTitle("添加数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {

                Log.d(repairDialog.getSelectedItem());
                int eventCode = repairDialog.getSelectedEventCode();
                long time = repairDialog.getTime();

                if (time == 0) {
                    ToastUtil.showToast(getApplicationContext(), "请输入时间");
                    return;
                }

                long playerId1 = repairDialog.getPlayerId1();
                int playerNumber1 = repairDialog.getPlayerNumber1();

                long playerId2 = -1;
                int playerNumber2 = -1;
                if (eventCode == EventCode.EVENT_HUAN_REN) {
                    playerId2 = repairDialog.getPlayerId2();
                    playerNumber2 = repairDialog.getPlayerNumber2();
                }

                Log.d("eventCode:" + eventCode + ",time:" + time + ",playerId1:" + playerId1
                        + "playerId2:" + playerId2 + ",playerNumber2:" + playerNumber2);

                EventPartListResponse.HttpEvent httpEvent = new EventPartListResponse.HttpEvent();
                httpEvent.matchId = mMatchId;
                httpEvent.teamId = mTeamId;
                httpEvent.playerId = playerId1;
                httpEvent.playerNumber = playerNumber1;
                httpEvent.partNumber = mPartNumber;
                httpEvent.timeSecond = time;
                if (eventCode == EventCode.EVENT_HUAN_REN) {
                    //换人的event需要在additionalData添加被换上场球员的基本信息
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", playerId2);
                        jsonObject.put("playerNumber", playerNumber2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    httpEvent.additionalData = jsonObject.toString();
                }
                httpEvent.uuid = UUID.randomUUID().toString();
                httpEvent.eventCode = eventCode;

                doAddItem(httpEvent);

                dialog.dismiss();
            }
        });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doAddItem(final EventPartListResponse.HttpEvent httpEvent) {
        EventCollectionRequest.HttpEvent httpEvent1 = new EventCollectionRequest.HttpEvent();
        httpEvent1.matchId = httpEvent.matchId;
        httpEvent1.teamId = httpEvent.teamId;
        httpEvent1.playerId = httpEvent.playerId;
        httpEvent1.partNumber = httpEvent.partNumber;
        httpEvent1.timeSecond = httpEvent.timeSecond;
        httpEvent1.additionalData = httpEvent.additionalData;
        httpEvent1.uuid = httpEvent.uuid;
        httpEvent1.eventCode = httpEvent.eventCode;

        final EventCollectionRequest request = new EventCollectionRequest(getApplicationContext());
        request.events.add(httpEvent1);

        mLadderBallApi.doCommitEventData(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.setMessage("添加记录中...");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        disMissProgressDialog();
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showNetworkFailToast(getApplicationContext());
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        disMissProgressDialog();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "添加记录成功");

                            //添加完毕后，必须重新刷一次，否则新添加的记录没有id，如果再对这个数据进行删除或修改就会报错
                            doGetEventList();
//                            mAdapter.addItem(httpEvent);
//                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });

    }

    private void showEditDialog(final int position) {
        List<Player> playerList = new Select().from(Player.class).where("matchId=? and teamId=?",
                mMatchId, mTeamId).execute();

        if (playerList == null) {
            playerList = new ArrayList<>();
        }

        EventPartListResponse.HttpEvent httpEvent = mAdapter.getItem(position);
        final DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_EDIT, playerList);
        repairDialog.setEvent(httpEvent);
        repairDialog.setDialogTitle("编辑数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Log.d(repairDialog.getSelectedItem());
                        int eventCode = repairDialog.getSelectedEventCode();
                        long time = repairDialog.getTime();

                        if (time == 0) {
                            ToastUtil.showToast(getApplicationContext(), "请输入时间");
                            return;
                        }

                        long playerId1 = repairDialog.getPlayerId1();
                        int playerNumber1 = repairDialog.getPlayerNumber1();

                        long playerId2 = -1;
                        int playerNumber2 = -1;
                        if (eventCode == EventCode.EVENT_HUAN_REN) {
                            playerId2 = repairDialog.getPlayerId2();
                            playerNumber2 = repairDialog.getPlayerNumber2();
                        }

                        Log.d("eventCode:" + eventCode + ",time:" + time + ",playerId1:" + playerId1
                        + "playerId2:" + playerId2 + ",playerNumber2:" + playerNumber2);
                        doModify(position, eventCode, time, playerId1, playerNumber1, playerId2, playerNumber2);

                        dialog.dismiss();
                    }
                });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doModify(final int position, int eventCode, long time, long playerId1, int playerNumber1, long playerId2, int playerNumber2) {
        EventPartListResponse.HttpEvent httpEvent = mAdapter.getItem(position);

        final EventModifyRequest request = new EventModifyRequest(getApplicationContext());
        request.id = httpEvent.id;
        request.matchId = httpEvent.matchId;
        request.teamId = httpEvent.teamId;
        request.playerId = playerId1;
        httpEvent.playerId = playerId1;
        httpEvent.playerNumber = playerNumber1;
        if (eventCode == -1) {
            request.eventCode = httpEvent.eventCode;
        } else {
            httpEvent.eventCode = eventCode;
            request.eventCode = eventCode;
        }
        request.partNumber = httpEvent.partNumber;
        request.timeSecond = time;
        httpEvent.timeSecond = time;
        request.uuid = UUID.randomUUID().toString();

        if (eventCode == EventCode.EVENT_HUAN_REN) {
            //换人的event需要在additionalData添加被换上场球员的基本信息
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", playerId2);
                jsonObject.put("playerNumber", playerNumber2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request.additionalData = jsonObject.toString();
            httpEvent.additionalData = jsonObject.toString();
        }

        mLadderBallApi.doModifyEvent(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d("modify event id:" + request.id);
                        mProgressDialog.setMessage("修改数据中...");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        disMissProgressDialog();
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showNetworkFailToast(getApplicationContext());
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        disMissProgressDialog();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "修改数据成功");
                            mAdapter.changeItem(position);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });
    }

    private void showDeleteDialog(final int position) {
        BaseDialog deleteDialog = new BaseDialog(this);
        deleteDialog.setDialogMessage("确定删除这条数据?");
        deleteDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                doDelete(position);
                dialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("取消", null);
        deleteDialog.show();
    }

    private void doDelete(final int position) {
        EventPartListResponse.HttpEvent httpEvent = mAdapter.getItem(position);

        final EventDeleteRequest request = new EventDeleteRequest(getApplicationContext());
        request.eventId = httpEvent.id;

        mLadderBallApi.doDeleteEvent(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d("delete event id:" + request.eventId);
                        mProgressDialog.setMessage("删除数据中...");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        disMissProgressDialog();
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showNetworkFailToast(getApplicationContext());
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        disMissProgressDialog();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "删除数据成功");
                            mAdapter.removeItem(position);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelProgressDialog();
    }
}
