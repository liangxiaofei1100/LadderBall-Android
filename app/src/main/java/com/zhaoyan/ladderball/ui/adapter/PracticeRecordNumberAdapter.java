package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.TmpPlayer;
import com.zhaoyan.ladderball.util.DensityUtil;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/5.
 */
public class PracticeRecordNumberAdapter extends RecyclerView.Adapter<PracticeRecordNumberAdapter.SingleChooseViewHolder> {

    private Context mContext;
    private List<TmpPlayer> mDataList;

    private LayoutInflater mInflater;

    private int mPlayerNum;

    private int mLastSelectPosition = 0;
        private int mSelectPosition = 0;
    private SparseBooleanArray mCheckedArray;

    private int mItemHeight;
    private int mDividerHeight;

    public PracticeRecordNumberAdapter(Context context, List<TmpPlayer> playerList) {
        mContext = context;
        mDataList = playerList;
        mInflater = LayoutInflater.from(context);

        mCheckedArray = new SparseBooleanArray(playerList.size());
        for (int i = 0; i < playerList.size(); i++) {
            mCheckedArray.put(i, playerList.get(i).isFirst);
        }

        init(context);
    }

    private void init(Context context) {
        //计算Item宽度和高度
//第一部算出子item的基本高端，以8个player为基准
        //得到横屏状态下屏幕的高端
        float height = DensityUtil.getHeightInPx(context);
        Log.d("height:" + height);

        int statusBarHeight = DensityUtil.getStatusBarHeight(context.getResources());
        Log.d("statusBarHeight:" + statusBarHeight);

        int column;
        if (mDataList.size() <= 8) {
            column = 4;
        } else {
            Log.d("numbers:" + mDataList.size());
            double size = mDataList.size() / 2.0;
            Log.d("ceil:" + size);
            column = (int) Math.ceil(size);
        }

        mItemHeight = (int) ((height - statusBarHeight) / column);

        int dividerHeight = DensityUtil.dip2px(context, 1);

//        mItemHeight = mItemHeight - 1 * 2 * column;
        Log.d("itemHeight:" + mItemHeight + ",column:" + column);

    }

    public void setDataList(List<TmpPlayer> dataList) {
        mDataList = dataList;
    }

    public void setSelectPosition(int position) {
        mSelectPosition = position;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public List<TmpPlayer> getDataList() {
        return mDataList;
    }

    public void changeItem(int position, TmpPlayer player) {
        mDataList.remove(position);
        mDataList.add(position, player);
        notifyItemChanged(position);
    }

    public void clear() {
        mDataList.clear();
    }

    @Override
    public SingleChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.layout_record_player_number_item, parent, false);
        return new SingleChooseViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(SingleChooseViewHolder holder, int position) {

        TmpPlayer player = mDataList.get(position);
//        Log.d(">>" + player.number + ",isFirst:" + player.isFirst + ",isOnPitch:" + player.isOnPitch);
        holder.numberView.setText(player.number + "");

        if (mSelectPosition == position) {
            holder.cardView.setCardBackgroundColor(Color.RED);
            holder.numberView.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.numberView.setTextColor(Color.BLACK);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public TmpPlayer getItem(int position) {
        return mDataList.get(position);
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public long getSelectItemId() {
        return  mDataList.get(mSelectPosition).playerId;
    }

    public class SingleChooseViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.playerCardView)
        CardView cardView;

        @Bind(R.id.tv_event_player_number)
        TextView numberView;

        public SingleChooseViewHolder(View itemView) {
            super(itemView);

//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mItemHeight);
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = mItemHeight;
            layoutParams.height = mItemHeight;
            itemView.setLayoutParams(layoutParams);

            CardView cardView  = (CardView) itemView;
            cardView.setCardElevation(4);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Log.d("position:" + position);

                    mLastSelectPosition = mSelectPosition;
                    setSelectPosition(position);
                    notifyItemChanged(mLastSelectPosition);
                    notifyItemChanged(position);

                    RxBusTag.PracticeDataRecord dataRecord = new RxBusTag.PracticeDataRecord();
                    dataRecord.itemType = RxBusTag.PracticeDataRecord.ITEM_PLAYER_CLICK;
                    dataRecord.position = position;
                    RxBus.get().post(RxBusTag.PRACTICE_DATA_RECORD_ACTIVITY, dataRecord);
                }
            });
        }

    }

}
