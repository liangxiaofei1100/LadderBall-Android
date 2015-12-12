package com.zhaoyan.ladderball.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zhaoyan.ladderball.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/12.
 */
public class DataRepairDialog extends BaseDialog {

    @Bind(R.id.et_dialog_data_repair_minute)
    EditText mMinuteET;
    @Bind(R.id.et_dialog_data_repair_second)
    EditText mSecondET;
    @Bind(R.id.et_dialog_data_repair_number)
    EditText mOldNumberET;
    @Bind(R.id.et_dialog_data_repair_number_new)
    EditText mNewNumberET;
    @Bind(R.id.spinner_dialog_data_repair)
    Spinner mSpinner;

    @Bind(R.id.ll_dialog_data_repair_edit)
    View mEditExtraView;

    private int mType;
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;

    public DataRepairDialog(Context context, int type) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_data_repair, null);
        ButterKnife.bind(this, view);

        if (type == TYPE_EDIT) {
            mEditExtraView.setVisibility(View.VISIBLE);
        } else {
            mEditExtraView.setVisibility(View.GONE);
        }

        String[] events = context.getResources().getStringArray(R.array.all_events);
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, events);
        mSpinner.setAdapter(adapter);


        setCustomView(view);
    }
}
