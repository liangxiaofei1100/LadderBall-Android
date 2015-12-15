package com.zhaoyan.ladderballmanager.http.request;

import android.content.Context;

/**
 * 登录请求参数
 * Created by Yuri on 2015/12/3.
 */
public class LoginRequest extends BaseRequest{
    public String userName;
    public String password;
    public int loginType;

    public LoginRequest(Context context) {
        super(context);
    }
}
