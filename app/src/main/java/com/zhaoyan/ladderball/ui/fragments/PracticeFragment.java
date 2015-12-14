package com.zhaoyan.ladderball.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.BaseRequest;
import com.zhaoyan.ladderball.http.response.TaskListResponse;
import com.zhaoyan.ladderball.model.PracticeTask;
import com.zhaoyan.ladderball.ui.activity.MainActivity;
import com.zhaoyan.ladderball.ui.adapter.PracticeTaskAdapter;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link PracticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticeFragment extends BaseFragment {

    @Bind(R.id.segmentControl)
    SegmentControl mSegmentControl;

    @Bind(R.id.task_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.task_recyclerview)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_task_empty)
    TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;
    private PracticeTaskAdapter mAdapter;

    public static final int REGET_DATA = -2;
    private Observable<Integer> mItemObservable;

    public static final int TYPE_UNASSIGNED = 0;
    public static final int TYPE_ASSIGNED = 1;
    private int mTaskType = TYPE_ASSIGNED;

    private List<PracticeTask> mUnAssignedTaskList = new ArrayList<>();
    private List<PracticeTask> mAssignedTaskList = new ArrayList<>();

    private boolean mUnAssignedPracticeHasGet = false;

    public PracticeFragment() {
        // Required empty public constructor
    }

    public static PracticeFragment newInstance() {
        return new PracticeFragment();
    }

    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mMainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mItemObservable = RxBus.get().register(RxBusTag.PRACTICE_ITEM_CLICK, Integer.class);
        mItemObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        if (position == REGET_DATA) {
                            Log.d("refresh data");
                            mTaskType = TYPE_ASSIGNED;
                            mSegmentControl.setSelectedIndex(0);
                            doGetTasks();
                            return;
                        }
                        PracticeTask task = mAdapter.getItem(position);

                        ToastUtil.showToast(getActivity(), "努力开发中...");
//                        startActivity(TaskMainActivity.getStartIntent(getActivity(), task.mMatchId));
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

        mSegmentControl.setText("已领取", "未领取");
        mSegmentControl.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                Log.d("index:" + index);
                if (index == 0) {
                    mTaskType = TYPE_ASSIGNED;
                    showData();
                } else {
                    mTaskType = TYPE_UNASSIGNED;
                    if (mUnAssignedPracticeHasGet) {
                        showData();
                    } else {
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(true);
                            }
                        });
                        doGetTasks();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PracticeTaskAdapter(getActivity(), new ArrayList<PracticeTask>());
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

        Observable<TaskListResponse> responseObservable;
        if (isAssigned()) {
            responseObservable = mLadderBallApi.doGetPracticeList(new BaseRequest(getActivity()));
        } else {
            responseObservable = mLadderBallApi.doGetUnAssignPracticeList(new BaseRequest(getActivity()));
        }

        responseObservable
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<TaskListResponse, Observable<TaskListResponse.HttpMatch>>() {
                    @Override
                    public Observable<TaskListResponse.HttpMatch> call(TaskListResponse taskListResponse) {
                        return Observable.from(taskListResponse.matches);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TaskListResponse.HttpMatch, PracticeTask>() {
                    @Override
                    public PracticeTask call(TaskListResponse.HttpMatch httpMatch) {
                        Log.d("mattchid:" + httpMatch.id + ",address:" + httpMatch.address);
                        PracticeTask task = new Select().from(PracticeTask.class).where("matchId=?", httpMatch.id).executeSingle();
                        if (task == null) {
                            task = new PracticeTask();
                        }
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
                        task.mIsAssigned = isAssigned();

                        task.save();

                        Log.d("finish saved");

                        return task;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PracticeTask>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d();
                        if (isAssigned()) {
                            mAssignedTaskList.clear();
                        } else {
                            mUnAssignedTaskList.clear();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d();
//                        ToastUtil.showToast(getActivity(), "获取任务完成");
                        if (!isAssigned()) {
                            mUnAssignedPracticeHasGet = true;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        showData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d(e.toString());
                        if (mMainActivity.getSelectTabPosition() == 1) {
                            ToastUtil.showToast(getActivity(), "网络连接失败，请重试");
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(PracticeTask task) {
                        Log.d(task.toString());
                        if (task.mIsAssigned) {
                            mAssignedTaskList.add(task);
                        } else {
                            mUnAssignedTaskList.add(task);
                        }
                    }
                });
    }

    /**
     * 根据当前选择标签的不同，决定显示未完成的还是已完成的数据
     */
    private void showData() {
        Log.d("isAssigned:" + isAssigned());
        if (isAssigned()) {
            mAdapter.setDataList(mAssignedTaskList);
        } else {
            mAdapter.setDataList(mUnAssignedTaskList);
        }

        if (mAdapter.getDataList().size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }

    private boolean isAssigned() {
        return mTaskType == TYPE_ASSIGNED;
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

        mMainActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d();

        RxBus.get().unregister(RxBusTag.PRACTICE_ITEM_CLICK, mItemObservable);
        mItemObservable = null;
    }
}
