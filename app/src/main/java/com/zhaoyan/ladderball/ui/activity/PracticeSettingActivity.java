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
import com.zhaoyan.ladderball.BallConstants;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.AddPlayerRequest;
import com.zhaoyan.ladderball.http.request.PracticeModifyRequest;
import com.zhaoyan.ladderball.http.response.AddPlayerResponse;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.http.response.PracticeDetailResponse;
import com.zhaoyan.ladderball.model.Match;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.model.TmpMatch;
import com.zhaoyan.ladderball.model.TmpPlayer;
import com.zhaoyan.ladderball.ui.adapter.PracticeSettingAdapter;
import com.zhaoyan.ladderball.ui.fragments.PracticeFragment;
import com.zhaoyan.ladderball.ui.view.SettingItemView;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.MatchUtil;
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
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 练习赛任务设置界面
 */
public class PracticeSettingActivity extends BaseActivity {
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

    private PracticeSettingAdapter mAdapter;

    private List<TmpPlayer> mAllPlayerList = new ArrayList<>();
    private List<TmpPlayer> mFirstPlayerList = new ArrayList<>();

    /**
     * 将当次操作所新增的球员id记录下来，如果退出的时候，记录员选择不保存，那么要讲这些球员的首发属性设置为false，否则会出问题
     */
    private List<Long> mNewAddPlayerIdList = new ArrayList<>();
    /**
     * 涉及到修改球员首发属性的球员id列表
     */
    private List<Long> mModifyPlayerIdList = new ArrayList<>();
    /**
     * 将档次操作的球员的id记录下来
     */

    private Observable<Integer> mItemObservable;

    private TmpMatch mDetailMatch;

    private boolean mHasChanged = false;

    private int mTeamType;

    public static Intent getStartIntent(Context context,long matchId) {
        Intent intent = new Intent();
        intent.setClass(context, PracticeSettingActivity.class);
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
        mDetailMatch = new Select().from(TmpMatch.class).where("matchId=?", matchId).executeSingle();
        if (mDetailMatch == null) {
            ToastUtil.showToast(getApplicationContext(), "数据错误");
            finish();
        }


        Log.d("teamHome:" + mDetailMatch.teamHome.toString());
        Log.d("teamVisitor:" + mDetailMatch.teamVisitor.toString());
        if (mDetailMatch.teamHome.isAssiged) {
            mTeamType = BallConstants.TEAM_HOME;

            mTeamInfo.setText(mDetailMatch.teamHome.name + "(主队)  " + mDetailMatch.teamHome.color);
            //查询该场比赛，该只队伍的人员
            mAllPlayerList = new Select().from(Player.class).where("matchId=? and teamId=?"
                    , mDetailMatch.matchId, mDetailMatch.teamHome.teamId).execute();
            if (mAllPlayerList == null) {
                mAllPlayerList = new ArrayList<>();
            }
            mDetailMatch.teamHome.players = mAllPlayerList;
            Log.d("teamHome.size:" + mDetailMatch.teamHome.players.size());
            for (TmpPlayer player: mDetailMatch.teamHome.players) {
                if (player.isFirst) {
                    mFirstPlayerList.add(player);//获取首发球员列表
                }
            }
        } else if (mDetailMatch.teamVisitor.isAssiged) {
            mTeamType = BallConstants.TEAM_VISITOR;
            mTeamInfo.setText(mDetailMatch.teamVisitor.name + "(客队)  " + mDetailMatch.teamVisitor.color);

            //查询该场比赛，该只队伍的人员
            mAllPlayerList = new Select().from(Player.class).where("matchId=? and teamId=?"
                    , mDetailMatch.matchId, mDetailMatch.teamVisitor.teamId).execute();
            if (mAllPlayerList == null) {
                mAllPlayerList = new ArrayList<>();
            }
            mDetailMatch.teamVisitor.players = mAllPlayerList;
            Log.d("teamVisitor.size:" + mDetailMatch.teamVisitor.players.size());
            for (TmpPlayer player: mDetailMatch.teamVisitor.players) {
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

        mAdapter = new PracticeSettingAdapter(this, mFirstPlayerList);
        mRecyclerView.setAdapter(mAdapter);

        mItemObservable = RxBus.get().register(RxBusTag.PlAYER_ITEM_REMOVE, Integer.class);
        mItemObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(final Integer integer) {
                Log.d("isMain:" + (Looper.myLooper() == Looper.getMainLooper()));
                final TmpPlayer player1 = mAdapter.getItem(integer);
                new AlertDialog.Builder(PracticeSettingActivity.this)
                        .setMessage("确定取消" + player1.number + "号首发位置")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setPlayerNonFirst(player1, integer);
                            }
                        })
                        .create().show();
            }
        });
        setResult(RESULT_CANCELED);
    }

    @OnClick(R.id.btn_add_player)
    void addPlayer() {
        if (mAdapter.getItemCount() >= mDetailMatch.playerNumber) {
            ToastUtil.showToast(getApplicationContext(), "首发位置已满，请先移除一个首发人员");
            return;
        }

        showAddNewPlayerDialog();
    }

    private void setPlayerNonFirst(TmpPlayer player, int poition) {
        player.isFirst = false;
        player.isOnPitch = false;
//        player.save();//不保存，待记录员点击提交按钮后  再保存，否则该次修改无效

        mAdapter.removeItem(poition);

        mModifyPlayerIdList.add(player.playerId);

        mHasChanged = true;

        mStartingUpTitle.setText("设置首发（" + mAdapter.getItemCount() + "/" + mDetailMatch.playerNumber + "）");
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

        if (mTeamType == BallConstants.TEAM_HOME) {
            Log.d("firstsize:" + mAdapter.getItemCount());
        } else {
            Log.d("firstsize:" +  mAdapter.getItemCount());
        }

        if (mAdapter.getItemCount() < mDetailMatch.playerNumber) {
            //如果当前首发人员小于赛制人数，则不能提交，提示用户
            ToastUtil.showToast(getApplicationContext(), "首发人员不足" + mDetailMatch.playerNumber + "人，请添加");
            return;
        }

        PracticeModifyRequest request = new PracticeModifyRequest(getApplicationContext());
        request.matchId = mDetailMatch.matchId;
        request.partMinutes = mDetailMatch.partMinutes;
        request.totalPart = mDetailMatch.totalPart;
        request.playerNumber = mDetailMatch.playerNumber;
        PracticeDetailResponse.HttpPlayer httpPlayer;

        if (mTeamType == BallConstants.TEAM_HOME) {
            for (TmpPlayer player : mDetailMatch.teamHome.players) {
                httpPlayer = new PracticeDetailResponse.HttpPlayer();
                httpPlayer.id = player.playerId;
                httpPlayer.name = player.name;
                httpPlayer.number = player.number;
                httpPlayer.isFirst = player.isFirst;
                request.players.add(httpPlayer);
            }
        } else {
            for (TmpPlayer player: mDetailMatch.teamVisitor.players) {
                httpPlayer = new PracticeDetailResponse.HttpPlayer();
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
        Observable<BaseResponse> observable = mLadderBallApi.doModifyPractice(request)
                .subscribeOn(Schedulers.io());

        observable
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse baseResponse) {
                        //保存数据库
                        Log.d("save detail match");
                        doSaveModifyPlayerData();
                        mDetailMatch.save();
                        return baseResponse;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.toString());
                        e.printStackTrace();
                        progressDialog.cancel();
                        ToastUtil.showToast(getApplicationContext(), "提交失败，请检查网络");
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        progressDialog.cancel();
                        if (baseResponse.header.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "设置修改成功");

                            setResult(RESULT_OK);

                            RxBus.get().post(RxBusTag.PRACTICE_ITEM_CLICK, PracticeFragment.REGET_DATA);

                            MatchUtil.setHadSetPractice(getApplicationContext(), mDetailMatch.matchId);

                            PracticeSettingActivity.this.finish();
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

                    if (num < mAdapter.getItemCount()) {
                        ToastUtil.showToast(getApplicationContext(), "首发人员超过" + num + "人，请调整");
                        return;
                    }

                    mRuleItemView.setSummaryText(num + "人制");

                    mDetailMatch.playerNumber = num;

                    mStartingUpTitle.setText("设置首发（" + mAdapter.getItemCount() + "/" + mDetailMatch.playerNumber + "）");
                } else if (type == 1) {
                    /*if (num < 1 || num > 6) {
                        editText.setError("请输入1到6的整数");
                        ToastUtil.showToast(getApplicationContext(), "请输入1到6的整数");
                        setDialogDismiss(dialog, false);
                        return;
                    }*/

                    mJieItemView.setSummaryText(num + "节");
                    mDetailMatch.totalPart = num;
                    mTotalTime.setText("比赛共" + mDetailMatch.totalPart * mDetailMatch.partMinutes + "分钟");
                } else if (type == 2) {
                    mJieTimeItemView.setSummaryText(num + "分钟");
                    mDetailMatch.partMinutes = num;
                    mTotalTime.setText("比赛共" + mDetailMatch.totalPart * mDetailMatch.partMinutes + "分钟");
                }

                mHasChanged = true;

//                mDetailMatch.save();

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
                //首先去当前数据库查一下是否有这个号码的球员了，如果有了直接将这个球员设置为首发，如果他已经是首发的话，提示用户
                int result = checkPlayer(num);
                if (result == -1) {
                    ToastUtil.showToast(getApplicationContext(), "该球员已经在首发位置上了");
                    dialog.dismiss();
                    return;
                }

                if (result == -2) {
                    //没有该球员可以新增了
                    doAdd(num, nameText);
                    mHasChanged = true;
                    dialog.dismiss();
                } else {
                    //有该球员但不是首发，将他调到首发位置上来
                    TmpPlayer player = mAllPlayerList.get(result);
                    player.isFirst = true;
                    player.isOnPitch = true;
//                    player.save();
                    mModifyPlayerIdList.add(player.playerId);

                    mAdapter.addItem(player);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

                    mStartingUpTitle.setText("设置首发（" + mAdapter.getItemCount() + "/" + mDetailMatch.playerNumber + "）");

                    mHasChanged = true;
                    dialog.dismiss();
                    return;
                }
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create().show();
    }

    private int checkPlayer(int playerNumber) {
        TmpPlayer player;
        for (int i=0;i<mAllPlayerList.size();i++) {
            player = mAllPlayerList.get(i);
            if (player.number == playerNumber) {
                    if (player.isFirst) {
                        return -1;//有该学员且已经在首发位置上了
                    } else {
                        return i;//有该学员但是不在首发位置,返回position
                    }
                }
        }
        return -2;//没有该球员
    }

    private String mAddFailString;
    private void doAdd(int playerNumber, String name) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在新增球员...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        AddPlayerRequest request = new AddPlayerRequest(getApplicationContext());
        request.matchId = mDetailMatch.matchId;
        final long teamId;
        if (mDetailMatch.teamHome.isAssiged) {
            teamId = mDetailMatch.teamHome.teamId;
        } else {
            teamId = mDetailMatch.teamVisitor.teamId;
        }
        request.teamId = teamId;

        final AddPlayerRequest.HttpPlayer httpPlayer = new AddPlayerRequest.HttpPlayer();
        httpPlayer.isFirst = false;//默认不首发
        httpPlayer.name = name;
        httpPlayer.number = Integer.valueOf(playerNumber);
        request.player = httpPlayer;

        mLadderBallApi.doAddPlayer(request)
                .map(new Func1<AddPlayerResponse, TmpPlayer>() {
                    @Override
                    public TmpPlayer call(AddPlayerResponse response) {
                        if (response.header.resultCode == 0) {
                            TmpPlayer player = new TmpPlayer();
                            player.matchId = mDetailMatch.matchId;
                            player.teamId = teamId;
                            player.playerId = response.player.id;
                            player.number = response.player.number;
                            player.name = response.player.name;
                            player.isFirst = true;
                            player.isOnPitch = true;

                            if (mTeamType == BallConstants.TEAM_HOME) {
                                mDetailMatch.teamHome.players.add(player);
                            } else {
                                mDetailMatch.teamVisitor.players.add(player);
                            }

                            Log.d("new add playerid:" + player.playerId);
                            mNewAddPlayerIdList.add(player.playerId);//这个主要是用于用户按返回键选择不保存的时候重置首发属性的
                            mModifyPlayerIdList.add(player.playerId);//
//                            mDetailMatch.save();

                            mHasChanged = true;
                            return player;
                        }
                        mAddFailString = response.header.resultText;
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TmpPlayer>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(e.toString());
                        progressDialog.cancel();
                        ToastUtil.showToast(getApplicationContext(), "网络连接超时，请重试");
                    }

                    @Override
                    public void onNext(TmpPlayer player) {
                        progressDialog.cancel();
                        if (player != null) {
                            ToastUtil.showToast(getApplicationContext(), "添加球员成功");
                            //添加成功，重新加载界面
                            mAdapter.addItem(player);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

                            mStartingUpTitle.setText("设置首发（" + mAdapter.getItemCount() + "/" + mDetailMatch.playerNumber + "）");
                        } else {
                            ToastUtil.showToast(getApplicationContext(), mAddFailString);
                        }
                    }
                });
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
                        for (TmpPlayer player: mDetailMatch.teamHome.players) {
                            if (player.isFirst) {
                                mFirstPlayerList.add(player);
                            }
                        }
                    } else if (mDetailMatch.teamVisitor.isAssiged) {
                        Log.d("teamVisitor.size:" + mDetailMatch.teamVisitor.players.size());
                        for (TmpPlayer player: mDetailMatch.teamVisitor.players) {
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
                            handleNewAddPlayer();
                        }
                    })
                    .create().show();
            return;
        }
        super.onBackPressed();
    }

    private void handleNewAddPlayer() {
        if (mNewAddPlayerIdList.size() == 0) {
            PracticeSettingActivity.this.finish();
            return;
        }

        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                //将新增球员的首发属性去除
                if (mTeamType == BallConstants.TEAM_HOME) {
                    for (TmpPlayer player: mDetailMatch.teamHome.players) {
                        for (long id: mNewAddPlayerIdList) {
//                            Log.d("has player?" + player.playerId + ",id:" + id);
                            if (player.playerId == id) {
                                player.isFirst = false;
                                player.isOnPitch = false;
                                player.save();
                            }
                        }
                    }
                } else {
                    for (TmpPlayer player: mDetailMatch.teamVisitor.players) {
                        for (long id: mNewAddPlayerIdList) {
                            if (player.playerId == id) {
                                player.isFirst = false;
                                player.isOnPitch = false;
                                player.save();
                            }
                        }
                    }
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {

                        PracticeSettingActivity.this.finish();
                    }
                });
    }


    /**
     * 保存当前设置修改过球员首发属性的球员信息到数据库
     * 主要是针对移除首发位置和添加已存在球员列表的人为首发的
     */
    private void doSaveModifyPlayerData() {
        if (mTeamType == BallConstants.TEAM_HOME) {
            for (TmpPlayer player: mDetailMatch.teamHome.players) {
                for (long id: mModifyPlayerIdList) {
                    if (player.playerId == id) {
                        player.save();
                    }
                }
            }
        } else {
            for (TmpPlayer player: mDetailMatch.teamVisitor.players) {
                for (long id: mModifyPlayerIdList) {
                    if (player.playerId == id) {
                        player.save();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(RxBusTag.PlAYER_ITEM_REMOVE, mItemObservable);
    }
}
