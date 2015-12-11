package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.model.EventItem;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.ArrayList;
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

    private List<EventItem> mDataList;


    public EventRecentAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mDataList = new ArrayList<>();
    }

    public void setDataList(List<EventItem> dataList) {
        mDataList = dataList;
    }

    public List<Player> getDataList() {
        return null;
    }

    public void clear() {
    }

    public void addItem(EventItem eventItem) {
        mDataList.add(mDataList.size(), eventItem);
        notifyItemInserted(mDataList.size());
    }

    @Override
    public RecentRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_recent_record_item, parent, false);
        return new RecentRecordViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecentRecordViewHolder holder, int position) {
        EventItem eventItem = mDataList.get(position);
        holder.numberView.setText(eventItem.playerNumber + "");
        holder.eventView.setText(getEventName(eventItem.eventCode));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

//    public Player getItem(int position) {
//        return mDataList.get(position);
//    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public class RecentRecordViewHolder extends RecyclerView.ViewHolder {

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
            RxBusTag.DataRecord dataRecord = new RxBusTag.DataRecord();
            dataRecord.itemType = RxBusTag.DataRecord.ITEM_EVENT_RECENT_REMOVE;
            dataRecord.position = position;
            RxBus.get().post(RxBusTag.DATA_RECORD_ACTIVITY, dataRecord);
        }

    }

    private String getEventName(int eventCode) {
        Log.d("eventCode:" + eventCode);
        String eventName = "";
        switch (eventCode) {
            case EventCode.EVENT_JIN_QIU:
                eventName = "进球";
                break;
            case EventCode.EVENT_ZHU_GONG:
                eventName = "助攻";
                break;
            case EventCode.EVENT_JIAO_QIU:
                eventName = "角球";
                break;
            case EventCode.EVENT_REN_YI_QIU:
                eventName = "任意球";
                break;
            case EventCode.EVENT_BIAN_JIE_QIU:
                eventName = "边界球";
                break;
            case EventCode.EVENT_YUE_WEI:
                eventName = "越位";
                break;
            case EventCode.EVENT_SHI_WU:
                eventName = "失误";
                break;
            case EventCode.EVENT_GUO_REN_CHENG_GONG:
                eventName = "过人成功";
                break;
            case EventCode.EVENT_GUO_REN_SHI_BAI:
                eventName = "过人失败";
                break;
            case EventCode.EVENT_SHE_ZHENG:
                eventName = "射正";
                break;
            case EventCode.EVENT_SHE_PIAN:
                eventName = "射偏";
                break;
            case EventCode.EVENT_SHE_MEN_BEI_DU:
                eventName = "射门被封堵";
                break;
            case EventCode.EVENT_CHUAN_QIU_CHENG_GONG:
                eventName = "传球成功";
                break;
            case EventCode.EVENT_WEI_XIE_QIU:
                eventName = "威胁球";
                break;
            case EventCode.EVENT_CHUAN_QIU_SHI_BAI:
                eventName = "传球失败";
                break;
            case EventCode.EVENT_FENG_DU_SHE_MEN:
                eventName = "封堵射门";
                break;
            case EventCode.EVENT_LAN_JIE:
                eventName = "拦截";
                break;
            case EventCode.EVENT_QIANG_DUAN:
                eventName = "抢断";
                break;
            case EventCode.EVENT_JIE_WEI:
                eventName = "解围";
                break;
            case EventCode.EVENT_BU_JIU_SHE_MEN:
                eventName = "扑救射门";
                break;
            case EventCode.EVENT_BU_JIU_DAN_DAO:
                eventName = "单刀";
                break;
            case EventCode.EVENT_SHOU_PAO_QIU:
                eventName = "手抛球";
                break;
            case EventCode.EVENT_QIU_MEN_QIU:
                eventName = "球门球";
                break;
            case EventCode.EVENT_HUANG_PAI:
                eventName = "黄牌";
                break;
            case EventCode.EVENT_HONG_PAI:
                eventName = "红牌";
                break;
            case EventCode.EVENT_FAN_GUI:
                eventName = "犯规";
                break;
            case EventCode.EVENT_WU_LONG_QIU:
                eventName = "乌龙球";
                break;
            case EventCode.EVENT_HUAN_REN:
                eventName = "换人";
                break;
        }
        return  eventName;
    }

}
