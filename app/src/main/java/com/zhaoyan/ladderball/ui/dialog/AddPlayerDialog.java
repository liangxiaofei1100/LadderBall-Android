package com.zhaoyan.ladderball.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.zhaoyan.ladderball.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yuri on 2015/12/11.
 */
public class AddPlayerDialog extends BaseDialog {

    @Bind(R.id.et_add_new_player_number)
    EditText mNumberView;
    @Bind(R.id.et_add_new_player_name)
    EditText mNameView;

    public AddPlayerDialog(Context context) {
        super(context);

        View view  = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_player, null);
        ButterKnife.bind(this, view);

        setDialogTitle("新增球员");
        setCustomView(view);
    }

    public String getPlayerNumber() {
        String number = mNumberView.getText().toString().trim();
        return number;
    }

    public String getPlayerName() {
        String name = mNameView.getText().toString().trim();
        return name;
    }

    public void setNumberErrStr(String string) {
        mNumberView.setError(string);
    }
}
