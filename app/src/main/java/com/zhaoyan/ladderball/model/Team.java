package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/12/7.
 */
@Table(name = "team")
public class Team extends Model{
    @Column(name = "teamId")
    public long teamId;//队伍id
    @Column(name = "matchId")
    public long matchId;//比赛id
    @Column(name = "name")
    public String name;
    @Column(name = "isAssiged")
    public boolean isAssiged;//是否被领取
    @Column(name = "color")
    public String color;//队伍的颜色
    @Column(name = "logoUrl")
    public String logoUrl;//对标
    @Column(name = "score")
    public int score;

    public List<Player> players = new ArrayList<>();
}
