package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.http.response.EventPartListResponse;
import com.zhaoyan.ladderball.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class DataRepairAdapter extends RecyclerView.Adapter<DataRepairAdapter.RepairViewHolder> {

    private Context mContext;
    private List<EventPartListResponse.HttpEvent> mDataList;

    private LayoutInflater mInflater;

    public DataRepairAdapter(Context context, List<EventPartListResponse.HttpEvent> playerList) {
        mContext = context;
        mDataList = playerList;
        mInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<EventPartListResponse.HttpEvent> dataList) {
        mDataList = dataList;
    }

    public void clear() {
        mDataList.clear();
    }

    public void addItem(EventPartListResponse.HttpEvent httpEvent) {
        mDataList.add(mDataList.size(), httpEvent);
        notifyItemInserted(mDataList.size());
    }

    @Override
    public RepairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_data_repair_item, parent, false);
        return new RepairViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RepairViewHolder holder, int position) {
        EventPartListResponse.HttpEvent httpEvent = mDataList.get(position);

        holder.timeView.setText(TimeUtil.timeFormat(httpEvent.timeSecond));
        holder.numberView.setText(httpEvent.playerNumber + "");
        if (httpEvent.eventCode == EventCode.EVENT_HUAN_REN) {
            int playerNumber = getReplacePlayerNumber(httpEvent.additionalData);
            if (playerNumber == -1) {
                holder.evetNameView.setText(getEventName(httpEvent.eventCode));
            } else {
                holder.evetNameView.setText(getEventName(httpEvent.eventCode) + "(" + playerNumber + ")");
            }
        } else {
            holder.evetNameView.setText(getEventName(httpEvent.eventCode));
        }
    }

    private int getReplacePlayerNumber(String additionalData) {
        int playerNumber = -1;
        if (TextUtils.isEmpty(additionalData)) {
            return -1;
        }
        try {
            JSONObject jsonObject = new JSONObject(additionalData);
            playerNumber = jsonObject.getInt("playerNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playerNumber;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public EventPartListResponse.HttpEvent getItem(int position) {
        return mDataList.get(position);
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void changeItem(int position) {
        notifyItemChanged(position);
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

    private String getEventName(int eventCode) {
//        Log.d("eventCode:" + eventCode);
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
