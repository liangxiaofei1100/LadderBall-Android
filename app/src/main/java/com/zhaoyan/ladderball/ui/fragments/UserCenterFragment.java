package com.zhaoyan.ladderball.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.BallConstants;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.User;
import com.zhaoyan.ladderball.ui.activity.LoginActivity;
import com.zhaoyan.ladderball.ui.activity.ModifyPasswordActivity;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.CommonUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.SharedPreferencesManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class UserCenterFragment extends Fragment {

    @Bind(R.id.siv_username)
    SettingItemView mUserNameView;
    @Bind(R.id.siv_phone)
    SettingItemView mUserPhoneView;
    @Bind(R.id.siv_address)
    SettingItemView mUserAddressView;
    @Bind(R.id.siv_gender)
    SettingItemView mUserGenderView;


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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Observable<User> observable = getUserInfo();
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        if (user != null) {
                            mUserNameView.setSummaryText(user.mUserName);
                            mUserPhoneView.setSummaryText(user.mPhone);
                            mUserAddressView.setSummaryText(user.mAddress);
                            if (user.mGender == BallConstants.MEN) {
                                mUserGenderView.setSummaryText("男");
                            } else {
                                mUserGenderView.setSummaryText("女");
                            }
                        }
                    }
                });
    }

    private Observable<User> getUserInfo() {
        String phone = CommonUtil.getUserPhone(getActivity());
        Log.d("phone:" + phone);
        final User user = new Select().from(User.class).where("phone=?", phone).executeSingle();
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                subscriber.onNext(user);
            }
        });
    }

    @OnClick(R.id.siv_modify_pw)
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
