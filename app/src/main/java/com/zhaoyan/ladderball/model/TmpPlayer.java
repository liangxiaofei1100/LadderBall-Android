package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 练习赛球员表
 * Created by Yuri on 2015/12/5.
 */
@Table(name = "tmp_player")
public class TmpPlayer extends Model{

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
    @Column(name = "isOnPitch")
    public boolean isOnPitch;//球员是否上场
//    public String mPlayerAvatarUrl;
    @Column(name = "number")
    public int number;

    @Override
    public String toString() {
        return "Player{" +
                ", playerId=" + playerId +
                ", teamId=" + teamId +
                ", matchId=" + matchId +
                ", name='" + name + '\'' +
                ", isFirst=" + isFirst +
                ", isOnPitch=" + isOnPitch +
                ", number=" + number +
                '}';
    }
}
