package com.zhaoyan.ladderball.http;

import com.zhaoyan.ladderball.http.request.LoginRequest;
import com.zhaoyan.ladderball.http.request.ModifyPasswordRequest;
import com.zhaoyan.ladderball.http.response.LoginResponse;
import com.zhaoyan.ladderball.http.response.ModifyPasswordResponse;

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

}
