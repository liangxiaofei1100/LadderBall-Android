package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.ToastUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class PlayerChooseAdapter extends RecyclerView.Adapter<PlayerChooseAdapter.PlayerChooseViewHolder> {

    private Context mContext;
    private List<Player> mDataList;

    private LayoutInflater mInflater;

    private int mPlayerNum;

    private SparseBooleanArray mCheckedArray;

    public PlayerChooseAdapter(Context context, List<Player> playerList, int playerNum) {
        mContext = context;
        mDataList = playerList;
        mInflater = LayoutInflater.from(context);

        mPlayerNum = playerNum;

        mCheckedArray = new SparseBooleanArray(playerList.size());
        for (int i = 0; i < playerList.size(); i++) {
            mCheckedArray.put(i, playerList.get(i).isFirst);
        }
    }

    public void setDataList(List<Player> dataList) {
        mDataList = dataList;
    }

    public List<Player> getDataList() {
        return mDataList;
    }

    public void clear() {
        mDataList.clear();
    }

    public List<Integer> getFirstPlayerNumber() {
        int count = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (mCheckedArray.get(i)) {
                count++;
                list.add(mDataList.get(i).number);
            }
        }
        return list;
    }

    public boolean canChoosePlayer() {
        int count = 0;
        for (int i = 0; i < mCheckedArray.size(); i++) {
            if (mCheckedArray.get(i)) {
                count++;
            }
        }
        return !(count >= mPlayerNum);
    }

    public void addItem(Player player) {
        mDataList.add(mDataList.size(), player);
        notifyItemInserted(mDataList.size());
    }

    @Override
    public PlayerChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_player_choose_item, parent, false);
        return new PlayerChooseViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(PlayerChooseViewHolder holder, int position) {

        Player player = mDataList.get(position);
        Log.d(">>" + player.number + ",isFirst:" + player.isFirst);
        holder.numberView.setText(player.number + "号");
        if (!TextUtils.isEmpty(player.name)) {
            holder.nameView.setText(player.name);
        }

        if (mCheckedArray.get(position)) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public Player getItem(int position) {
        return mDataList.get(position);
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public class PlayerChooseViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_player_choose_avatar)
        ImageView imageView;

        @Bind(R.id.tv_player_choose_number)
        TextView numberView;

        @Bind(R.id.tv_player_choose_name)
        TextView nameView;

        @Bind(R.id.cb_player_choose)
        CheckBox mCheckBox;

        public PlayerChooseViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getLayoutPosition();
                    Log.d("position:" + position);

                    if (isChecked && !canChoosePlayer()) {
                        ToastUtil.showToast(mContext, "最多选择" + mPlayerNum + "位首发球员");
                        mCheckBox.setChecked(false);
                        return;
                    }

                    mDataList.get(position).isFirst = isChecked;
                    mCheckedArray.put(position, isChecked);
                    RxBus.get().post(RxBusTag.PLAYER_CHOOSE_CHANGE, "" + position);
                }
            });
        }

    }

}
