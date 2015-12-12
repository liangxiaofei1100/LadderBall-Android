package com.zhaoyan.ladderball.http;

import com.zhaoyan.ladderball.http.request.AddPlayerRequest;
import com.zhaoyan.ladderball.http.request.BaseRequest;
import com.zhaoyan.ladderball.http.request.EventCollectionRequest;
import com.zhaoyan.ladderball.http.request.LoginRequest;
import com.zhaoyan.ladderball.http.request.MatchDetailRequest;
import com.zhaoyan.ladderball.http.request.MatchModifyRequest;
import com.zhaoyan.ladderball.http.request.ModifyPasswordRequest;
import com.zhaoyan.ladderball.http.response.AddPlayerResponse;
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

    /**比赛时间数据采集*/
    @POST("match/eventcollection")
    Observable<BaseResponse> doCommitEventData(@Body EventCollectionRequest request);

    /**新增球员请求*/
    @POST("match/addplayer")
    Observable<AddPlayerResponse> doAddPlayer(@Body AddPlayerRequest request);

    /**已领取的练习赛列表请求*/
    @POST("tmpmatch/list")
    Observable<TaskListResponse> doGetPracticeList(@Body BaseRequest request);

    /**未被领取的练习赛列表请求*/
    @POST("tmpmatch/toasignlist")
    Observable<TaskListResponse> doGetUnAssignPracticeList(@Body BaseRequest request);

}
