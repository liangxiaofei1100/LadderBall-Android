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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yuri on 2015/12/5.
 */
public class EventRecentAdapter extends RecyclerView.Adapter<EventRecentAdapter.RecentRecordViewHolder> {

    private Context mContext;

    private LayoutInflater mInflater;

    public EventRecentAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);


    }

    public void setDataList(List<Player> dataList) {
    }

    public List<Player> getDataList() {
        return null;
    }

    public void clear() {
    }

    public void addItem(Player player) {
//        mDataList.add(mDataList.size(), player);
//        notifyItemInserted(mDataList.size());
    }

    @Override
    public RecentRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_recent_record_item, parent, false);
        return new RecentRecordViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecentRecordViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

//    public Player getItem(int position) {
//        return mDataList.get(position);
//    }

//    public void removeItem(int position) {
//        mDataList.remove(position);
//        notifyItemRemoved(position);
//    }

    public class RecentRecordViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_recent_record_remove)
        ImageView imageView;

        @Bind(R.id.tv_recent_record_number)
        TextView numberView;
        @Bind(R.id.tv_recent_record_event)
        TextView eventView;

        public RecentRecordViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);


        }

        @OnClick(R.id.iv_recent_record_remove)
        void removeEvent() {
            int position = getLayoutPosition();
            Log.d("remove:" + position);
        }

    }

}
