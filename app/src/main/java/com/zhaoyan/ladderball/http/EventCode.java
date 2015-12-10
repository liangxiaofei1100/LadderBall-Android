package com.zhaoyan.ladderball.http;

/*
| 技术动作   | 技术编号	| 说明		|
| -------- | --------- |----------	|
| 进球		| 10001		|			|
| 助攻		| 10002		|			|
| 角球		| 10003		|			|
| 任意球		| 10004		|			|
| 边界球		| 10005		|			|
| 越位		| 10006		|			|
| 失误		| 10007		|			|
| 过人成功	| 10008		|			|
| 过人失败	| 10009		|			|
| 射正		| 10010		|			|
| 射偏		| 10011		|			|
| 射门被堵	| 10012		|			|
| 传球成功	| 10013		|			|
| 威胁球		| 10014		|			|
| 传球失败	| 10015		|			|
| 封堵射门	| 10016		|			|
| 拦截		| 10017		|			|
| 抢断		| 10018		|			|
| 解围		| 10019		|			|
| 补救射门	| 10020		|			|
| 补救单刀	| 10021		|			|
| 手抛球		| 10022		|			|
| 球门球		| 10023		|			|
| 黄牌		| 10024		|			|
| 红牌		| 10025		|			|
| 犯规		| 10026		|			|
| 乌龙球		| 10027		|			|
| 换人		| 10028		|			|
 */
public class EventCode {
    /**
     * 进球
     */
    public static final int EVENT_JIN_QIU = 10001;
    /**
     * 助攻
     */
    public static final int EVENT_ZHU_GONG = 10002;
    /**
     * 角球
     */
    public static final int EVENT_JIAO_QIU = 10003;
    /**
     * 任意球
     */
    public static final int EVENT_REN_YI_QIU = 10004;
    /**
     * 边界球
     */
    public static final int EVENT_BIAN_JIE_QIU = 10005;
    /**
     * 越位
     */
    public static final int EVENT_YUE_WEI = 10006;
    /**
     * 失误
     */
    public static final int EVENT_SHI_WU = 10007;
    /**
     * 过人成功
     */
    public static final int EVENT_GUO_REN_CHENG_GONG = 10008;
    /**
     * 过人失败
     */
    public static final int EVENT_GUO_REN_SHI_BAI = 10009;
    /**
     * 射正
     */
    public static final int EVENT_SHE_ZHENG = 10010;
    /**
     * 射偏
     */
    public static final int EVENT_SHE_PIAN = 10011;
    /**
     * 射门被堵
     */
    public static final int EVENT_SHE_MEN_BEI_DU = 10012;
    /**
     * 传球成功
     */
    public static final int EVENT_CHUAN_QIU_CHENG_GONG = 10013;
    /**
     * 威胁球
     */
    public static final int EVENT_WEI_XIE_QIU = 10014;
    /**
     * 传球失败
     */
    public static final int EVENT_CHUAN_QIU_SHI_BAI = 10015;
    /**
     * 封堵射门
     */
    public static final int EVENT_FENG_DU_SHE_MEN = 10016;
    /**
     * 拦截
     */
    public static final int EVENT_LAN_JIE = 10017;
    /**
     * 抢断
     */
    public static final int EVENT_QIANG_DUAN = 10018;
    /**
     * 解围
     */
    public static final int EVENT_JIE_WEI = 10019;
    /**
     * 补救射门
     */
    public static final int EVENT_BU_JIU_SHE_MEN = 10020;
    /**
     * 补救单刀
     */
    public static final int EVENT_BU_JIU_DAN_DAO = 10021;
    /**
     * 手抛球
     */
    public static final int EVENT_SHOU_PAO_QIU = 10022;
    /**
     * 球门球
     */
    public static final int EVENT_QIU_MEN_QIU = 10023;
    /**
     * 黄牌
     */
    public static final int EVENT_HUANG_PAI = 10024;
    /**
     * 红牌
     */
    public static final int EVENT_HONG_PAI = 10025;
    /**
     * 犯规
     */
    public static final int EVENT_FAN_GUI = 10026;
    /**
     * 乌龙球
     */
    public static final int EVENT_WU_LONG_QIU = 10027;
    /**
     * 换人
     */
    public static final int EVENT_HUAN_REN = 10028;

    public static final int EVENT_PART_OVER = 20001;
}