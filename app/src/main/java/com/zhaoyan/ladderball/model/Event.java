package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Yuri on 2015/12/9.
 */
@Table(name = "event")
public class Event extends Model {

    @Column(name = "matchId")
    public long matchId;//比赛id
    @Column(name = "teamId")
    public long teamId;//队伍id
    @Column(name = "playerId")
    public long playerId;//球员id
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
    @Column(name = "buJiuSheMen")
    public int buJiuSheMen;
    @Column(name = "buJiuDanDao")
    public int buJiuDanDao;
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

}
