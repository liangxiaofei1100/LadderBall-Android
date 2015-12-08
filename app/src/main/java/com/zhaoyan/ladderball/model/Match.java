package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/12/7.
 */
@Table(name = "match")
public class Match extends Model{

    @Column(name = "matchId")
    public long matchId;
    @Column(name = "startTime")
    public long startTime;
    @Column(name = "address")
    public String address;
    @Column(name = "playerNumber")
    public int playerNumber;
    @Column(name = "totalPart")
    public int totalPart;
    @Column(name = "partMinutes")
    public int partMinutes;

    @Column(name = "teamHome")
    public Team teamHome;
    @Column(name = "teamVisitor")
    public Team teamVisitor;

    public List<PartData> partDatas = new ArrayList<>();

}
