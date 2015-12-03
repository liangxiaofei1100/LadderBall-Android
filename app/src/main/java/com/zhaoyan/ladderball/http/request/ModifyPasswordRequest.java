package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * 修改密码请求request
 * Created by Yuri on 2015/12/3.
 */
public class ModifyPasswordRequest extends BaseRequest {

    public String password;
    public String newPassword;

    public ModifyPasswordRequest(Context context) {
        super(context);
    }
}
