package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/10.
 */
@Table(name = "player_event")
public class PlayerEvent extends Model {

    @Column(name = "matchId")
    public long matchId;//比赛id
    @Column(name = "teamId")
    public long teamId;//队伍id
    @Column(name = "playerId")
    public long playerId;//球员id
    @Column(name = "playerNumber")
    public int playerNumber;//球员号码
    @Column(name = "partNumber")
    public int partNumber;//节数
    @Column(name = "timeSecond")
    public long timeSecond;//相对时间
    @Column(name = "additionalData")
    public String additionalData;//备注

    @Column(name = "jinQiu")
    public int jinQiu;
    @Column(name = "zhuGong")
    public int zhuGong;
    @Column(name = "jiaoQiu")
    public int jiaoQiu;
    @Column(name = "renYiQiu")
    public int renYiQiu;
    @Column(name = "bianJieQiu")
    public int bianJieQiu;
    @Column(name = "yueWei")
    public int yueWei;
    @Column(name = "shiWu")
    public int shiWu;
    @Column(name = "guoRenChengGong")
    public int guoRenChengGong;
    @Column(name = "guoRenShiBai")
    public int guoRenShiBai;
    @Column(name = "sheZheng")
    public int sheZheng;
    @Column(name = "shePian")
    public int shePian;
    @Column(name = "sheMenBeiDu")
    public int sheMenBeiDu;
    @Column(name = "chuanQiuChengGong")
    public int chuanQiuChengGong;
    @Column(name = "chuanQiuShiBai")
    public int chuanQiuShiBai;
    @Column(name = "weiXieQiu")
    public int weiXieQiu;
    @Column(name = "fengDuSheMen")
    public int fengDuSheMen;
    @Column(name = "lanJie")
    public int lanJie;
    @Column(name = "qiangDuan")
    public int qiangDuan;
    @Column(name = "jieWei")
    public int jieWei;
    @Column(name = "puJiuSheMen")
    public int puJiuSheMen;
    @Column(name = "danDao")
    public int danDao;
    @Column(name = "shouPaoQiu")
    public int shouPaoQiu;
    @Column(name = "qiuMenQiu")
    public int qiuMenQiu;
    @Column(name = "huangPai")
    public int huangPai;
    @Column(name = "hongPai")
    public int hongPai;
    @Column(name = "fanGui")
    public int fanGui;
    @Column(name = "wuLongQiu")
    public int wuLongQiu;
    @Column(name = "huanRen")
    public int huanRen;

    @Override
    public String toString() {
        return "Event{" +
                "additionalData='" + additionalData + '\'' +
                ", matchId=" + matchId +
                ", teamId=" + teamId +
                ", playerId=" + playerId +
                ", partNumber=" + partNumber +
                ", timeSecond=" + timeSecond +
                ", jinQiu=" + jinQiu +
                ", zhuGong=" + zhuGong +
                ", jiaoQiu=" + jiaoQiu +
                ", renYiQiu=" + renYiQiu +
                ", bianJieQiu=" + bianJieQiu +
                ", yueWei=" + yueWei +
                ", shiWu=" + shiWu +
                ", guoRenChengGong=" + guoRenChengGong +
                ", guoRenShiBai=" + guoRenShiBai +
                ", sheZheng=" + sheZheng +
                ", shePian=" + shePian +
                ", sheMenBeiDu=" + sheMenBeiDu +
                ", chuanQiuChengGong=" + chuanQiuChengGong +
                ", chuanQiuShiBai=" + chuanQiuShiBai +
                ", weiXieQiu=" + weiXieQiu +
                ", fengDuSheMen=" + fengDuSheMen +
                ", lanJie=" + lanJie +
                ", qiangDuan=" + qiangDuan +
                ", jieWei=" + jieWei +
                ", puJiuSheMen=" + puJiuSheMen +
                ", danDao=" + danDao +
                ", shouPaoQiu=" + shouPaoQiu +
                ", qiuMenQiu=" + qiuMenQiu +
                ", huangPai=" + huangPai +
                ", hongPai=" + hongPai +
                ", fanGui=" + fanGui +
                ", wuLongQiu=" + wuLongQiu +
                ", huanRen=" + huanRen +
                '}';
    }

}
