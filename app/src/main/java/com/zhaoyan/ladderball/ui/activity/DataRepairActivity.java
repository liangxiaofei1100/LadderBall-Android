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

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.http.request.EventPartListRequest;
import com.zhaoyan.ladderball.http.response.EventPartListResponse;
import com.zhaoyan.ladderball.ui.adapter.DataRepairAdapter;
import com.zhaoyan.ladderball.ui.adapter.OnItemClickListener;
import com.zhaoyan.ladderball.ui.dialog.BaseDialog;
import com.zhaoyan.ladderball.ui.dialog.DataRepairDialog;
import com.zhaoyan.ladderball.ui.dialog.MenuDialog;
import com.zhaoyan.ladderball.ui.view.ItemDivider;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

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

    private long mMatchId;
    private long mTeamId;
    private int mPartNumber;

    @Bind(R.id.data_repair_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_data_repair_retry)
    Button mRetryButton;
    @Bind(R.id.tv_data_repair_empty)
    TextView mEmptyView;

    DataRepairAdapter mAdapter;

    private List<String> mMenuList = new ArrayList<>();

    public static Intent getStartIntent(Context context, long matchId, long teamId, int partNumber) {
        Intent intent=  new Intent();
        intent.setClass(context, DataRepairActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_PART_NUMBER, partNumber);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDivider(getApplicationContext()));

        mAdapter = new DataRepairAdapter(getApplicationContext(), new ArrayList<EventPartListResponse.HttpEvent>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mMenuList.add("编辑");
        mMenuList.add("删除");
        mMenuList.add("取消");

        doGetEventList();
    }

    private void doGetEventList() {
        EventPartListRequest request = new EventPartListRequest(getApplicationContext());
        request.matchId = mMatchId;
        request.teamId = mTeamId;
        request.partNumber = mPartNumber;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("获取数据中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        Observable<EventPartListResponse> responseObservable
                = mLadderBallApi.doGetEvetPartList(request).subscribeOn(Schedulers.io());

        responseObservable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
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
                        progressDialog.cancel();

                        e.printStackTrace();
                        Log.e(e.toString());

                        mRetryButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<EventPartListResponse.HttpEvent> result) {
                        progressDialog.cancel();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"+")
                .setIcon(R.mipmap.ic_add_white)
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
            doAddItem();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final int position) {
        new MenuDialog(this, mMenuList)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int menuPosition) {
                        if (menuPosition == 0) {
                            //edit
                            doEditItem(position);
                        } else if (menuPosition == 1) {
                            //delete
                            doDeleteItem(position);
                        } else {
                            //cancel
                        }
                    }
                })
                .show();
    }

    private void doAddItem() {
        DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_ADD);
        repairDialog.setDialogTitle("添加数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {

            }
        });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doEditItem(int position) {
        EventPartListResponse.HttpEvent httpEvent = mAdapter.getItem(position);
        DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_EDIT);
        repairDialog.setEvent(httpEvent);
        repairDialog.setDialogTitle("编辑数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {

                    }
                });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doDeleteItem(int position) {
        BaseDialog deleteDialog = new BaseDialog(this);
        deleteDialog.setDialogMessage("确定删除这条数据?");
        deleteDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {

            }
        });
        deleteDialog.setNegativeButton("取消", null);
        deleteDialog.show();
    }
}