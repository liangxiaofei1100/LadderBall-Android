package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/10.
 */
@Table(name = "tmp_event_item")
public class TmpEventItem extends Model {

    @Column(name = "eventCode")
    public int eventCode;

    @Column(name = "matchId")
    public long matchId;

    @Column(name = "teamId")
    public long teamId;

    @Column(name = "playerId")
    public long playerId;

    @Column(name = "playerNumber")
    public int playerNumber;

    @Column(name = "partNumber")
    public int partNumber;

    @Column(name = "timeSecond")
    public long timeSecond;

    @Column(name = "additionalData")
    public String additionalData;

    @Column(name = "event_uuid")
    public String uuid;

}
