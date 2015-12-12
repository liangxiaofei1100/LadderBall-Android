package com.zhaoyan.ladderball.http.response;

import java.util.List;

/**
 * Created by Yuri on 2015/12/12.
 */
public class EventPartListResponse extends BaseResponse {

    public List<HttpEvent> events;

    public static class HttpEvent{
        /**
         *"id": 15,
         "eventCode": 10001,
         "matchId": 1,
         "teamId": 1,
         "playerId": 1,
         "playerNumber": 1,
         "partNumber": 1,
         "timeSecond": 100,
         "additionalData": null,
         "uuid": "87d3352a-c5df-4538-89fc-93f134db877g"s
         */

        public long id;//event id
        public int eventCode;
        public long matchId;
        public long teamId;
        public long playerId;
        public int playerNumber;
        public int partNumber;
        public long timeSecond;
        public String additionalData;//{"id":1,"number":10}
        public String uuid;

    }
}
