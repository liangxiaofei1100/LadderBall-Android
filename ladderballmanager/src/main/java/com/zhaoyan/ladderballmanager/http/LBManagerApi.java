package com.zhaoyan.ladderballmanager.http;


import com.zhaoyan.ladderballmanager.http.request.LoginRequest;
import com.zhaoyan.ladderballmanager.http.request.TaskListRequest;
import com.zhaoyan.ladderballmanager.http.response.LoginResponse;
import com.zhaoyan.ladderballmanager.http.response.TaskListResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * retrofit 请求接口集合
 * Created by Yuri on 2015/12/2.
 */
public interface LBManagerApi {

    /**retrofit，登录请求*/
    @POST("account/recorderlogin")
    Observable<LoginResponse> doLogin(@Body LoginRequest loginRequest);

    /**获取任务列表请求*/
    @POST("match/list")
    Observable<TaskListResponse> doGetTaskList(@Body TaskListRequest request);


}
