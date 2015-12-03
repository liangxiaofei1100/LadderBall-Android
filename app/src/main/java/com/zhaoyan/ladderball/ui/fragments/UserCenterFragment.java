package com.zhaoyan.ladderball.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.ui.activity.LoginActivity;
import com.zhaoyan.ladderball.ui.activity.ModifyPasswordActivity;
import com.zhaoyan.ladderball.util.CommonUtil;
import com.zhaoyan.ladderball.util.SharedPreferencesManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserCenterFragment extends Fragment {


    public UserCenterFragment() {
        // Required empty public constructor
    }

    public static UserCenterFragment newInstance() {
        UserCenterFragment fragment = new UserCenterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_center, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @OnClick(R.id.rl_user_modify_passwd)
    public void doModifyPasswd() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ModifyPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_exit)
    void doExitLogin() {
        SharedPreferencesManager.put(getActivity(), CommonUtil.KEY_USER_PHONE, "-1");

        Intent intent = new Intent();
        intent.setClass(getActivity(), LoginActivity.class);
        startActivity(intent);

        getActivity().finish();
    }

}
