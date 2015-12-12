package com.zhaoyan.ladderball.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.util.Log;

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

    public void rollback(int eventCode) {
        Log.d("eventCode：" + eventCode);
        switch (eventCode) {
            case EventCode.EVENT_JIN_QIU:
                jinQiu -= 1;
                break;
            case EventCode.EVENT_ZHU_GONG:
                zhuGong -= 1;
                break;
            case EventCode.EVENT_JIAO_QIU:
                jiaoQiu -= 1;
                break;
            case EventCode.EVENT_REN_YI_QIU:
                renYiQiu -= 1;
                break;
            case EventCode.EVENT_BIAN_JIE_QIU:
                bianJieQiu -= 1;
                break;
            case EventCode.EVENT_YUE_WEI:
                yueWei -= 1;
                break;
            case EventCode.EVENT_SHI_WU:
                shiWu -= 1;
                break;
            case EventCode.EVENT_GUO_REN_CHENG_GONG:
                guoRenChengGong -= 1;
                break;
            case EventCode.EVENT_GUO_REN_SHI_BAI:
                guoRenShiBai -= 1;
                break;
            case EventCode.EVENT_SHE_ZHENG:
                sheZheng -= 1;
                break;
            case EventCode.EVENT_SHE_PIAN:
                shePian -= 1;
                break;
            case EventCode.EVENT_SHE_MEN_BEI_DU:
                sheMenBeiDu -= 1;
                break;
            case EventCode.EVENT_CHUAN_QIU_CHENG_GONG:
                chuanQiuChengGong -= 1;
                break;
            case EventCode.EVENT_WEI_XIE_QIU:
                weiXieQiu -= 1;
                break;
            case EventCode.EVENT_CHUAN_QIU_SHI_BAI:
                chuanQiuShiBai -= 1;
                break;
            case EventCode.EVENT_FENG_DU_SHE_MEN:
                fengDuSheMen -= 1;
                break;
            case EventCode.EVENT_LAN_JIE:
                lanJie -= 1;
                break;
            case EventCode.EVENT_QIANG_DUAN:
                qiangDuan -= 1;
                break;
            case EventCode.EVENT_JIE_WEI:
                jieWei -= 1;
                break;
            case EventCode.EVENT_BU_JIU_SHE_MEN:
                puJiuSheMen -= 1;
                break;
            case EventCode.EVENT_BU_JIU_DAN_DAO:
                danDao -= 1;
                break;
            case EventCode.EVENT_SHOU_PAO_QIU:
                shouPaoQiu -= 1;
                break;
            case EventCode.EVENT_QIU_MEN_QIU:
                qiuMenQiu -= 1;
                break;
            case EventCode.EVENT_HUANG_PAI:
                huangPai -= 1;
                break;
            case EventCode.EVENT_HONG_PAI:
                hongPai -= 1;
                break;
            case EventCode.EVENT_FAN_GUI:
                fanGui -= 1;
                break;
            case EventCode.EVENT_WU_LONG_QIU:
                wuLongQiu -= 1;
                break;
            case EventCode.EVENT_HUAN_REN:
                huanRen -= 1;
                break;
        }
        save();
    }

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
