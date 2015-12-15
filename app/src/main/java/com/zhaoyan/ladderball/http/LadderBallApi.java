package com.zhaoyan.ladderball.http;

import com.zhaoyan.ladderball.http.request.AddPlayerRequest;
import com.zhaoyan.ladderball.http.request.BaseRequest;
import com.zhaoyan.ladderball.http.request.CommitTaskRequest;
import com.zhaoyan.ladderball.http.request.CreatePracticeRequest;
import com.zhaoyan.ladderball.http.request.EventDeleteRequest;
import com.zhaoyan.ladderball.http.request.EventCollectionRequest;
import com.zhaoyan.ladderball.http.request.EventModifyRequest;
import com.zhaoyan.ladderball.http.request.EventPartListRequest;
import com.zhaoyan.ladderball.http.request.LoginRequest;
import com.zhaoyan.ladderball.http.request.MatchDetailRequest;
import com.zhaoyan.ladderball.http.request.MatchModifyRequest;
import com.zhaoyan.ladderball.http.request.ModifyPasswordRequest;
import com.zhaoyan.ladderball.http.request.PracticeDetailRequest;
import com.zhaoyan.ladderball.http.request.PracticeModifyRequest;
import com.zhaoyan.ladderball.http.request.ReceivePracticeRequest;
import com.zhaoyan.ladderball.http.request.TaskListRequest;
import com.zhaoyan.ladderball.http.response.AddPlayerResponse;
import com.zhaoyan.ladderball.http.response.BaseResponse;
import com.zhaoyan.ladderball.http.response.EventPartListResponse;
import com.zhaoyan.ladderball.http.response.LoginResponse;
import com.zhaoyan.ladderball.http.response.MatchDetailResponse;
import com.zhaoyan.ladderball.http.response.ModifyPasswordResponse;
import com.zhaoyan.ladderball.http.response.PracticeDetailResponse;
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
    Observable<TaskListResponse> doGetTaskList(@Body TaskListRequest request);

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

    /**获取一场比赛一小节的数据记录*/
    @POST("match/eventpartlist")
    Observable<EventPartListResponse> doGetEvetPartList(@Body EventPartListRequest request);

    /**提交任务请求*/
    @POST("match/submit")
    Observable<BaseResponse> doCommitTask(@Body CommitTaskRequest request);

    /**删除一条数据记录*/
    @POST("match/event/delete")
    Observable<BaseResponse> doDeleteEvent(@Body EventDeleteRequest request);

    /**修改一条数据记录*/
    @POST("match/event/modify")
    Observable<BaseResponse> doModifyEvent(@Body EventModifyRequest request);

    /**已领取的练习赛列表请求*/
    @POST("tmpmatch/list")
    Observable<TaskListResponse> doGetPracticeList(@Body BaseRequest request);

    /**未被领取的练习赛列表请求*/
    @POST("tmpmatch/toasignlist")
    Observable<TaskListResponse> doGetUnAssignPracticeList(@Body BaseRequest request);

    /**创建练习赛*/
    @POST("tmpmatch/add")
    Observable<BaseResponse> doCreatePracticeTask(@Body CreatePracticeRequest request);

    /**领取练习赛，只能领取客队的任务*/
    @POST("tmpmatch/asignvisitor")
    Observable<BaseResponse> doReceivePracticeMatch(@Body ReceivePracticeRequest request);

    /**练习赛获取任务详细信息请求*/
    @POST("tmpmatch/detail")
    Observable<PracticeDetailResponse> doGetPracticeDetail(@Body PracticeDetailRequest request);

    /**练习赛修改比赛设置请求*/
    @POST("tmpmatch/modify")
    Observable<BaseResponse> doModifyPractice(@Body PracticeModifyRequest request);

}
