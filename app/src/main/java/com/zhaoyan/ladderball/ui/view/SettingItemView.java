package com.zhaoyan.ladderball.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.util.DensityUtil;

/**
 * 将我的界面所有设置相关的item，封装成一个自定义控件
 */
public class SettingItemView extends FrameLayout {

    private int mIconResId;
    private String mTitle;
    private String mSummary;
    private int mSummaryColor;
    /**是否有下分隔线*/
    private boolean mHasUnderLine;
    /**是否是包含一个Switch Button的Item*/
    private boolean mIsSwitchItem;
    /**是否需要右边的箭头*/
    private boolean mHasRightArrow;

    /**
     * 浮在图标右上角的红点标志，如果有图标的话 <br>
     * 在title右边的红点标记，如果没图标的话
     * */
    private View mRedDotView;
    private TextView mSummaryView;

//    private SlideButton mSlideButton;

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        if (attributes != null) {
            try {
                mIconResId = attributes.getResourceId(R.styleable.SettingItemView_itemIcon, -1);
                mTitle = attributes.getString(R.styleable.SettingItemView_itemTitle);
                mSummary = attributes.getString(R.styleable.SettingItemView_itemSummary);
                mSummaryColor = attributes.getColor(R.styleable.SettingItemView_itemSummaryColor,
                        getResources().getColor(R.color.textSecondary));
                mHasUnderLine = attributes.getBoolean(R.styleable.SettingItemView_itemUnderLine, true);
                mIsSwitchItem = attributes.getBoolean(R.styleable.SettingItemView_itemSwitch, false);
                mHasRightArrow = attributes.getBoolean(R.styleable.SettingItemView_itemRightArrow, true);
            } finally {
                attributes.recycle();
            }
        }

        //暂时不实现sitchitem
//        if (mIsSwitchItem) {
//            initSwitch(context);
//        } else {
            init(context);
//        }
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.layout_setting_item, null);
        View iconFrameView = rootView.findViewById(R.id.layout_frame);
        ImageView iconView = (ImageView) rootView.findViewById(R.id.iv_setting_icon);
        TextView titleView = (TextView) rootView.findViewById(R.id.tv_setting_title);
        mSummaryView = (TextView) rootView.findViewById(R.id.tv_setting_summary);
        View underLineView = rootView.findViewById(R.id.underLineView);
        View indicatorView = rootView.findViewById(R.id.indicator);
        if (mIconResId == -1) {
            mRedDotView = rootView.findViewById(R.id.iv_icon_new);
            iconFrameView.setVisibility(View.GONE);
            rootView.setPadding(rootView.getPaddingLeft() + DensityUtil.dip2px(context, 5),
                    rootView.getPaddingTop(),
                    rootView.getPaddingRight(),
                    rootView.getPaddingBottom());
        } else {
            mRedDotView = rootView.findViewById(R.id.iv_icon_dot);
            iconView.setImageResource(mIconResId);
        }
        titleView.setText(mTitle);
        mSummaryView.setText(mSummary);
        mSummaryView.setTextColor(mSummaryColor);
        underLineView.setVisibility(mHasUnderLine ? VISIBLE : INVISIBLE);
        indicatorView.setVisibility(mHasRightArrow ? VISIBLE : GONE);
        addView(rootView);

        setBackgroundResource(R.drawable.setting_item_bg);
    }

//    private void initSwitch(Context context) {
//        View rootView = inflate(context, R.layout.layout_setting_switch_item, null);
//        View iconFrameView = rootView.findViewById(R.id.layout_frame);
//        ImageView iconView = (ImageView) rootView.findViewById(R.id.iv_setting_icon);
//        TextView titleView = (TextView) rootView.findViewById(R.id.tv_setting_title);
//        mSummaryView = (TextView) rootView.findViewById(R.id.tv_setting_summary);
//        View underLineView = rootView.findViewById(R.id.underLineView);
//        mRedDotView = rootView.findViewById(R.id.iv_icon_dot);
//        if (mIconResId == -1) {
//            mRedDotView = rootView.findViewById(R.id.iv_icon_new);
//            iconFrameView.setVisibility(View.GONE);
//            rootView.setPadding(rootView.getPaddingLeft() + DensityUtil.dip2px(context, 5),
//                    rootView.getPaddingTop(),
//                    rootView.getPaddingRight(),
//                    rootView.getPaddingBottom());
//        } else {
//            mRedDotView = rootView.findViewById(R.id.iv_icon_dot);
//            iconView.setImageResource(mIconResId);
//        }
//        titleView.setText(mTitle);
//        underLineView.setVisibility(mHasUnderLine ? VISIBLE : INVISIBLE);
//
//        mSlideButton = (SlideButton) rootView.findViewById(R.id.btn_switch);
//        addView(rootView);
//        setBackgroundResource(R.drawable.setting_item_selector);
//    }

    /**
     * 是否有新的事件提醒
     */
    public void setHasNew(boolean hsNew) {
        if (mRedDotView != null) {
            mRedDotView.setVisibility(hsNew ? VISIBLE : GONE);
        }
    }

    public void setSummaryText(String text) {
        if (mSummaryView != null) {
            mSummaryView.setText(text);
        }
    }

//    public void setSlideButtonChecked(boolean checked) {
//        mSlideButton.setChecked(checked);
//    }
//
//    public void setOnSlideListener( SlideButton.SlideListener listener) {
//        mSlideButton.setSlideListener(listener);
//    }

}
