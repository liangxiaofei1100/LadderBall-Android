package com.zhaoyan.ladderball.http.request;

import android.content.Context;

import com.zhaoyan.ladderball.http.response.PracticeDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 练习赛任务修改请求
 * Created by Yuri on 2015/12/8.
 */
public class PracticeModifyRequest extends BaseRequest {

    public long matchId;
    public int playerNumber;
    public int totalPart;
    public int partMinutes;

    public List<PracticeDetailResponse.HttpPlayer> players;


    public PracticeModifyRequest(Context context) {
        super(context);

        players = new ArrayList<>();
    }
}
