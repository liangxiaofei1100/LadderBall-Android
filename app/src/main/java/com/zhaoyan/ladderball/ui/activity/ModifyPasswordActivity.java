package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.ModifyPasswordRequest;
import com.zhaoyan.ladderball.http.response.ModifyPasswordResponse;
import com.zhaoyan.ladderball.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ModifyPasswordActivity extends BaseActivity {

    @Bind(R.id.et_old_password)
    EditText mOldPwEditText;
    @Bind(R.id.et_new_password)
    EditText mNewPwEditText;
    @Bind(R.id.et_new_password_again)
    EditText mNewPwAgainEditText;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("请求修改密码...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    @OnClick(R.id.btn_modify_pw_confirm)
    public void attemptModifyPassword() {
        // Reset errors.
        mOldPwEditText.setError(null);
        mNewPwEditText.setError(null);
        mNewPwAgainEditText.setError(null);
        //
        String oldPassword = mOldPwEditText.getText().toString();
        String newPassword = mNewPwEditText.getText().toString();
        String newPasswordAgain = mNewPwAgainEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(newPasswordAgain)) {
            mNewPwAgainEditText.setError("请再次输入新密码");
            focusView = mNewPwAgainEditText;
            cancel = true;
        }

        if (!newPassword.equals(newPasswordAgain)) {
            mNewPwAgainEditText.setError("两次输入的新密码不相同");
            focusView = mNewPwAgainEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPassword)) {
            mNewPwEditText.setError("新密码不能为空");
            focusView = mNewPwEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(oldPassword)) {
            mOldPwEditText.setError("旧密码不能为空");
            focusView = mOldPwEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            doModifyPassword();
        }

    }

    private void doModifyPassword() {

        ModifyPasswordRequest request = new ModifyPasswordRequest(getApplicationContext());

        Observable<ModifyPasswordResponse> observable = mLadderBallApi.doModifyPassword(request);
        observable.subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.show();;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ModifyPasswordResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }
                        Snackbar.make(mOldPwEditText, "网络错误，请检查网络连接", Snackbar.LENGTH_SHORT).show();
                        Log.d(e.toString());
                    }

                    @Override
                    public void onNext(ModifyPasswordResponse response) {
                        Log.d("resultCode=" + response.header.resultCode);
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }

                        if (response.header.resultCode == 0) {
                            Snackbar.make(mOldPwEditText, "密码修改成功", Snackbar.LENGTH_SHORT).show();

                            ModifyPasswordActivity.this.finish();
                        } else {
                            Snackbar.make(mOldPwEditText, response.header.resultText, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
