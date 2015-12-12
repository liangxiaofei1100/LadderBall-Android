package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/12.
 */
@Table(name = "practice_task")
public class PracticeTask extends Model {

    //一场比赛有唯一的id
    @Column(name = "matchId")
    public long mMatchId;

    @Column(name = "teamHomeName")
    public String mTeamHomeName;//主场队伍的名称
    @Column(name = "teamHomeLogoUrl")
    public String mTeamHomeLogoUrl;//主场队伍的对标
    @Column(name = "teamHomeColor")
    public String mTeamHomeColor;//主场队伍的颜色
    @Column(name = "teamHomeIsAssigned")
    public boolean mTeamHomeIsAssigned;//组队任务是否被领取
    @Column(name = "teamHomeScore")
    public int mTeamHomeScore;//主队得分

    @Column(name = "teamVisitorColor")
    public String mTeamVisitorColor;//客场队伍的颜色
    @Column(name = "teamVisitorName")
    public String mTeamVisitorName;//客场队伍的名称
    @Column(name = "teamVisitorLogoUrl")
    public String mTeamVisitorLogoUrl;//客场队伍的对标
    @Column(name = "teamVisitorIsAssigned")
    public boolean mTeamVisitorIsAssigned;//客队任务是否被领取
    @Column(name = "teamVisitorScore")
    public int mTeamVisitorScore;//客队得分

    @Column(name = "playerNum")
    public int mPlayerNum;//一场比赛每边各多少人，即：8VS8,11VS11
    @Column(name = "startTime")
    public long mStartTime;//比赛开始时间
    @Column(name = "address")
    public String mAddress;//比赛进行地点
    @Column(name = "isComplete")
    public boolean mIsComplete;//task是否完成

    @Column(name = "isAssigned")
    public boolean mIsAssigned;//是否被领取

    @Override
    public String toString() {
        return "Task{" +
                "mAddress='" + mAddress + '\'' +
                ", mMatchId=" + mMatchId +
                ", mTeamHomeName='" + mTeamHomeName + '\'' +
                ", mTeamVisitorName='" + mTeamVisitorName + '\'' +
                ", mTeamHomeLogoUrl='" + mTeamHomeLogoUrl + '\'' +
                ", mTeamVisitorLogoUrl='" + mTeamVisitorLogoUrl + '\'' +
                ", mTeamHomeColor='" + mTeamHomeColor + '\'' +
                ", mTeamVisitorColor='" + mTeamVisitorColor + '\'' +
                ", mTeamHomeIsAssigned=" + mTeamHomeIsAssigned +
                ", mTeamVisitorIsAssigned=" + mTeamVisitorIsAssigned +
                ", mTeamHomeScore=" + mTeamHomeScore +
                ", mTeamVisitorScore=" + mTeamVisitorScore +
                ", mPlayerNum=" + mPlayerNum +
                ", mStartTime=" + mStartTime +
                ", mIsComplete=" + mIsComplete +
                '}';
    }

}
