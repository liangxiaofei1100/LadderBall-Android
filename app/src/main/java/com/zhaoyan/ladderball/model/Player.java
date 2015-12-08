package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/5.
 */
@Table(name = "player")
public class Player extends Model{

    @Column(name = "playerId")
    public long playerId;
    @Column(name = "teamId")
    public long teamId;
    @Column(name = "matchId")
    public long matchId;
    @Column(name = "name")
    public String name;
    @Column(name = "isFirst")
    public boolean isFirst;
//    public String mPlayerAvatarUrl;
    @Column(name = "number")
    public int number;
}
