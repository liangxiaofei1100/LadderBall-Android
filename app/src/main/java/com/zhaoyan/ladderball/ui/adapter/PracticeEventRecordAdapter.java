package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.TmpPlayerEvent;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class PracticeEventRecordAdapter extends RecyclerView.Adapter<PracticeEventRecordAdapter.EventRecordViewHolder> {

    private Context mContext;

    private LayoutInflater mInflater;

    private TmpPlayerEvent mPlayerEvent;

    private String[] mEventsName;

    public PracticeEventRecordAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mEventsName = context.getResources().getStringArray(R.array.events_name);
    }

    public void setDataList(TmpPlayerEvent playerEvent) {
        mPlayerEvent = playerEvent;
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

//        Log.d("position:" + position);
        holder.eventNameView.setText(mEventsName[position]);
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
//                Log.d("shezheng:eventNum:" + eventNum);
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
//                Log.d(">>>>>>>>>>>>");
                break;
            default:
                Log.d("default");
                break;
        }
//        Log.d("eventNum:" + eventNum);
        holder.eventCountView.setText(eventNum);
    }

    private void setEventCountText(String text, TextView textView) {
        textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return mEventsName.length;//最后一个是功能按钮
    }

    public TmpPlayerEvent getItem() {
        return mPlayerEvent;
    }

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
                    postEvent(position);
                }
            });
        }

        private void postEvent(int position) {
            RxBusTag.PracticeDataRecord dataRecord = new RxBusTag.PracticeDataRecord();
            dataRecord.itemType = RxBusTag.PracticeDataRecord.ITEM_EVENT_RECORD_CLICK;
            dataRecord.position = position;
            RxBus.get().post(RxBusTag.PRACTICE_DATA_RECORD_ACTIVITY, dataRecord);
        }
    }

}
