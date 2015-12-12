package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class DataRepairAdapter extends RecyclerView.Adapter<DataRepairAdapter.RepairViewHolder> {

    private Context mContext;
    private List<Player> mDataList;

    private LayoutInflater mInflater;

    public DataRepairAdapter(Context context, List<Player> playerList) {
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
    public RepairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_data_repair_item, parent, false);
        return new RepairViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RepairViewHolder holder, int position) {

//        Player player = mDataList.get(position);
//        Log.d(">>" + player.number + ",isFirst:" + player.isFirst);
//        holder.textView.setText(player.number + "号");

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public Player getItem(int position) {
        return mDataList.get(position);
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public class RepairViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_data_repair_item_time)
        TextView timeView;

        @Bind(R.id.tv_data_repair_item_number)
        TextView numberView;

        @Bind(R.id.tv_data_repair_item_event_name)
        TextView evetNameView;

        public RepairViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (mListener != null) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }

    }

    public OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
