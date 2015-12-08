package com.zhaoyan.ladderball.http;

import com.zhaoyan.ladderball.http.request.BaseRequest;
import com.zhaoyan.ladderball.http.request.LoginRequest;
import com.zhaoyan.ladderball.http.request.MatchDetailRequest;
import com.zhaoyan.ladderball.http.request.MatchModifyRequest;
import com.zhaoyan.ladderball.http.request.ModifyPasswordRequest;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.http.response.LoginResponse;
import com.zhaoyan.ladderball.http.response.MatchDetailResponse;
import com.zhaoyan.ladderball.http.response.ModifyPasswordResponse;
import com.zhaoyan.ladderball.http.response.TaskListResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * retrofit 请求接口集合
 * Created by Yuri on 2015/12/2.
 */
public interface LadderBallApi {

    /**retrofit，登录请求*/
    @POST("account/recorderlogin")
    Observable<LoginResponse> doLogin(@Body LoginRequest loginRequest);

    /**修改密码请求*/
    @POST("account/recorderSetPassword")
    Observable<ModifyPasswordResponse> doModifyPassword(@Body ModifyPasswordRequest request);

    /**获取任务列表请求*/
    @POST("match/list")
    Observable<TaskListResponse> doGetTaskList(@Body BaseRequest request);

    /**获取任务详细信息请求*/
    @POST("match/detail")
    Observable<MatchDetailResponse> doGetMatchDetail(@Body MatchDetailRequest request);

    /**修改比赛设置请求*/
    @POST("match/modify")
    Observable<BaseResponse> doModifyMatch(@Body MatchModifyRequest request);

}
