package com.zhaoyan.ladderballmanager.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhaoyan.ladderballmanager.R;
import com.zhaoyan.ladderballmanager.http.request.CreateMatchRequest;
import com.zhaoyan.ladderballmanager.http.response.BaseResponse;
import com.zhaoyan.ladderballmanager.ui.fragment.TaskFragment;
import com.zhaoyan.ladderballmanager.util.Log;
import com.zhaoyan.ladderballmanager.util.ToastUtil;
import com.zhaoyan.ladderballmanager.util.rx.RxBus;
import com.zhaoyan.ladderballmanager.util.rx.RxBusTag;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CreateTaskActivity extends BaseActivity {

    @Bind(R.id.et_teamhome_name)
    EditText mTeamHomeNameET;
    @Bind(R.id.et_teamvisitor_name)
    EditText mTeamVisitorNameET;
    @Bind(R.id.et_playerNumber)
    EditText mPlayerNumberET;
    @Bind(R.id.et_partNumber)
    EditText mPartNumberET;
    @Bind(R.id.et_partMinutes)
    EditText mPartMinutesET;
    @Bind(R.id.et_address)
    EditText mAddressET;

    @Bind(R.id.spinner_teamhome_color)
    Spinner mTeamHomeColorSpinner;
    @Bind(R.id.spinner_teamvisitor_color)
    Spinner mTeamVisitorColorSpinner;
//    @Bind(R.id.spinner_playerNumber)
//    Spinner mPlayerNumberSpinner;

    @Bind(R.id.tv_datetime)
    TextView mDateTimeTV;
    @Bind(R.id.btn_select_datetime)
    Button mSelectDateTimeBtn;

    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] teamColors = getResources().getStringArray(R.array.team_color);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, teamColors);

        mTeamHomeColorSpinner.setAdapter(adapter);
        mTeamVisitorColorSpinner.setAdapter(adapter);
    }

    @OnClick(R.id.btn_create_match)
    void attempCreate() {
        mTeamHomeNameET.setError(null);
        mTeamVisitorNameET.setError(null);
        mPlayerNumberET.setError(null);
        mPartNumberET.setError(null);
        mPartMinutesET.setError(null);
        mAddressET.setError(null);

        String teamHomeName = mTeamHomeNameET.getText().toString().trim();
        String teamVisitorName = mTeamVisitorNameET.getText().toString().trim();
        String playerNumber = mPlayerNumberET.getText().toString().trim();
        String partNumber = mPartNumberET.getText().toString().trim();
        String partMinutes = mPartMinutesET.getText().toString().trim();
        String address = mAddressET.getText().toString().trim();

        View focusView = null;
        boolean isCancel = false;

        if (TextUtils.isEmpty(address)) {
            mAddressET.setError("地点不能为空");
            focusView = mAddressET;
            isCancel = true;
        }

        if (TextUtils.isEmpty(partMinutes)) {
            mPartMinutesET.setError("请填写每节的时间(分)");
            focusView = mPartMinutesET;
            isCancel = true;
        }

        if (TextUtils.isEmpty(partNumber)) {
            mPartNumberET.setError("请填写小节数");
            focusView = mPartNumberET;
            isCancel = true;
        }

        if (TextUtils.isEmpty(playerNumber) || !isValidPlayerNumber(playerNumber)) {
            mPlayerNumberET.setError("请输入正确的赛制人数");
            focusView = mPlayerNumberET;
            isCancel = true;
        }

        if (TextUtils.isEmpty(teamVisitorName)) {
            mTeamVisitorNameET.setError("客队名称不能为空");
            focusView = mTeamVisitorNameET;
            isCancel = true;
        }

        if (TextUtils.isEmpty(teamHomeName)) {
            mTeamHomeNameET.setError("主队名称不能为空");
            focusView = mTeamHomeNameET;
            isCancel = true;
        }

        if (isCancel) {
            focusView.requestFocus();
            return;
        }

        if (mStartTime == 0) {
            ToastUtil.showToast(getApplicationContext(), "请设置比赛时间");
            return;
        }

        String teamHomeColor = (String) mTeamHomeColorSpinner.getSelectedItem();
        String teamVisitorColor = (String) mTeamVisitorColorSpinner.getSelectedItem();
        Log.d("homeColor:" + teamHomeColor + ",visitorColor:" + teamVisitorColor);

        CreateMatchRequest request = new CreateMatchRequest(getApplicationContext());
        request.teamHomeName = teamHomeName;
        request.teamVisitorName = teamVisitorName;
        request.teamHomeColor = teamHomeColor;
        request.teamVisitorColor = teamVisitorColor;
        request.playerNumber = Integer.valueOf(playerNumber);
        request.totalPat = Integer.valueOf(partNumber);
        request.partMinutes = Integer.valueOf(partMinutes);
        request.address = address;
        request.startTime = mStartTime;

        doCreateMatch(request);
    }

    private void doCreateMatch(CreateMatchRequest request) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在创建比赛...");
        progressDialog.setCancelable(false);

        mLadderBallApi.doCreateMatch(request)
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
                        ToastUtil.showNetworkFailToast(getApplicationContext());
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "创建比赛成功");

                            RxBus.get().post(RxBusTag.TASK_FRAGMENT, TaskFragment.REGET_DATA);

                            CreateTaskActivity.this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });

    }

    @OnClick(R.id.btn_select_datetime)
    void doSelectDateTime() {
        Intent intent = new Intent();
        intent.setClass(this, DateTimeActivity.class);
        startActivityForResult(intent, 0);
    }


    private boolean isValidPlayerNumber(String number) {
        int playerNumber = Integer.valueOf(number);
        if (playerNumber >= 1 && playerNumber <= 11) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            long time = data.getLongExtra("datetime", -1);

            if (time != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                String dateTime = sdf.format(new Date(time));
                mDateTimeTV.setText(dateTime);
                Log.d(dateTime);

                mStartTime = time;
            }
        }
    }
}
