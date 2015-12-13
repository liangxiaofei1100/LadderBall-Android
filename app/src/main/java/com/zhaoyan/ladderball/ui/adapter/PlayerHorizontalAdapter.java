package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yuri on 2015/12/5.
 */
public class PlayerHorizontalAdapter extends RecyclerView.Adapter<PlayerHorizontalAdapter.PlayerViewHolder> {

    private Context mContext;
    private List<Player> mDataList;

    private LayoutInflater mInflater;

    public PlayerHorizontalAdapter(Context context, List<Player> playerList) {
        mContext = context;
        mDataList = playerList;
        mInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<Player> dataList) {
        //过滤掉非首发的
        for (Player player: dataList) {
            if (player.isFirst) {
                mDataList.add(player);
            }
        }
    }

    public void clear() {
        mDataList.clear();
    }

    public void addItem(Player player) {
        mDataList.add(mDataList.size(), player);
        notifyItemInserted(mDataList.size());
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        Player player = mDataList.get(position);
//        Log.d(">>" + player.number + ",isFirst:" + player.isFirst);
        holder.textView.setText(player.number + "号");

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

    public class PlayerViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_player_item_avatar)
        ImageView imageView;

        @Bind(R.id.tv_player_item_number)
        TextView textView;

        @Bind(R.id.iv_player_item_remove)
        ImageView removeView;

        public PlayerViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            removeView.setVisibility(View.GONE);
        }

        @OnClick(R.id.iv_player_item_remove)
        void removePlayer() {
            RxBus.get().post(RxBusTag.PlAYER_ITEM_REMOVE, getLayoutPosition());
        }

    }

}
