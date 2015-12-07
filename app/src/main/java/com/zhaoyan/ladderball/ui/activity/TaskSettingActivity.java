package com.zhaoyan.ladderball.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.TaskSettingAdapter;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class TaskSettingActivity extends AppCompatActivity {

    @Bind(R.id.stv_rule)
    SettingItemView mRuleItemView;
    @Bind(R.id.stv_jie)
    SettingItemView mJieItemView;
    @Bind(R.id.stv_jie_time)
    SettingItemView mJieTimeItemView;

    @Bind(R.id.rlv_task_setting)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_task_setting_commit)
    Button mCommitButton;

    private TaskSettingAdapter mAdapter;

    private List<Player> mPlayerList = new ArrayList<>();

    private Observable<Integer> mItemObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //test
        Player player;
        for (int i=0; i<6; i++) {
            player = new Player();
            player.number = i;
            mPlayerList.add(player);
        }
        //test

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new TaskSettingAdapter(this, mPlayerList);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setAdapter(mAdapter);

        mItemObservable = RxBus.get().register(RxBusTag.PlAYER_ITEM_REMOVE, Integer.class);
        mItemObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(final Integer integer) {
                Log.d("isMain:" + (Looper.myLooper() == Looper.getMainLooper()));
                Player player1 = mAdapter.getItem(integer);
                new AlertDialog.Builder(TaskSettingActivity.this)
                        .setMessage("确定移除" + player1.number + "号")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.removeItem(integer);
                            }
                        })
                        .create().show();
            }
        });

//        mCommitButton.setEnabled(false);
    }

    @OnClick(R.id.btn_add_player)
    void addPlayer() {
        showAddNewPlayerDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.stv_rule)
    void doSetMatchRule() {
        showEditDialog(0);
    }

    @OnClick(R.id.stv_jie)
    void doSetJie() {
        showEditDialog(1);
    }
    @OnClick(R.id.stv_jie_time)
    void doSetJieTime() {
        showEditDialog(2);
    }

    public void showEditDialog(final int type) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_dialog);
        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.til_dialog);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (type == 0) {
            textInputLayout.setHint("请输入1到11的整数");
            dialog.setTitle("赛制人数设置");
        } else if (type == 1) {
            dialog.setTitle("比赛节数设置");
        } else if (type == 2){
            dialog.setTitle("每节时长设置");
        }
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                Log.d("text：" + text);
                if (text.isEmpty()) {
                    editText.setError("不能为空");
                    ToastUtil.showToast(getApplicationContext(), "不能为空");
                    setDialogDismiss(dialog, false);
                    return;
                }

                int num = Integer.valueOf(text);
                if (type == 0) {
                    if (num < 1 || num > 11) {
                        editText.setError("请输入1到11的整数");
                        ToastUtil.showToast(getApplicationContext(), "请输入1到11的整数");
                        setDialogDismiss(dialog, false);
                        return;
                    }
                    mRuleItemView.setSummaryText(num + "人制");
                } else if (type == 1) {
                    mJieItemView.setSummaryText(num + "节");
                } else if (type == 2){
                    mJieTimeItemView.setSummaryText(num + "分钟");
                }
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create().show();
    }

    private void showAddNewPlayerDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_new_player, null);
        final EditText numEditText = (EditText) view.findViewById(R.id.et_add_new_player_number);
        final EditText nameEditText = (EditText) view.findViewById(R.id.et_add_new_player_name);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("新增球员");
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = numEditText.getText().toString();
                Log.d("text：" + text);
                if (text.isEmpty()) {
                    ToastUtil.showToast(getApplicationContext(), "球员号码不能为空");
                    setDialogDismiss(dialog, false);
                    return;
                }

                String nameText = nameEditText.getText().toString().trim();
                int num = Integer.valueOf(text);
                Player player = new Player();
                player.number = num;
                mAdapter.addItem(player);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create().show();
    }

    public static void setDialogDismiss(DialogInterface dialog, boolean dismiss){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, dismiss);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(RxBusTag.PlAYER_ITEM_REMOVE, mItemObservable);
    }
}
