package com.zhaoyan.ladderball.ui.fragments;


import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.BaseRequest;
import com.zhaoyan.ladderball.http.response.TaskListResponse;
import com.zhaoyan.ladderball.model.Task;
import com.zhaoyan.ladderball.ui.activity.TaskMainActivity;
import com.zhaoyan.ladderball.ui.adapter.TaskAdapter;
import com.zhaoyan.ladderball.ui.view.SegmentControl;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 任务tab页，显示当前用户（记录员）已领取的任务列表（比赛列表）
 */
public class TaskFragment extends BaseFragment {

    @Bind(R.id.segmentControl)
    SegmentControl mSegmentControl;

    @Bind(R.id.task_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.task_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_task_empty)
    TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;
    private TaskAdapter mAdapter;

    private Observable<Integer> mItemObservable;

    public static final int TYPE_UNCOMPLETEED = 0;
    public static final int TYPE_COMPLETEED = 1;
    private int mTaskType = TYPE_UNCOMPLETEED;

    private List<Task> mUnCompleteTaskList = new ArrayList<>();
    private List<Task> mCompleteTaskList = new ArrayList<>();

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

        mItemObservable = RxBus.get().register(RxBusTag.TASK_ITEM_CLICK, Integer.class);
        mItemObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        Task task = mAdapter.getItem(position);

                        startActivity(TaskMainActivity.getStartIntent(getActivity(), task.mMatchId));
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

        mSegmentControl.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                Log.d("index:" + index);
                if (index == 0) {
                    mTaskType = TYPE_UNCOMPLETEED;
                } else {
                    mTaskType = TYPE_COMPLETEED;
                }
                showData();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TaskAdapter(getActivity(), new ArrayList<Task>());
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
                .flatMap(new Func1<TaskListResponse, Observable<TaskListResponse.HttpMatch>>() {
                    @Override
                    public Observable<TaskListResponse.HttpMatch> call(TaskListResponse taskListResponse) {
                        return Observable.from(taskListResponse.matches);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TaskListResponse.HttpMatch, Task>() {
                    @Override
                    public Task call(TaskListResponse.HttpMatch httpMatch) {
                        Log.d("mattchid:" + httpMatch.id + ",address:" + httpMatch.address);
                        Task task = new Task();
                        task.mMatchId = httpMatch.id;

                        task.mTeamHomeName = httpMatch.teamHome.name;
                        task.mTeamHomeColor = httpMatch.teamHome.color;
                        task.mTeamHomeScore = httpMatch.teamHome.score;
                        task.mTeamHomeIsAssigned = httpMatch.teamHome.isAsigned;
                        task.mTeamHomeLogoUrl = httpMatch.teamHome.logoURL;

                        task.mTeamVisitorName = httpMatch.teamVisitor.name;
                        task.mTeamVisitorColor = httpMatch.teamVisitor.color;
                        task.mTeamVisitorScore = httpMatch.teamVisitor.score;
                        task.mTeamVisitorIsAssigned = httpMatch.teamVisitor.isAsigned;
                        task.mTeamVisitorLogoUrl = httpMatch.teamVisitor.logoURL;

                        task.mPlayerNum = httpMatch.playerNumber;
                        task.mAddress = httpMatch.address;
                        task.mIsComplete = httpMatch.complete;
                        task.mStartTime = httpMatch.startTime;
                        return task;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Task>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d();
                        mUnCompleteTaskList.clear();
                        mCompleteTaskList.clear();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d();
//                        ToastUtil.showToast(getActivity(), "获取任务完成");
                        mSwipeRefreshLayout.setRefreshing(false);
                        showData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.toString());
                        ToastUtil.showToast(getActivity(), "获取任务出错，请重试");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Task task) {
                        Log.d(task.toString());
                        if (task.mIsComplete) {
                            mCompleteTaskList.add(task);
                        } else {
                            mUnCompleteTaskList.add(task);
                        }
//                        mAdapter.addDataToList(task);
                    }
                });
    }

    /**
     * 根据当前选择标签的不同，决定显示未完成的还是已完成的数据
     */
    private void showData() {
        Log.d();
        if (mTaskType == TYPE_UNCOMPLETEED) {
            mAdapter.setDataList(mUnCompleteTaskList);
        } else {
            mAdapter.setDataList(mCompleteTaskList);
        }

        if (mAdapter.getDataList().size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
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

        RxBus.get().unregister(RxBusTag.TASK_ITEM_CLICK, mItemObservable);
        mItemObservable = null;
    }
}
