package com.zhaoyan.ladderball.http.response;

import java.util.List;

/**
 * Created by Yuri on 2015/12/7.
 */
public class MatchDetailResponse extends BaseResponse {

    public long id;// match id
    public long startTime;
    public String address;

    public HttpTeam teamHome;
    public HttpTeam teamVisitor;

    public int playerNumber;
    public int totalPart;
    public int partMinutes;
    public List<HttpPartData> partDatas;

    public class HttpTeam{
        public long id;
        public String name;
        public boolean isAsigned;
        public String color;
        public String logoURL;
        public List<HttpPlayer> players;
    }

    public class HttpPlayer{
        public long id;
        public String name;
        public int number;
        public boolean isFirst;
    }

    public class HttpPartData{
        public int partNumber;
        public boolean isComplete;
    }


}
