package com.zhaoyan.ladderballmanager.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/16.
 */
public class CreateMatchRequest extends BaseRequest {

    public String teamHomeName;
    public String teamHomeColor;
    public String teamVisitorName;
    public String teamVisitorColor;
    public int playerNumber;
    public long startTime;
    public String address;
    public int totalPart;
    public int partMinutes;

    public CreateMatchRequest(Context context) {
        super(context);
    }
}
