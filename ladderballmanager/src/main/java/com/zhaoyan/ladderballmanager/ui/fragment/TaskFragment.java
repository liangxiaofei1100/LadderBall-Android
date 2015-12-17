package com.zhaoyan.ladderballmanager.ui.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.zhaoyan.ladderballmanager.R;
import com.zhaoyan.ladderballmanager.http.request.AsignTaskRequest;
import com.zhaoyan.ladderballmanager.http.request.BaseRequest;
import com.zhaoyan.ladderballmanager.http.response.BaseResponse;
import com.zhaoyan.ladderballmanager.http.response.TaskListResponse;
import com.zhaoyan.ladderballmanager.ui.adapter.TaskAdapter;
import com.zhaoyan.ladderballmanager.util.Log;
import com.zhaoyan.ladderballmanager.util.ToastUtil;
import com.zhaoyan.ladderballmanager.util.rx.RxBus;
import com.zhaoyan.ladderballmanager.util.rx.RxBusTag;

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

/**
 * 任务tab页，显示当前用户（记录员）已领取的任务列表（比赛列表）
 */
public class TaskFragment extends BaseFragment {

    @Bind(R.id.task_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.task_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_task_empty)
    TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;
    private TaskAdapter mAdapter;

    public static final int REGET_DATA = -2;
    private Observable<Integer> mItemObservable;

    private List<TaskListResponse.HttpMatch> mMatchList = new ArrayList<>();


    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mItemObservable = RxBus.get().register(RxBusTag.TASK_FRAGMENT, Integer.class);
        mItemObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        if (position == REGET_DATA) {
                            Log.d("refresh data");
                            doGetTasks();
                            return;
                        }

                        handleItemClick(position);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskAdapter(getActivity(), new ArrayList<TaskListResponse.HttpMatch>());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetTasks();
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        doGetTasks();
    }

    /**
     * do get task from server
     */
    private void doGetTasks() {
        Observable<TaskListResponse> responseObservable = mLadderBallApi.doGetTaskList(new BaseRequest(getActivity()));
        responseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskListResponse>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d();
                        mSwipeRefreshLayout.setRefreshing(false);

                        showData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d(e.toString());
                        ToastUtil.showNetworkFailToast(getActivity());

                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(TaskListResponse response) {
                        Log.d();
                        if (response.header.resultCode == 0) {
                            mMatchList = response.matches;
                        }
                    }
                });
    }

    /**
     * 根据当前选择标签的不同，决定显示未完成的还是已完成的数据
     */
    private void showData() {
        Log.d();
            mAdapter.setDataList(mMatchList);

        if (mAdapter.getDataList().size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void handleItemClick(final int position) {
        String[] menus = new String[2];
        menus[0] = "分配主队";
        menus[1] = "分配客队";
        new AlertDialog.Builder(getActivity())
                .setItems(menus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchTask(position, which);
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    private void dispatchTask(int position, int menuPosition) {
        final TaskListResponse.HttpMatch match = mAdapter.getItem(position);

        String dialogTitle = "分配任务";
        int teamType = 1;
        if (menuPosition == 0) {
            //分配主队
            dialogTitle = "分配主队任务";
            teamType = 1;
        } else {
            dialogTitle = "分配客队任务";
            teamType = 2;
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_edit, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_recorderphone);
        final EditText editText = (EditText) view.findViewById(R.id.et_dispatch_phone);

        if (teamType == 1) {
            if (match.recorderHome != null) {
                textView.setText("当前主队任务领取人：" + match.recorderHome.phone);
            } else {
                textView.setText("当前主队任务无人领取");
            }
        } else {
            if (match.recorderVisitor != null) {
                textView.setText("当前客队任务领取人：" + match.recorderVisitor.phone);
            } else {
                textView.setText("当前客队任务无人领取");
            }
        }

        final int finalTeamType = teamType;
        new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(view)
                .setPositiveButton("分配", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPhone = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(newPhone)) {
                            ToastUtil.showToast(getActivity(), "请输入记录员的手机号码");
                        } else {
                            doAsignTask(match.id, newPhone, finalTeamType);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    private void doAsignTask(long matchId, String newPhone, int asignedTeam) {
        AsignTaskRequest request = new AsignTaskRequest(getActivity());
        request.matchId = matchId;
        request.recorderPhone = newPhone;
        request.asignedTeam = asignedTeam;

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("分配任务...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        mLadderBallApi.doAsignTask(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
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
                        e.printStackTrace();
                        Log.e(e.toString());
                        progressDialog.cancel();
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        progressDialog.cancel();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getActivity(), "分配任务完成");
                            doGetTasks();
                        } else {
                            ToastUtil.showToast(getActivity(), baseResponse.header.resultText);
                        }
                    }
                });
    }

    private boolean isMainLooper() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d();

        RxBus.get().unregister(RxBusTag.TASK_FRAGMENT, mItemObservable);
        mItemObservable = null;
    }
}
