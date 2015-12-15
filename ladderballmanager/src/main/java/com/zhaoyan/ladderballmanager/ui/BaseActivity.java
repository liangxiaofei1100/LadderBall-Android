package com.zhaoyan.ladderballmanager.ui;

import android.support.v7.app.AppCompatActivity;

import com.zhaoyan.ladderballmanager.http.LBManagerApi;
import com.zhaoyan.ladderballmanager.http.LBManagerFactory;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 *
 * Created by Yuri on 2015/12/2.
 */
public class BaseActivity extends AppCompatActivity {

    public static final LBManagerApi mLadderBallApi = LBManagerFactory.getInstance();

    private CompositeSubscription mCompositeSubscription;

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

}
