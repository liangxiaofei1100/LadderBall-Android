package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.CreatePracticeRequest;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.ui.fragments.PracticeFragment;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CreatePracticeActivity extends BaseActivity {

    @Bind(R.id.et_team_home_name)
    EditText mTeamHomeNameView;
    @Bind(R.id.et_team_visitor_name)
    EditText mTeamVisitorNameView;
    @Bind(R.id.et_match_playernumber)
    EditText mMatchPlayerNUmberView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_practice);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @OnClick(R.id.btn_create_practice_save)
    void doCreatePractice() {
        mTeamHomeNameView.setError(null);
        mTeamVisitorNameView.setError(null);
        mMatchPlayerNUmberView.setError(null);

        String teamHomeName = mTeamHomeNameView.getText().toString().trim();
        String teamVisitorName = mTeamVisitorNameView.getText().toString().trim();
        String matchPlayerNumber = mMatchPlayerNUmberView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(matchPlayerNumber) || !isMatchPlayerNumberValid(matchPlayerNumber)) {
            mMatchPlayerNUmberView.setError("请输入1-11的整数");
            focusView = mMatchPlayerNUmberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(teamVisitorName)) {
            mTeamVisitorNameView.setError("名称不能为空");
            focusView = mTeamVisitorNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(teamHomeName)) {
            mTeamHomeNameView.setError("名称不能为空");
            focusView = mTeamHomeNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTeamHomeNameView.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mTeamVisitorNameView.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mMatchPlayerNUmberView.getWindowToken(), 0);


            startCreatePractice(teamHomeName, teamVisitorName, Integer.valueOf(matchPlayerNumber));
        }

    }

    private void startCreatePractice(String teamHomeName, String teamVisitorName, int playerNumber) {
        CreatePracticeRequest request = new CreatePracticeRequest(getApplicationContext());
        request.teamHomeName = teamHomeName;
        request.teamVisitorName = teamVisitorName;
        request.playerNumber = playerNumber;

        Observable<BaseResponse> observable = mLadderBallApi.doCreatePracticeTask(request)
                .subscribeOn(Schedulers.io());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("创建练习赛中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        observable
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

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.cancel();
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showToast(getApplicationContext(), "无法连接到网络，请重试");
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        progressDialog.cancel();

                        if (baseResponse.header.resultCode == 0) {
                            RxBus.get().post(RxBusTag.PRACTICE_ITEM_CLICK, PracticeFragment.REGET_DATA);

                            CreatePracticeActivity.this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });

    }

    private boolean isMatchPlayerNumberValid(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }

        int playerNumber = Integer.valueOf(number);
        if (playerNumber < 1 || playerNumber > 11) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
