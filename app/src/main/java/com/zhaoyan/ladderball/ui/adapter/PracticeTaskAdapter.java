package com.zhaoyan.ladderball.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.PracticeTask;
import com.zhaoyan.ladderball.util.Log;
import com.zhaoyan.ladderball.util.TimeUtil;
import com.zhaoyan.ladderball.util.rx.RxBus;
import com.zhaoyan.ladderball.util.rx.RxBusTag;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/4.
 */
public class PracticeTaskAdapter extends RecyclerView.Adapter<PracticeTaskAdapter.TaskViewHoder> {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<PracticeTask> mDataList;

    public PracticeTaskAdapter(Context context, List<PracticeTask> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataList = list;
    }

    public void setDataList(List<PracticeTask> list) {
        Log.d(list.size() + "");
        mDataList = list;
    }

    public List<PracticeTask> getDataList() {
        return mDataList;
    }

    public void addDataToList(PracticeTask item) {
        mDataList.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public PracticeTaskAdapter.TaskViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHoder(rootView);
    }

    @Override
    public void onBindViewHolder(PracticeTaskAdapter.TaskViewHoder holder, final int position) {

        PracticeTask task = getItem(position);

        if (TextUtils.isEmpty(task.mTeamHomeColor)) {
            holder.homeTeamNameColor.setText(task.mTeamHomeName);
        } else {
            holder.homeTeamNameColor.setText(task.mTeamHomeName + "\n" + "(" + task.mTeamHomeColor + ")");
        }

        if (TextUtils.isEmpty(task.mTeamVisitorColor)) {
            holder.visitorTeamNameColor.setText(task.mTeamVisitorName);
        } else {
            holder.visitorTeamNameColor.setText(task.mTeamVisitorName + "\n" + "(" + task.mTeamVisitorColor + ")");
        }

        if (task.mIsComplete) {
            holder.score.setText(task.mTeamHomeScore + ":" + task.mTeamVisitorScore);
        } else {
            holder.score.setText("VS");
        }
        holder.rule.setText(task.mPlayerNum + "人制");
        holder.date.setText(TimeUtil.getFormatterDate(task.mStartTime));
        holder.address.setText(task.mAddress);

        holder.homeTeamStatus.setVisibility(task.mTeamHomeIsAssigned ? View.VISIBLE : View.GONE);
        holder.visitorTeamStatus.setVisibility(task.mTeamVisitorIsAssigned ? View.VISIBLE : View.GONE);

        Picasso.with(mContext)
                .load(task.mTeamHomeLogoUrl)
                .resize(96, 96)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.homeTeamIcon);

        Picasso.with(mContext)
                .load(task.mTeamVisitorLogoUrl)
                .resize(96, 96)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.visitorTeamIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.get().post(RxBusTag.PRACTICE_ITEM_CLICK, position);
            }
        });
    }

    public PracticeTask getItem(int position) {
        return  mDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class TaskViewHoder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_task_item_home_team_icon)
        ImageView homeTeamIcon;
        @Bind(R.id.iv_task_item_visitor_team_icon)
        ImageView visitorTeamIcon;

        @Bind(R.id.tv_task_item_home_team_name_color)
        TextView homeTeamNameColor;
        @Bind(R.id.tv_task_item_visitor_team_name_color)
        TextView visitorTeamNameColor;

        @Bind(R.id.tv_task_item_score)
        TextView score;
        @Bind(R.id.tv_task_item_rule)
        TextView rule;

        @Bind(R.id.tv_task_item_date)
        TextView date;
        @Bind(R.id.tv_task_item_address)
        TextView address;

        @Bind(R.id.tv_task_item_home_team_status)
        TextView homeTeamStatus;
        @Bind(R.id.tv_task_item_visitor_team_status)
        TextView visitorTeamStatus;

        public TaskViewHoder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
