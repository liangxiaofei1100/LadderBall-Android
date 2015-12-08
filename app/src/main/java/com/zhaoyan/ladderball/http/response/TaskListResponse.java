package com.zhaoyan.ladderball.http.response;

import java.util.List;

/**
 * Created by Yuri on 2015/12/4.
 */
public class TaskListResponse extends BaseResponse {

    public List<HttpMatch> matches;

    public class HttpMatch {
        public long id;

        public HttpTeam teamHome;//主队
        public HttpTeam teamVisitor;//客队
        public int playerNumber;//赛制：8人制，11人制
        public long startTime;//比赛开始日期
        public String address;//比赛地点
        public boolean complete;//是否完成

    }

    public class HttpTeam{
        public String name;
        public boolean isAsigned;
        public int score;
        public String color;
        public String logoURL;
    }
}
