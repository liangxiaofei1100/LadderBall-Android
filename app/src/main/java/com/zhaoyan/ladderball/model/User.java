package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

/**
 * 用户信息表
 * Created by Yuri on 2015/12/2.
 */
@Table(name = "user")
public class User extends Model{

    public String mUserName;
}
