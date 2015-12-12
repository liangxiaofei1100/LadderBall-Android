package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/12.
 */
public class CreatePracticeRequest extends BaseRequest{

    public String teamHomeName;
    public String teamVisitorName;
    public int playerNumber;

    public CreatePracticeRequest(Context context) {
        super(context);
    }
}
