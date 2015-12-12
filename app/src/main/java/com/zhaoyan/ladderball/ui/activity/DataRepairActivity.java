package com.zhaoyan.ladderball.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.ui.adapter.DataRepairAdapter;
import com.zhaoyan.ladderball.ui.adapter.OnItemClickListener;
import com.zhaoyan.ladderball.ui.dialog.BaseDialog;
import com.zhaoyan.ladderball.ui.dialog.DataRepairDialog;
import com.zhaoyan.ladderball.ui.dialog.MenuDialog;
import com.zhaoyan.ladderball.ui.view.ItemDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DataRepairActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String EXTRA_MATCH_ID = "matchId";
    private static final String EXTRA_TEAM_ID = "teamId";
    private static final String EXTRA_PART_NUMBER = "part_number";

    private long mMatchId;
    private long mTeamId;
    private int mPartNumber;

    @Bind(R.id.data_repair_recyclerview)
    RecyclerView mRecyclerView;

    DataRepairAdapter mAdapter;

    private List<String> mMenuList = new ArrayList<>();

    public static Intent getStartIntent(Context context, long matchId, long teamId, int partNumber) {
        Intent intent=  new Intent();
        intent.setClass(context, DataRepairActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, matchId);
        intent.putExtra(EXTRA_TEAM_ID, teamId);
        intent.putExtra(EXTRA_PART_NUMBER, partNumber);
        return  intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_repair);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mMatchId = intent.getLongExtra(EXTRA_MATCH_ID, -1);
        mTeamId = intent.getLongExtra(EXTRA_TEAM_ID, -1);
        mPartNumber = intent.getIntExtra(EXTRA_PART_NUMBER, -1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDivider(getApplicationContext()));

        mAdapter = new DataRepairAdapter(getApplicationContext(), new ArrayList<Player>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mMenuList.add("编辑");
        mMenuList.add("删除");
        mMenuList.add("取消");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"+")
                .setIcon(R.mipmap.ic_add_white)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            DataRepairActivity.this.finish();
        } else if (itemId == 0) {
            //add new record
            doAddItem();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(final int position) {
        new MenuDialog(this, mMenuList)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int menuPosition) {
                        if (menuPosition == 0) {
                            //edit
                            doEditItem(position);
                        } else if (menuPosition == 1) {
                            //delete
                            doDeleteItem(position);
                        } else {
                            //cancel
                        }
                    }
                })
                .show();
    }

    private void doAddItem() {
        DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_ADD);
        repairDialog.setDialogTitle("添加数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {

            }
        });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doEditItem(int position) {
        DataRepairDialog repairDialog = new DataRepairDialog(this, DataRepairDialog.TYPE_EDIT);
        repairDialog.setDialogTitle("编辑数据");
        repairDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {

                    }
                });
        repairDialog.setNegativeButton("取消", null);
        repairDialog.show();
    }

    private void doDeleteItem(int position) {
        BaseDialog deleteDialog = new BaseDialog(this);
        deleteDialog.setDialogMessage("确定删除这条数据?");
        deleteDialog.setPositiveButton("确定", new BaseDialog.onMMDialogClickListener() {
            @Override
            public void onClick(Dialog dialog) {

            }
        });
        deleteDialog.setNegativeButton("取消", null);
        deleteDialog.show();
    }
}
