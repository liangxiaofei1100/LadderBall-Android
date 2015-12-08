package com.zhaoyan.ladderball.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.util.DensityUtil;
import com.zhaoyan.ladderball.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuri on 2015/12/7.
 */
public class DataRecordPlayerLayout extends FrameLayout implements View.OnClickListener {

    private List<Integer> mPlayNumbers = new ArrayList<>();

    private HashMap<Integer, TextView> mViews = new HashMap<>();

    public DataRecordPlayerLayout(Context context) {
        super(context);
    }

    public DataRecordPlayerLayout(Context context, List<Integer> playerNum) {
        super(context);
        mPlayNumbers = playerNum;
        initView(context);
    }

    private void initView(Context context) {

        //第一部算出子item的基本高端，以8个player为基准
        //得到横屏状态下屏幕的高端
        float height = DensityUtil.getHeightInPx(context);
        Log.d("height:" + height);

        int statusBarHeight = DensityUtil.getStatusBarHeight(getResources());
        Log.d("statusBarHeight:" + statusBarHeight);

        int column;
        if (mPlayNumbers.size() <= 8) {
            column = 4;
        } else {
            Log.d("numbers:" + mPlayNumbers.size());
            double size = mPlayNumbers.size() / 2.0;
            Log.d("ceil:" + size);
            column = (int) Math.ceil(size);
        }

        int itemHeight = (int) ((height - statusBarHeight) / column);
        ;
        Log.d("itemHeight:" + itemHeight + ",column:" + column);
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemHeight * 2, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams itemParams = new ViewGroup.LayoutParams(itemHeight * 2, itemHeight);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(itemHeight, itemHeight);

        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView;
        TextView textOneView;
        TextView textTwoView;

        int currentColumn;
        for (int i = 0; i <= column; i++) {
            Log.d("i:" + i);
            currentColumn = i * 2;
            Log.d("currentColumn:" + currentColumn);
            itemView = inflater.inflate(R.layout.layout_data_record_player, null);
            itemView.setLayoutParams(itemParams);

            textOneView = (TextView) itemView.findViewById(R.id.tv_player_item_one);
            textOneView.setLayoutParams(textParams);
            textTwoView = (TextView) itemView.findViewById(R.id.tv_player_item_two);
            textTwoView.setLayoutParams(textParams);

            int firstPlayer = -1;
            int secondPlayer = -1;
            try {
                firstPlayer = mPlayNumbers.get(currentColumn);
                secondPlayer = mPlayNumbers.get(currentColumn + 1);
            } catch (IndexOutOfBoundsException e) {
            }

            Log.d("firstPlayer:" + firstPlayer);
            Log.d("secondPlayer:" + secondPlayer);

            if (firstPlayer != -1) {
                textOneView.setId(firstPlayer);
                textOneView.setOnClickListener(this);
                textOneView.setText(firstPlayer + "");
                mViews.put(firstPlayer, textOneView);
            }

            if (secondPlayer != -1) {
                textTwoView.setId(secondPlayer);
                textTwoView.setOnClickListener(this);
                textTwoView.setText(secondPlayer + "");
                mViews.put(secondPlayer, textTwoView);
            }

            textOneView.setBackgroundResource(R.drawable.record_player_bg);
            textTwoView.setBackgroundResource(R.drawable.record_player_bg);

            linearLayout.addView(itemView);
        }
        addView(linearLayout);
    }

    @Override
    public void onClick(View v) {
        Log.d("id:" + v.getId());
        v.setSelected(true);

        int id = v.getId();

        TextView textView;
        int playerNum;
        for (int i = 0; i < mPlayNumbers.size(); i++ ) {
            playerNum = mPlayNumbers.get(i);
            textView = mViews.get(playerNum);
            if (playerNum == id) {
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }
        }

    }
}