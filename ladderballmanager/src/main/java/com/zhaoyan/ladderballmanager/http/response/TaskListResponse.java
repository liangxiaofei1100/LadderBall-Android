package com.zhaoyan.ladderballmanager.http.response;

import java.util.List;

/**
 * Created by Yuri on 2015/12/16.
 */
public class TaskListResponse extends BaseResponse {

    public List<HttpMatch> matches;

    public static class HttpMatch{
        public long id;
        public HttpTeam teamHome;
        public HttpTeam teamVisitor;
        public HttpRecorder recorderHome;
        public HttpRecorder recorderVisitor;
        public int playerNumber;
        public long startTime;
        public String address;
        public boolean complete;
    }

    public static class HttpTeam{
        public long id;
        public String name;
        public String color;
        public int score;
        public String logoURL;
    }

    public static class HttpRecorder{
        public long id;
        public String phone;
        public String name;
        public String address;
        public int gender;
    }

}
