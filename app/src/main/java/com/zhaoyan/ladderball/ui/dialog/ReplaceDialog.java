package com.zhaoyan.ladderball.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.model.Player;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhaoyan.ladderball.R.id.tv_replace_dialog_empty;

/**
 * Created by Yuri on 2015/12/11.
 */
public class ReplaceDialog extends BaseDialog implements BaseDialog.OnMenuClickListener {

    Context mContext;
    RecyclerView mRecyclerView;

    ReplaceAdapter mAdapter;

    TextView mEmptyView;

    List<Player> mPlayerList;

    public ReplaceDialog(Context context, List<Player> playerList) {
        super(context);

        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_replace_dialog, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.replace_recyclerview);
        mEmptyView= (TextView) view.findViewById(tv_replace_dialog_empty);
        setDialogTitle("换人");
        setRightMenu("新增球员");

        mPlayerList = playerList;

        if (mPlayerList.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        setCustomView(view);
    }

    public OnAddNewPlayerClickListener mListener;
    public void setOnAddNewClickListener(OnAddNewPlayerClickListener listener) {
        mListener =  listener;
    }

    @Override
    public void onMenuClick() {
        if (mListener != null) {
            mListener.onAddNew();
        }
    }

    public interface OnAddNewPlayerClickListener{
        void onAddNew();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ReplaceAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        setOnMenuClickListener(this);
    }

    public Player getSelectPlayer() {
        if (mAdapter.getSelectPosition() == -1) {
            return null;
        }

        return mPlayerList.get(mAdapter.getSelectPosition());
    }


    public class ReplaceAdapter extends RecyclerView.Adapter<ReplaceAdapter.ReplaceViewHolder> {

        LayoutInflater inflater;

        private int mSelectPosition = -1;

        public ReplaceAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ReplaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.layout_replace_dialog_item, null);
            return new ReplaceViewHolder(view);
        }

        public int getSelectPosition() {
            return mSelectPosition;
        }

        @Override
        public void onBindViewHolder(ReplaceViewHolder holder, int position) {

            Player player = mPlayerList.get(position);
            holder.numberView.setText(player.number + "");
            if (mSelectPosition == position) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mPlayerList.size();
        }

        public class ReplaceViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tv_replace_dialog_number)
            TextView numberView;
            @Bind(R.id.rb_replace_dialog)
            RadioButton radioButton;

            public ReplaceViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.rb_replace_dialog)
            void onRadioButtonClick(View view) {
                mSelectPosition = getLayoutPosition();
                notifyDataSetChanged();
            }
        }
    }

}
