package com.zhaoyan.ladderballmanager.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/14.
 */
public class TaskListRequest extends BaseRequest {

    public int completeType;

    public TaskListRequest(Context context) {
        super(context);
    }
}
