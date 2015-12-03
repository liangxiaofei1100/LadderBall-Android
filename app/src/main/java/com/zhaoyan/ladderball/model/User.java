package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 用户信息表
 * Created by Yuri on 2015/12/2.
 */
@Table(name = "user")
public class User extends Model{

    /**用户唯一标识符*/
    @Column(name = "userToken")
    public String mUserToken;

    /**用户昵称，姓名*/
    @Column(name = "name")
    public String mUserName;

    /**手机号，也即用户的登录账号*/
    @Column(name = "phone")
    public String mPhone;

    /**用户地址，目前仅保存地区，比如上海市 普陀区*/
    @Column(name = "address")
    public String mAddress;

    /**性别，0：女性；1：男性*/
    @Column(name = "gender")
    public int mGender;


}
