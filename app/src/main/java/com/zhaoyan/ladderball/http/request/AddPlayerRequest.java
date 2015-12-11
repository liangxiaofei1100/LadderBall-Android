package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/11.
 */
public class AddPlayerRequest extends BaseRequest {

    public long matchId;
    public long teamId;
    public HttpPlayer player;

    public AddPlayerRequest(Context context) {
        super(context);
    }

    public static class HttpPlayer{
        public String name;
        public int number;
        public boolean isFirst;
    }
}
