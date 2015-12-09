package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Event;
import com.zhaoyan.ladderball.model.Player;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class EventRecordAdapter extends RecyclerView.Adapter<EventRecordAdapter.EventRecordViewHolder> {

    private Context mContext;

    private LayoutInflater mInflater;

    private Event mPlayerEvent;

    public EventRecordAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setDataList(Event event) {
       mPlayerEvent = event;
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
    public EventRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_event_record_item, parent, false);
        return new EventRecordViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(EventRecordViewHolder holder, int position) {
        switch (position) {

        }
    }

    @Override
    public int getItemCount() {
        return 16;
    }

//    public Player getItem(int position) {
//        return mDataList.get(position);
//    }

//    public void removeItem(int position) {
//        mDataList.remove(position);
//        notifyItemRemoved(position);
//    }

    public class EventRecordViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_event_record_name)
        TextView eventNameView;
        @Bind(R.id.tv_event_record_count)
        TextView eventCountView;

        public EventRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }

}
