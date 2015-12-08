package com.zhaoyan.ladderball.http.request;

import android.content.Context;

import com.zhaoyan.ladderball.http.response.MatchDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/12/8.
 */
public class MatchModifyRequest extends BaseRequest {

    public long matchId;
    public int playerNumber;
    public int totalPart;
    public int partMinutes;

    public List<MatchDetailResponse.HttpPlayer> players;


    public MatchModifyRequest(Context context) {
        super(context);

        players = new ArrayList<>();
    }
}
