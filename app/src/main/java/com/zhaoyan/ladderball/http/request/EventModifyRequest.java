package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * 数据记录事件修改请求（用于修改一条数据）
 * Created by Yuri on 2015/12/15.
 */
public class EventModifyRequest extends BaseRequest {

    public long id;//事件id
    public int eventCode;//事件编码
    public long matchId;
    public long teamId;
    public long playerId;
    public int partNumber;
    public long timeSecond;
    public String additionalData;
    public String uuid;

    public EventModifyRequest(Context context) {
        super(context);
    }
}
