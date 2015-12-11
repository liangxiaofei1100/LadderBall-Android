package com.zhaoyan.ladderball.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.request.AddPlayerRequest;
import com.zhaoyan.ladderball.http.response.AddPlayerResponse;
import com.zhaoyan.ladderball.http.response.ResponseHeader;
import com.zhaoyan.ladderball.model.Match;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.PlayerChooseAdapter;
import com.zhaoyan.ladderball.ui.dialog.AddPlayerDialog;
import com.zhaoyan.ladderball.ui.dialog.BaseDialog;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

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

public class ChooseStartingUpPlayerActivity extends BaseActivity {

    public static final String EXTRA_MATCH_ID = "matchId";
    public static final String EXTRA_TEAM_ID = "teamId";
    public static final String EXTRA_TEAM_NAME = "team_name";
    public static final String EXTRA_MATCH_PLAYER_NUMBER = "match_player_number";

    @Bind(R.id.tv_player_choose_title)
    TextView mTitleView;
    @Bind(R.id.tv_player_choose_players)
    TextView mPlayersView;

    @Bind(R.id.player_choose_recyclerview)
    RecyclerView mRecyclerView;

    PlayerChooseAdapter mAdapter;

    List<Player> mPlayerList = new ArrayList<>();

    private int mPlayerNum = -1;
    private String mTeamName;

    private Match mMatch;

    private Observable<String> mItemObserable;
    private long mMatchId;
    private long mTeamId;

    public static Intent getStartIntent(Context context, long matchId, long teamId, int playerNum, String teamName) {
        Intent intent = new Intent();
        intent.setClass(context, ChooseStartingUpPlayerActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_MATCH_PLAYER_NUMBER, playerNum);
        intent.putExtra(EXTRA_TEAM_NAME, teamName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_starting_up_player);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mMatchId = intent.getLongExtra(EXTRA_MATCH_ID, -1);
        mTeamId = intent.getLongExtra(EXTRA_TEAM_ID, -1);
        mPlayerNum = intent.getIntExtra(EXTRA_MATCH_PLAYER_NUMBER, -1);
        mTeamName = intent.getStringExtra(EXTRA_TEAM_NAME);

        Log.d("matchId:" + mMatchId + ",teamId:" + mTeamId + ",mPlayerNum:" + mPlayerNum
                + "teamName:" + mTeamName);

        mTitleView.setText("当前队伍为：" + mTeamName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new PlayerChooseAdapter(ChooseStartingUpPlayerActivity.this, new ArrayList<Player>(), mPlayerNum);
        mRecyclerView.setAdapter(mAdapter);

        initData(false);

        mItemObserable = RxBus.get().register(RxBusTag.PLAYER_CHOOSE_CHANGE, String.class);
        mItemObserable.map(new Func1<String, String[]>() {
            @Override
            public String[] call(String s) {
//                Log.d("s:" + s);
                String[] results = new String[2];
                List<Integer> firstCountList = mAdapter.getFirstPlayerNumber();
                results[0] = firstCountList.size() + "";
                String choosedPlayers = "";
                for (int i = 0; i < firstCountList.size(); i++) {
//                    Log.d("i:" + i  + ",number:" + firstCountList.get(i));
                    if (i == firstCountList.size() - 1) {
                        choosedPlayers += firstCountList.get(i) + "";
                    } else {
                        choosedPlayers += firstCountList.get(i) + "、";
                    }
                }
                results[1] = choosedPlayers;
                return results;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String[]>() {
                    @Override
                    public void call(String[] results) {
//                        Log.d("results:" + results[0] + "," +results[1]);
                        mTitleView.setText("当前队伍为：" + mTeamName + "，已选首发(" + results[0]
                                + "/" + mPlayerNum + ")人");
                        mPlayersView.setText("首发球员：" + results[1]);
                    }
                });

        setResult(RESULT_CANCELED);
    }

    private void initData(final boolean needScrollToEnd) {
        Observable<List<Player>> observable = doGetPlayer(mMatchId, mTeamId)
                .subscribeOn(Schedulers.io());

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Player>>() {
                    @Override
                    public void onCompleted() {
                        Log.d();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(e);
                        ToastUtil.showToast(getApplicationContext(), "查询数据失败");
                    }

                    @Override
                    public void onNext(List<Player> playerList) {
                        Log.d("playlist.size:" + playerList.size());
                        mAdapter.setDataList(playerList);
                        mAdapter.notifyDataSetChanged();

                        if (needScrollToEnd) {
                            mRecyclerView.scrollToPosition(playerList.size() - 1);
                        }

                        List<Integer> firstCountList = mAdapter.getFirstPlayerNumber();
                        mTitleView.setText("当前队伍为：" + mTeamName + "，首发球员(" + firstCountList.size()
                                + "/" + mPlayerNum + ")人");
                        String choosedPlayers = "";
                        for (Integer num : firstCountList) {
                            choosedPlayers += num + "、";
                        }
                        mPlayersView.setText("首发球员：" + choosedPlayers);
                    }
                });
    }

    private Observable<List<Player>> doGetPlayer(final long matchId, final long teamId) {
        return Observable.create(new Observable.OnSubscribe<List<Player>>() {
            @Override
            public void call(Subscriber<? super List<Player>> subscriber) {
//                List<Player> playerList = new Select().from(Player.class).where("matchId=? and teamId=?",
//                        matchId, teamId).execute();

                mMatch = new Select().from(Match.class).where("matchId=?", matchId).executeSingle();

                if (mMatch.teamHome.isAssiged) {
                    subscriber.onNext(mMatch.teamHome.players);
                } else {
                    subscriber.onNext(mMatch.teamVisitor.players);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "新增球员").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == 0) {
            showAddNewDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_player_choose_save)
    void doSavePlayerChoose() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("保存中...");
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Player player;
                List<Player> playerList = mAdapter.getDataList();
                for (int i = 0; i < playerList.size(); i++) {
                    Log.d("number:" + playerList.get(i).number + ",isFirst:" + playerList.get(i).isFirst);
                    player = playerList.get(i);
                    player.save();
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        progressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(e);
                        progressDialog.cancel();
                        ToastUtil.showToast(getApplicationContext(), "保存失败，请重试");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        progressDialog.cancel();
                        setResult(RESULT_OK);
                        ToastUtil.showToast(getApplicationContext(), "保存成功");
                        ChooseStartingUpPlayerActivity.this.finish();
                    }
                });

    }

    private void showAddNewDialog() {
        final AddPlayerDialog addDialog = new AddPlayerDialog(this);
        addDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                String playerNumber = addDialog.getPlayerNumber();
                if (playerNumber.isEmpty()) {
                    addDialog.setNumberErrStr("必须输入一个号码");
                    return;
                }

                String name = addDialog.getPlayerName();

                doAdd(playerNumber, name);

                dialog.dismiss();
            }
        });
        addDialog.setNegativeButton("取消", null);
        addDialog.show();
    }

    private void doAdd(String playerNumber, String name) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在新增球员...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        AddPlayerRequest request = new AddPlayerRequest(getApplicationContext());
        request.matchId = mMatchId;
        request.teamId = mTeamId;

        final AddPlayerRequest.HttpPlayer httpPlayer = new AddPlayerRequest.HttpPlayer();
        httpPlayer.isFirst = false;//默认不首发
        httpPlayer.name = name;
        httpPlayer.number = Integer.valueOf(playerNumber);
        request.player = httpPlayer;

        mLadderBallApi.doAddPlayer(request)
                .map(new Func1<AddPlayerResponse, ResponseHeader>() {
                    @Override
                    public ResponseHeader call(AddPlayerResponse response) {
                        if (response.header.resultCode == 0) {
                            Player player = new Player();
                            player.matchId = mMatchId;
                            player.teamId = mTeamId;
                            player.playerId = response.player.id;
                            player.number = response.player.number;
                            player.name = response.player.name;
                            player.isFirst = response.player.isFirst;

                            if (mMatch.teamHome.isAssiged) {
                                mMatch.teamHome.players.add(player);
                            } else {
                                mMatch.teamVisitor.players.add(player);
                            }
                            mMatch.save();
                        }
                        return response.header;
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
                .subscribe(new Observer<ResponseHeader>() {
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
                    public void onNext(ResponseHeader responseHeader) {
                        progressDialog.cancel();
                        if (responseHeader.resultCode == 0) {
                            ToastUtil.showToast(getApplicationContext(), "添加球员成功");
                            //添加成功，重新加载界面
                            initData(true);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), responseHeader.resultText);
                        }
                    }
                });
    }
}
