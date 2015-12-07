package com.zhaoyan.ladderball.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.ui.view.DataRecordPlayerLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DataRecoderActivity extends AppCompatActivity {

    @Bind(R.id.fl_players)
    FrameLayout mPlayersLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_recoder);

        ButterKnife.bind(this);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i <11; i++) {
            numbers.add(i);
        }
        DataRecordPlayerLayout playerLayout = new DataRecordPlayerLayout(this, numbers);
        mPlayersLayout.addView(playerLayout);
    }

}
