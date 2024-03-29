package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 练习赛小节数据表
 * Created by Yuri on 2015/12/7.
 */
@Table(name = "tmp_part_data")
public class TmpPartData extends Model {
    @Column(name = "matchId")
    public long matchId;
//    @Column(name = "teamId")
//    public long teamId;
    @Column(name = "partNumber")
    public int partNumber;
    @Column(name = "isComplete")
    public boolean isComplete;
}
