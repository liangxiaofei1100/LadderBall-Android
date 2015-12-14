package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 练习赛表
 * Created by Yuri on 2015/12/14.
 */
@Table(name = "tmp_match")
public class TmpMatch extends Model {

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
    public TmpTeam teamHome;
    @Column(name = "teamVisitor")
    public TmpTeam teamVisitor;

    public List<TmpPartData> partDatas = new ArrayList<>();

    @Override
    public String toString() {
        return "Match{" +
                "address='" + address + '\'' +
                ", matchId=" + matchId +
                ", startTime=" + startTime +
                ", playerNumber=" + playerNumber +
                ", totalPart=" + totalPart +
                ", partMinutes=" + partMinutes +
                ", teamHome=" + teamHome +
                ", teamVisitor=" + teamVisitor +
                ", partDatas=" + partDatas +
                '}';
    }
}
