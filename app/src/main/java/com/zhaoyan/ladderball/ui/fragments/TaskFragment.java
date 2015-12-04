package com.zhaoyan.ladderball.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.ui.view.SegmentControl;
import com.zhaoyan.ladderball.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskFragment extends Fragment {

    @Bind(R.id.segmentControl)
    SegmentControl mSegmentControl;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    }
}
