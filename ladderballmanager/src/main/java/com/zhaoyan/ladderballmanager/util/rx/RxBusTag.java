package com.zhaoyan.ladderballmanager.util.rx;

/**
 *
 * Created by Yuri on 2015/12/2.
 */
public class RxBusTag {

    public static final String TASK_FRAGMENT = "task_item_click";
    public static final String PRACTICE_ITEM_CLICK = "practice_item_click";

    public static final String PlAYER_ITEM_REMOVE = "player_item_remove";

    public static final String PLAYER_CHOOSE_CHANGE = "player_choose_change";

    public static final String DATA_RECORD_ACTIVITY = "data_record_activity";
    public static final String EVENT_RECORD_ITEM = "event_record_item";
    public static final String EVENT_REMOVE_RECENT_ITEM = "event_remove_recent_item";

    public static class DataRecord{
        public int itemType;
        public int position;

        public static final int ITEM_EVENT_RECORD_CLICK = 0;
        public static final int ITEM_EVENT_RECENT_REMOVE = 1;
        public static final int ITEM_PLAYER_CLICK = 2;
    }
}
