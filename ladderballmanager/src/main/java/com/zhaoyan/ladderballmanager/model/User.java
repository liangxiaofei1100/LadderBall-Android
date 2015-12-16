package com.zhaoyan.ladderballmanager.model;

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
    public String mName;


}
