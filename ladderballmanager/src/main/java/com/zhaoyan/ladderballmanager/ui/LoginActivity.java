package com.zhaoyan.ladderballmanager.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderballmanager.R;
import com.zhaoyan.ladderballmanager.http.request.LoginRequest;
import com.zhaoyan.ladderballmanager.http.response.LoginResponse;
import com.zhaoyan.ladderballmanager.model.User;
import com.zhaoyan.ladderballmanager.util.CommonUtil;
import com.zhaoyan.ladderballmanager.util.Log;
import com.zhaoyan.ladderballmanager.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity{


    // UI references.
    @Bind(R.id.phone)
    AutoCompleteTextView mPhoneView;
    @Bind(R.id.password)
    EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if (CommonUtil.isLogin(this)) {
            goToMain();
        }
    }

    private void goToMain() {
        Log.d();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);

        this.finish();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.phone_sign_in_button)
    void attemptLogin() {
        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            doLogin(phone, password);
        }
    }

    private void doLogin(String phone, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录...");
        progressDialog.setCancelable(false);

        LoginRequest request = new LoginRequest(getApplicationContext());
        request.userName = phone;
        request.password = password;
        request.loginType = 1;
        mLadderBallApi.doLogin(request)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.cancel();
                        e.printStackTrace();
                        Log.e(e.toString());
                        ToastUtil.showNetworkFailToast(getApplicationContext());
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        progressDialog.cancel();
                        if (loginResponse.header.resultCode == 0) {
                            Log.d("login success");
                            User user = new Select().from(User.class).where("userToken=?" + loginResponse.userToken).executeSingle();
                            if (user == null) {
                                user = new User();
                                user.mUserToken = loginResponse.userToken;
                                user.mName = loginResponse.name;
                                user.save();
                            }

                            goToMain();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), loginResponse.header.resultText);
                        }
                    }
                });
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


}

