package com.zhaoyan.ladderball.http.request;

import android.content.Context;

/**
 * Created by Yuri on 2015/12/3.
 */
public class ModifyPasswordRequest extends BaseRequest {

    public String password;
    public String newPassword;

    public ModifyPasswordRequest(Context context) {
        super(context);
    }
}
