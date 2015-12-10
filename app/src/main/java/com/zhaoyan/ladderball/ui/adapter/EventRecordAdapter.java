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
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

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

    private String[] mEventsName;

    public EventRecordAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mEventsName = context.getResources().getStringArray(R.array.events_name);
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

        if (mPlayerEvent == null) {
            return;
        }

        holder.eventNameView.setText(mEventsName[position]);
//        Log.d("position:" + position + "mplayerevent:" + mPlayerEvent.toString());
        String eventNum = "";
        switch (position) {
            case 0:
                eventNum = mPlayerEvent.jinQiu + "/" + mPlayerEvent.zhuGong;
                break;
            case 1:
                eventNum = mPlayerEvent.jiaoQiu + "/" + mPlayerEvent.renYiQiu + "/" + mPlayerEvent.bianJieQiu;
                break;
            case 2:
                eventNum = mPlayerEvent.yueWei + "/" + mPlayerEvent.shiWu;
                break;
            case 3:
                eventNum = mPlayerEvent.guoRenChengGong + "/" + mPlayerEvent.guoRenShiBai;
                break;
            case 4:
                eventNum = mPlayerEvent.sheZheng + "/" + mPlayerEvent.shePian + "/" + mPlayerEvent.sheMenBeiDu;
                break;
            case 5:
                eventNum = mPlayerEvent.chuanQiuChengGong + "";
                break;
            case 6:
                eventNum = mPlayerEvent.weiXieQiu + "";
                break;
            case 7:
                eventNum = mPlayerEvent.chuanQiuShiBai + "";
                break;
            case 8:
                eventNum = mPlayerEvent.fengDuSheMen + "";
                break;
            case 9:
                eventNum = mPlayerEvent.lanJie + "";
                break;
            case 10:
                eventNum = mPlayerEvent.qiangDuan + "";
                break;
            case 11:
                eventNum = mPlayerEvent.jieWei + "";
                break;
            case 12:
                eventNum = mPlayerEvent.puJiuSheMen + "/" + mPlayerEvent.danDao;
                break;
            case 13:
                eventNum = mPlayerEvent.shouPaoQiu + "/" + mPlayerEvent.qiuMenQiu;
                break;
            case 14:
                eventNum = mPlayerEvent.huangPai + "/" + mPlayerEvent.hongPai + "/"
                        + mPlayerEvent.fanGui + "/" + mPlayerEvent.wuLongQiu;
                break;
            case 15:
                //功能按钮没有数字
                eventNum = "这是功能";
//                holder.eventCountView.setVisibility(View.GONE);
                break;
            default:
                eventNum = "默认";
                break;
        }
        holder.eventCountView.setText(eventNum);
    }

    @Override
    public int getItemCount() {
        return mEventsName.length;//最后一个是功能按钮
    }

    public Event getItem() {
        return mPlayerEvent;
    }

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Log.d("position:" + position);
//                    ToastUtil.showToast(mContext, "Click " + position);
                    switch (position) {
                        case 0:
//                            mPlayerEvent.jinQiu += 1;
//                            mPlayerEvent.zhuGong += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 1:
//                            mPlayerEvent.jiaoQiu += 1;
//                            mPlayerEvent.renYiQiu += 1;
//                            mPlayerEvent.bianJieQiu += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 2:
//                            mPlayerEvent.yueWei += 1;
//                            mPlayerEvent.shiWu += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 3:
//                            mPlayerEvent.guoRenChengGong += 1;
//                            mPlayerEvent.guoRenShiBai += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 4:
//                            mPlayerEvent.sheZheng += 1;
//                            mPlayerEvent.shePian += 1;
//                            mPlayerEvent.sheMenBeiDu += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 5:
                            mPlayerEvent.chuanQiuChengGong += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 6:
                            mPlayerEvent.weiXieQiu += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 7:
                            mPlayerEvent.chuanQiuShiBai += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 8:
                            mPlayerEvent.fengDuSheMen += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 9:
                            mPlayerEvent.lanJie += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 10:
                            mPlayerEvent.qiangDuan += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 11:
                            mPlayerEvent.jieWei += 1;
                            doChangeEvent(mPlayerEvent, position);
                            break;
                        case 12:
//                            mPlayerEvent.puJiuSheMen += 1;
//                            mPlayerEvent.danDao += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 13:
//                            mPlayerEvent.shouPaoQiu += 1;
//                            mPlayerEvent.qiuMenQiu += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 14:
//                            mPlayerEvent.huangPai += 1;
//                            mPlayerEvent.hongPai += 1;
//                            mPlayerEvent.fanGui += 1;
//                            mPlayerEvent.wuLongQiu += 1;
//                            doChangeEvent(mPlayerEvent, position);
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                        case 15:
                            //功能按钮没有数字
                            RxBus.get().post(RxBusTag.EVENT_RECORD_ITEM, position);
                            break;
                    }
                }
            });
        }

        private void doChangeEvent(Event event, int position) {
            if (event == null) {
                //
            } else {
                event.save();
            }
            notifyItemChanged(position);
        }

    }

}
