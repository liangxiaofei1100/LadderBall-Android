package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.LoginRequest;
import com.zhaoyan.ladderball.http.response.LoginResponse;
import com.zhaoyan.ladderball.util.CommonUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.SharedPreferencesManager;
import com.zhaoyan.ladderball.util.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via phone/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    @Bind(R.id.phone)
    EditText mPhoneView;
    @Bind(R.id.password)
    EditText mPasswordView;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (CommonUtil.isLogin(getApplicationContext())) {
            goToMain();
        }

        //支持用户点击虚拟键盘的回车键直接登录
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在登录...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

    }


    /**
     * 开始登陆
     */
    @OnClick(R.id.sign_in_button)
    public void attemptLogin() {
        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_phone_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //doLoginAction
            Log.d("doLoginAction");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mPhoneView.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

            doLoginAction();
        }
    }

    private boolean isPhoneValid(String phone) {
        return CommonUtil.isValidPhoneNumber(phone);
    }

    private boolean isPasswordValid(String password) {
        //TODO: 确定密码的格式逻辑
        return password.length() >= 6;
    }

    public void doLoginAction() {
        //使用RxJava执行登录
        LoginRequest request = new LoginRequest(getApplicationContext());
        request.loginType = 1;
        request.userName = mPhoneView.getText().toString();
        request.password = mPasswordView.getText().toString();
        Observable<LoginResponse> observable = mLadderBallApi.doLogin(request);
        observable.subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //在这里执行加载等待动画
                        mProgressDialog.show();
                    }
                })
                //加载等待动画必须在UI线程中执行
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressDialog != null) {
                            mProgressDialog.cancel();
                        }

                        Log.e(e.toString());
                        Snackbar.make(mPasswordView, "网络错误，请重试！", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(LoginResponse response) {
                        onLoginOver(response);
                    }
                });

    }

    /**
     * 跳转主界面
     */
    private void goToMain() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void onLoginOver(LoginResponse response) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }

        if (response.header.resultCode != 0) {
            Snackbar.make(mPasswordView, "用户名或密码错误，登录失败", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mPasswordView, "登录成功", Snackbar.LENGTH_SHORT).show();

            CommonUtil.setUserHttpHeaderToken(getApplicationContext(), response.userToken);

            SharedPreferencesManager.put(getApplicationContext(), CommonUtil.KEY_USER_PHONE, response.phone);

            UserManager.saveOrUpdateUser(response);

            goToMain();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }
}

