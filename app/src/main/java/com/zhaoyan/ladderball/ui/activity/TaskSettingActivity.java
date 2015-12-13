package com.zhaoyan.ladderball.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.MatchModifyRequest;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.http.response.MatchDetailResponse;
import com.zhaoyan.ladderball.model.Match;
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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TaskSettingActivity extends BaseActivity {
    public static final String EXTRA_MATCH_ID = "match_id";
    private static final int REQUEST_CODE_PLAYER_CHOOSE = 0;

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

    @Bind(R.id.tv_task_setting_team_status)
    TextView mTeamAssigned;
    @Bind(R.id.tv_task_setting_team_info)
    TextView mTeamInfo;

    @Bind(R.id.tv_task_setting_total_time)
    TextView mTotalTime;

    @Bind(R.id.tv_task_setting_first_title)
    TextView mStartingUpTitle;

    private TaskSettingAdapter mAdapter;

    private List<Player> mFirstPlayerList = new ArrayList<>();

    private Observable<Integer> mItemObservable;

    private Match mDetailMatch;

    private boolean mHasChanged = false;

    public static Intent getStartIntent(Context context,long matchId) {
        Intent intent = new Intent();
        intent.setClass(context, TaskSettingActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long matchId = getIntent().getLongExtra(EXTRA_MATCH_ID, -1);
        Log.d("matchId:" + matchId);
        if (matchId == -1) {
            ToastUtil.showToast(getApplicationContext(), "数据错误");
            finish();
        }
        mDetailMatch = new Select().from(Match.class).where("matchId=?", matchId).executeSingle();
        if (mDetailMatch == null) {
            ToastUtil.showToast(getApplicationContext(), "数据错误");
            finish();
        }


        Log.d("teamHome:" + mDetailMatch.teamHome.toString());
        Log.d("teamVisitor:" + mDetailMatch.teamVisitor.toString());
        if (mDetailMatch.teamHome.isAssiged) {
            mTeamInfo.setText(mDetailMatch.teamHome.name + "(主队)  " + mDetailMatch.teamHome.color);

            //查询该场比赛，该只队伍的人员
            List<Player> playerList = new Select().from(Player.class).where("matchId=? and teamId=?"
                    , mDetailMatch.matchId, mDetailMatch.teamHome.teamId).execute();
            if (playerList == null) {
                playerList = new ArrayList<>();
            }
            mDetailMatch.teamHome.players = playerList;
            Log.d("teamHome.size:" + mDetailMatch.teamHome.players.size());
            for (Player player: mDetailMatch.teamHome.players) {
                if (player.isFirst) {
                    mFirstPlayerList.add(player);
                }
            }
        } else if (mDetailMatch.teamVisitor.isAssiged) {
            mTeamInfo.setText(mDetailMatch.teamVisitor.name + "(客队)  " + mDetailMatch.teamVisitor.color);

            //查询该场比赛，该只队伍的人员
            List<Player> playerList = new Select().from(Player.class).where("matchId=? and teamId=?"
                    , mDetailMatch.matchId, mDetailMatch.teamVisitor.teamId).execute();
            if (playerList == null) {
                playerList = new ArrayList<>();
            }
            mDetailMatch.teamVisitor.players = playerList;

            Log.d("teamVisitor.size:" + mDetailMatch.teamVisitor.players.size());
            for (Player player: mDetailMatch.teamVisitor.players) {
                if (player.isFirst) {
                    mFirstPlayerList.add(player);
                }
            }
        }

        mRuleItemView.setSummaryText(mDetailMatch.playerNumber + "人制");
        mJieItemView.setSummaryText(mDetailMatch.totalPart + "节");
        mJieTimeItemView.setSummaryText(mDetailMatch.partMinutes + "分钟");
        mTotalTime.setText("比赛共" + mDetailMatch.totalPart * mDetailMatch.partMinutes + "分钟");

        mStartingUpTitle.setText("设置首发（" + mFirstPlayerList.size() + "/" + mDetailMatch.playerNumber + "）");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new TaskSettingAdapter(this, mFirstPlayerList);
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
        setResult(RESULT_CANCELED);
    }

    @OnClick(R.id.btn_add_player)
    void addPlayer() {
//        showAddNewPlayerDialog();
        if (mDetailMatch == null)
            return;

        long teamId;
        String teamName;
        if (mDetailMatch.teamHome.isAssiged) {
            teamId = mDetailMatch.teamHome.teamId;
            teamName = mDetailMatch.teamHome.name;
        } else {
            teamId = mDetailMatch.teamVisitor.teamId;
            teamName = mDetailMatch.teamVisitor.name;
        }
        startActivityForResult(ChooseStartingUpPlayerActivity.getStartIntent(this, mDetailMatch.matchId,
                teamId, mDetailMatch.playerNumber, teamName), REQUEST_CODE_PLAYER_CHOOSE);
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

    @OnClick(R.id.btn_task_setting_commit)
    void doModifyMatch() {
        if (mDetailMatch == null) {
            return;
        }
        MatchModifyRequest request = new MatchModifyRequest(getApplicationContext());
        request.matchId = mDetailMatch.matchId;
        request.partMinutes = mDetailMatch.partMinutes;
        request.totalPart = mDetailMatch.totalPart;
        request.playerNumber = mDetailMatch.playerNumber;
        MatchDetailResponse.HttpPlayer httpPlayer;
        if (mDetailMatch.teamHome.isAssiged) {
            for (Player player: mDetailMatch.teamHome.players) {
                httpPlayer = new MatchDetailResponse.HttpPlayer();
                httpPlayer.id = player.playerId;
                httpPlayer.name = player.name;
                httpPlayer.number = player.number;
                httpPlayer.isFirst = player.isFirst;
                request.players.add(httpPlayer);
            }
        } else if (mDetailMatch.teamVisitor.isAssiged) {
            for (Player player: mDetailMatch.teamVisitor.players) {
                httpPlayer = new MatchDetailResponse.HttpPlayer();
                httpPlayer.id = player.playerId;
                httpPlayer.name = player.name;
                httpPlayer.number = player.number;
                httpPlayer.isFirst = player.isFirst;
                request.players.add(httpPlayer);
            }
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在提交，请等待...");
        Observable<BaseResponse> observable = mLadderBallApi.doModifyMatch(request)
                .subscribeOn(Schedulers.io());

        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.toString());
                        e.printStackTrace();
                        Log.e(e);
                        progressDialog.cancel();
                        ToastUtil.showToast(getApplicationContext(), "提交失败，请检查网络");
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        progressDialog.cancel();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "设置修改成功");

                            setResult(RESULT_OK);

                            RxBus.get().post(RxBusTag.TASK_ITEM_CLICK, -2);

                            TaskSettingActivity.this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), baseResponse.header.resultText);
                        }
                    }
                });
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
            //textInputLayout.setHint("请输入1到6的整数");
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

                    mDetailMatch.playerNumber = num;
                } else if (type == 1) {
                    /*if (num < 1 || num > 6) {
                        editText.setError("请输入1到6的整数");
                        ToastUtil.showToast(getApplicationContext(), "请输入1到6的整数");
                        setDialogDismiss(dialog, false);
                        return;
                    }*/

                    mJieItemView.setSummaryText(num + "节");
                    mTotalTime.setText("比赛共" + mDetailMatch.totalPart * mDetailMatch.partMinutes + "分钟");
                    mDetailMatch.totalPart = num;
                } else if (type == 2){
                    mJieTimeItemView.setSummaryText(num + "分钟");
                    mTotalTime.setText("比赛共" + mDetailMatch.totalPart * mDetailMatch.partMinutes + "分钟");
                    mDetailMatch.partMinutes = num;
                }

                mHasChanged = true;

                mDetailMatch.save();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PLAYER_CHOOSE) {
                mHasChanged = true;
                mDetailMatch = new Select().from(Match.class).where("matchId=?", mDetailMatch.matchId).executeSingle();

                if (mDetailMatch != null) {
                    mAdapter.clear();
                    if (mDetailMatch.teamHome.isAssiged) {
                        Log.d("teamHome.size:" + mDetailMatch.teamHome.players.size());
                        for (Player player: mDetailMatch.teamHome.players) {
                            if (player.isFirst) {
                                mFirstPlayerList.add(player);
                            }
                        }
                    } else if (mDetailMatch.teamVisitor.isAssiged) {
                        Log.d("teamVisitor.size:" + mDetailMatch.teamVisitor.players.size());
                        for (Player player: mDetailMatch.teamVisitor.players) {
                            if (player.isFirst) {
                                mFirstPlayerList.add(player);
                            }
                        }
                    }
                    mAdapter.setDataList(mFirstPlayerList);
                    mAdapter.notifyDataSetChanged();

                    mStartingUpTitle.setText("设置首发（" + mFirstPlayerList.size() + "/" + mDetailMatch.playerNumber + "）");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mHasChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否保存当前修改？")
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doModifyMatch();
                        }
                    })
                    .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TaskSettingActivity.this.finish();
                        }
                    })
                    .create().show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(RxBusTag.PlAYER_ITEM_REMOVE, mItemObservable);
    }
}
