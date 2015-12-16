package com.zhaoyan.ladderball.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.http.EventCode;
import com.zhaoyan.ladderball.http.response.EventPartListResponse;
import com.zhaoyan.ladderball.model.Player;
import com.zhaoyan.ladderball.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 数据修复菜单对话框
 * 包括数据添加，数据修改以及数据删除
 * Created by Yuri on 2015/12/12.
 */
public class DataRepairDialog extends BaseDialog implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.et_dialog_data_repair_minute)
    EditText mMinuteET;
    @Bind(R.id.et_dialog_data_repair_second)
    EditText mSecondET;
//    @Bind(R.id.et_dialog_data_repair_number)
//    EditText mOldNumberET;
//    @Bind(R.id.et_dialog_data_repair_number_new)
//    EditText mNewNumberET;

    @Bind(R.id.spinner_dialog_repair_event)
    Spinner mEventSpinner;

    @Bind(R.id.spinner_dialog_repair_number1)
    Spinner mNumber1Spinner;

    @Bind(R.id.spinner_dialog_repair_number2)
    Spinner mNumber2Spinner;

    @Bind(R.id.ll_dialog_data_repair_edit)
    View mEditExtraView;

    private int mType;
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;

    private boolean mHasChangeEventCode = false;

    private SpinnerAdapter mAdapter1;
    private SpinnerAdapter mAdapter2;


    private List<Player> mPlayerList;

    public OnRepairConfirmListener mListener;

    public void setOnConfirmListener(OnRepairConfirmListener listener) {
        mListener = listener;
    }

    public interface OnRepairConfirmListener {
        void onConfirm();
    }

    public DataRepairDialog(Context context, int type, List<Player> playerList) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_data_repair, null);
        ButterKnife.bind(this, view);

        if (type == TYPE_EDIT) {
            mEditExtraView.setVisibility(View.GONE);
        } else {
            mEditExtraView.setVisibility(View.GONE);
        }

        mPlayerList = playerList;

        mMinuteET.setText("0");
        mSecondET.setText("0");

        mMinuteET.setSelection(1);

        String[] events = context.getResources().getStringArray(R.array.all_events);
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_item, events);
        mEventSpinner.setAdapter(adapter);
        mEventSpinner.setOnItemSelectedListener(this);


        mAdapter1 = new SpinnerAdapter();
        mNumber1Spinner.setAdapter(mAdapter1);

        mAdapter2 = new SpinnerAdapter();
        mNumber2Spinner.setAdapter(mAdapter2);

        setCustomView(view);
    }

    private int getPosition(int number) {
        Player player;
        for (int i = 0; i < mPlayerList.size(); i++) {
            player = mPlayerList.get(i);
            if (player.number == number) {
                return i;
            }
        }
        return -1;
    }

    public void setEvent(EventPartListResponse.HttpEvent event) {
        if (event == null) {
            return;
        }
        String[] minSecs = getMinSec(event.timeSecond);
        mMinuteET.setText(minSecs[0]);
        mSecondET.setText(minSecs[1]);

        mMinuteET.setSelection(minSecs[0].length());

//        mOldNumberET.setText(event.playerNumber + "");
        mEventSpinner.setSelection(getEventPosition(event.eventCode));
        int playerNumber = getReplacePlayerNumber(event.additionalData);
//        if (playerNumber == -1) {
//            mNewNumberET.setText("");
//        } else {
//            mNewNumberET.setText(playerNumber + "");
//        }

        int position = getPosition(event.playerNumber);
        mNumber1Spinner.setSelection(position);

        if (event.eventCode == EventCode.EVENT_HUAN_REN) {
            mEditExtraView.setVisibility(View.VISIBLE);

        }

    }

    public long getTime() {
        String minute = mMinuteET.getText().toString().trim();
        String second = mSecondET.getText().toString().trim();
        if (TextUtils.isEmpty(minute)) {
            minute = "0";
        }

        if (TextUtils.isEmpty(second)) {
            second = "0";
        }

        long time = Integer.valueOf(minute) * 60 * 1000 + Integer.valueOf(second) * 1000;
        return time;
    }

    public long getPlayerId1() {
        return mAdapter1.getItemId(mNumber1Spinner.getSelectedItemPosition());
    }

    public int getPlayerNumber1() {
        return mAdapter1.getItem(mNumber1Spinner.getSelectedItemPosition()).number;
    }

    public long getPlayerId2() {
        return mAdapter2.getItemId(mNumber2Spinner.getSelectedItemPosition());
    }

    public int getPlayerNumber2() {
        return mAdapter2.getItem(mNumber2Spinner.getSelectedItemPosition()).number;
    }

    public String getSelectedItem() {
        String result = (String) mEventSpinner.getSelectedItem();
        return result;
    }


    public int getSelectedEventCode() {
        if (mHasChangeEventCode) {
            return getEventCode(mEventSpinner.getSelectedItemPosition());
        }

        return -1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mHasChangeEventCode = true;
        if (position == 27) {
            mEditExtraView.setVisibility(View.VISIBLE);
        } else {
            mEditExtraView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String[] getMinSec(long time) {
        String[] result = new String[2];
        String min = time % (60 * 60 * 1000) / (60 * 1000) + "";
        String sec = time % (60 * 60 * 1000) % (60 * 1000) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        }

        result[0] = min;

        if (sec.length() == 4) {
            sec = "0" + sec;
        } else if (sec.length() == 3) {
            sec = "00" + sec;
        } else if (sec.length() == 2) {
            sec = "000" + sec;
        } else if (sec.length() == 1) {
            sec = "0000" + sec;
        }

        result[1] = sec.trim().substring(0, 2);
        return result;
    }

    private int getReplacePlayerNumber(String additionalData) {
        int playerNumber = -1;
        if (TextUtils.isEmpty(additionalData)) {
            return -1;
        }
        try {
            JSONObject jsonObject = new JSONObject(additionalData);
            playerNumber = jsonObject.getInt("playerNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playerNumber;
    }

    private class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPlayerList.size();
        }

        @Override
        public Player getItem(int position) {
            return mPlayerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mPlayerList.get(position).playerId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, null);
            TextView textView = (TextView) view.findViewById(R.id.text1);
            textView.setText(mPlayerList.get(position).number + "");
            return view;
        }
    }


    private int getEventPosition(int eventCode) {
        Log.d("eventCode:" + eventCode);
        int position = 0;
        switch (eventCode) {
            case EventCode.EVENT_JIN_QIU:
                position = 0;
                break;
            case EventCode.EVENT_ZHU_GONG:
                position = 1;
                break;
            case EventCode.EVENT_JIAO_QIU:
                position = 2;
                break;
            case EventCode.EVENT_REN_YI_QIU:
                position = 3;
                break;
            case EventCode.EVENT_BIAN_JIE_QIU:
                position = 4;
                break;
            case EventCode.EVENT_YUE_WEI:
                position = 5;
                break;
            case EventCode.EVENT_SHI_WU:
                position = 6;
                break;
            case EventCode.EVENT_GUO_REN_CHENG_GONG:
                position = 7;
                break;
            case EventCode.EVENT_GUO_REN_SHI_BAI:
                position = 8;
                break;
            case EventCode.EVENT_SHE_ZHENG:
                position = 9;
                break;
            case EventCode.EVENT_SHE_PIAN:
                position = 10;
                break;
            case EventCode.EVENT_SHE_MEN_BEI_DU:
                position = 11;
                break;
            case EventCode.EVENT_CHUAN_QIU_CHENG_GONG:
                position = 12;
                break;
            case EventCode.EVENT_WEI_XIE_QIU:
                position = 13;
                break;
            case EventCode.EVENT_CHUAN_QIU_SHI_BAI:
                position = 14;
                break;
            case EventCode.EVENT_FENG_DU_SHE_MEN:
                position = 15;
                break;
            case EventCode.EVENT_LAN_JIE:
                position = 16;
                break;
            case EventCode.EVENT_QIANG_DUAN:
                position = 17;
                break;
            case EventCode.EVENT_JIE_WEI:
                position = 18;
                break;
            case EventCode.EVENT_BU_JIU_SHE_MEN:
                position = 19;
                break;
            case EventCode.EVENT_BU_JIU_DAN_DAO:
                position = 20;
                break;
            case EventCode.EVENT_SHOU_PAO_QIU:
                position = 21;
                break;
            case EventCode.EVENT_QIU_MEN_QIU:
                position = 22;
                break;
            case EventCode.EVENT_HUANG_PAI:
                position = 23;
                break;
            case EventCode.EVENT_HONG_PAI:
                position = 24;
                break;
            case EventCode.EVENT_FAN_GUI:
                position = 25;
                break;
            case EventCode.EVENT_WU_LONG_QIU:
                position = 26;
                break;
            case EventCode.EVENT_HUAN_REN:
                position = 27;
                break;
        }
        return position;
    }

    private int getEventCode(int position) {
        int eventCode = 0;
        switch (position) {
            case 0:
                eventCode = EventCode.EVENT_JIN_QIU;
                break;
            case 1:
                eventCode = EventCode.EVENT_ZHU_GONG;
                break;
            case 2:
                eventCode = EventCode.EVENT_JIAO_QIU;
                break;
            case 3:
                eventCode = EventCode.EVENT_REN_YI_QIU;
                break;
            case 4:
                eventCode = EventCode.EVENT_BIAN_JIE_QIU;
                break;
            case 5:
                eventCode = EventCode.EVENT_YUE_WEI;
                break;
            case 6:
                eventCode = EventCode.EVENT_SHI_WU;
                break;
            case 7:
                eventCode = EventCode.EVENT_GUO_REN_CHENG_GONG;
                break;
            case 8:
                eventCode = EventCode.EVENT_GUO_REN_SHI_BAI;
                break;
            case 9:
                eventCode = EventCode.EVENT_SHE_ZHENG;
                break;
            case 10:
                eventCode = EventCode.EVENT_SHE_PIAN;
                break;
            case 11:
                eventCode = EventCode.EVENT_SHE_MEN_BEI_DU;
                break;
            case 12:
                eventCode = EventCode.EVENT_CHUAN_QIU_CHENG_GONG;
                break;
            case 13:
                eventCode = EventCode.EVENT_WEI_XIE_QIU;
                break;
            case 14:
                eventCode = EventCode.EVENT_CHUAN_QIU_SHI_BAI;
                break;
            case 15:
                eventCode = EventCode.EVENT_FENG_DU_SHE_MEN;
                break;
            case 16:
                eventCode = EventCode.EVENT_LAN_JIE;
                break;
            case 17:
                eventCode = EventCode.EVENT_QIANG_DUAN;
                break;
            case 18:
                eventCode = EventCode.EVENT_JIE_WEI;
                break;
            case 19:
                eventCode = EventCode.EVENT_BU_JIU_SHE_MEN;
                break;
            case 20:
                eventCode = EventCode.EVENT_BU_JIU_DAN_DAO;
                break;
            case 21:
                eventCode = EventCode.EVENT_SHOU_PAO_QIU;
                break;
            case 22:
                eventCode = EventCode.EVENT_QIU_MEN_QIU;
                break;
            case 23:
                eventCode = EventCode.EVENT_HUANG_PAI;
                break;
            case 24:
                eventCode = EventCode.EVENT_HONG_PAI;
                break;
            case 25:
                eventCode = EventCode.EVENT_FAN_GUI;
                break;
            case 26:
                eventCode = EventCode.EVENT_WU_LONG_QIU;
                break;
            case 27:
                eventCode = EventCode.EVENT_HUAN_REN;
                break;
        }
        return eventCode;
    }

}
