package com.zhaoyan.ladderball.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.ui.adapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/12/12.
 */
public class MenuDialog extends BaseDialog implements AdapterView.OnItemClickListener {

    public MenuDialog(Context context, List<String> items) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_menu, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_dialog);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new ListDialogAdapter(items));
        setCustomView(view);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.onItemClick(position);
            dismiss();
        }
    }


    public interface OnMMItemClickListener{
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public MenuDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
        return this;
    }

    private class ListDialogAdapter extends BaseAdapter {

        List<String> mItemList = new ArrayList<>();


        public ListDialogAdapter(List<String> items) {
            mItemList = items;
        }


        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.layout_dialog_menu_item, null);
            TextView textView = (TextView) view.findViewById(R.id.list_dialog_item_title);
            textView.setText(mItemList.get(position));
            return view;
        }
    }
}
