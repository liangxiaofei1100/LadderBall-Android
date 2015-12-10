package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/7.
 */
@Table(name = "part_data")
public class PartData extends Model {
    @Column(name = "matchId")
    public long matchId;
//    @Column(name = "teamId")
//    public long teamId;
    @Column(name = "partNumber")
    public int partNumber;
    @Column(name = "isComplete")
    public boolean isComplete;
}
