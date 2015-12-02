package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.User;
import com.zhaoyan.ladderball.util.CommonUtil;
import com.zhaoyan.ladderball.util.Log;

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
public class LoginActivity extends AppCompatActivity {

    // UI references.
    @Bind(R.id.phone)
    EditText mPhoneView;
    @Bind(R.id.password)
    EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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
            doLoginAction();
        }
    }

    private boolean isPhoneValid(String phone) {
        return CommonUtil.isValidPhoneNumber(phone);
    }

    private boolean isPasswordValid(String password) {
        //TODO: 确定密码的格式逻辑
        return password.length() > 4;
    }

    public void doLoginAction() {
        //使用RxJava执行登录
        Observable<User> observable = Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                Log.d("start");
                User user;
                for (int i=0; i < 2; i++) {
                    Log.d("start>>>>" + i);
                    user = new User();
                    user.mUserName = "username" + i;
                    subscriber.onNext(user);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(e.toString());
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
                Log.d("end");
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //在这里执行加载等待动画
                        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("正在登录中...");
                        progressDialog.show();
                    }
                })
                //加载等待动画必须在UI线程中执行
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        LoginActivity.this.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.toString());
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d("user.name:" + user.mUserName);

                    }
                });

    }

}

