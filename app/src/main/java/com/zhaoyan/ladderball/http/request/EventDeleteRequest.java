package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * 数据修复之删除一条数据记录
 * Created by Yuri on 2015/12/15.
 */
public class EventDeleteRequest extends BaseRequest {

    public long eventId;

    public EventDeleteRequest(Context context) {
        super(context);
    }
}
