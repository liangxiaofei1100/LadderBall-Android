package com.zhaoyan.ladderball.http.request;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/12/10.
 */
public class EventCollectionRequest extends BaseRequest {

    public List<HttpEvent> events;

    public EventCollectionRequest(Context context) {
        super(context);

        events = new ArrayList<>();
    }

    public static class HttpEvent{
        public int eventCode;
        public long matchId;
        public long teamId;
        public long playerId;
        public int partNumber;
        public long timeSecond;
        public String additionalData;
        public String uuid;
    }
}
