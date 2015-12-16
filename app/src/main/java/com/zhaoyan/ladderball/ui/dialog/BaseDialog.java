package com.zhaoyan.ladderball.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.ladderball.R;
import com.zhaoyan.ladderball.util.DensityUtil;

public class BaseDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private View mCustomView;

    private View mDialogView;


    private Button mLeftButton;
    private Button mMiddleButton;
    private Button mRightButton;

    private TextView mMenuView;

    private String mTitle;
    private String mMessage;
    private int mIconResId = -1;

    private boolean mHasMessage = false;
    private boolean mShowTitle = false;

    private int mTextGravity = Gravity.START;

    /**对话框按钮*/
    private SparseBooleanArray mButtonArray = new SparseBooleanArray(3);

    private String mNegativeMessage, mPositiveMessage, mNeutralMessage;

    public static final int BUTTON_POSITIVE = 1;
    public static final int BUTTON_NEUTRAL = 2;
    public static final int BUTTON_NEGATIVE = 4;

    private onMMDialogClickListener mNegativeListener;
    private onMMDialogClickListener mPositiveListener;
    private onMMDialogClickListener mNeutralListener;

    private String mMenuText;

    public OnMenuClickListener mMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    public interface OnMenuClickListener{
        void onMenuClick();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_dialog_menu) {
            if (mMenuClickListener != null) {
                mMenuClickListener.onMenuClick();
            }
        }
    }

    public interface onMMDialogClickListener {
        void onClick(Dialog dialog);
    }

    public BaseDialog(Context context) {
        super(context, R.style.Custom_Dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        init(mContext);
    }

    private void init(Context context) {
        mDialogView = View.inflate(context, R.layout.layout_dialog_main, null);

        View topView = mDialogView.findViewById(R.id.topPanel);
        FrameLayout frameLayoutCustomView = (FrameLayout) mDialogView
                .findViewById(R.id.customPanel);

        TextView messageView = (TextView) mDialogView
                .findViewById(R.id.tv_custom_dialog_message);
        messageView.setGravity(mTextGravity);

        mMenuView = (TextView) mDialogView.findViewById(R.id.btn_dialog_menu);

        if (mCustomView != null) {
            frameLayoutCustomView.setVisibility(View.VISIBLE);
            if (frameLayoutCustomView.getChildCount() > 0) {
                frameLayoutCustomView.removeAllViews();
            }
            frameLayoutCustomView.addView(mCustomView);
        }

        if (mShowTitle) {
            topView.setVisibility(View.VISIBLE);
            TextView titleView = (TextView) mDialogView
                    .findViewById(R.id.tv_custom_dialog_title);
            titleView.setText(mTitle);

            ImageView iconView = (ImageView) mDialogView.findViewById(R.id.icon);
            if (mIconResId != -1) {
                iconView.setImageResource(mIconResId);
            }
        } else {
            topView.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mMenuText)) {
            mMenuView.setVisibility(View.GONE);
        } else {
            mMenuView.setText(mMenuText);
            mMenuView.setVisibility(View.VISIBLE);
            mMenuView.setOnClickListener(this);
        }

        if (mHasMessage) {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(mMessage);
            if (!mShowTitle) {
                messageView.setPadding(messageView.getPaddingLeft(),
                        messageView.getPaddingTop() + DensityUtil.dip2px(context, 10),
                        messageView.getPaddingRight(),
                        messageView.getPaddingBottom());
            }
        } else {
            messageView.setVisibility(View.GONE);
        }

        setUpButtons();
        setContentView(mDialogView);
    }

    /**
     * 设置Dialog内容文本的位置，居中，居左，还是居右；默认居中
     * @param gravity 居中{@link Gravity#CENTER},居左{@link Gravity#LEFT},居右{@link Gravity#RIGHT}
     */
    public void setTextGravity(int gravity){
        mTextGravity = gravity;
    }

    private void setUpButtons() {
        int BIT_BUTTON_POSITIVE = 1;
        int BIT_BUTTON_NEGATIVE = 2;
        int BIT_BUTTON_NEUTRAL = 4;
        int whichButtons = 0;

        if (!hasButtons()) {
            mDialogView.findViewById(R.id.button_panel).setVisibility(View.GONE);
        } else {
            if (hasButton(BUTTON_NEGATIVE)) {
                mLeftButton = (Button) mDialogView.findViewById(R.id.btn_left);
                mLeftButton.setText(mNegativeMessage);
                mLeftButton.setVisibility(View.VISIBLE);
                mLeftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null == mNegativeListener) {
                            dismiss();
                        } else {
                            mNegativeListener.onClick(BaseDialog.this);
                        }
                    }
                });
                whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
            }

            if (hasButton(BUTTON_NEUTRAL)) {
                mMiddleButton = (Button) mDialogView.findViewById(R.id.btn_middle);
                mMiddleButton.setText(mNeutralMessage);
                mMiddleButton.setVisibility(View.VISIBLE);
                mMiddleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null == mNeutralListener) {
                            dismiss();
                        } else {
                            mNeutralListener.onClick(BaseDialog.this);
                        }
                    }
                });
                whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
            }

            if (hasButton(BUTTON_POSITIVE)) {
                mRightButton = (Button) mDialogView.findViewById(R.id.btn_right);
                mRightButton.setText(mPositiveMessage);
                mRightButton.setVisibility(View.VISIBLE);
                mRightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null == mPositiveListener) {
                            dismiss();
                        } else {
                            mPositiveListener.onClick(BaseDialog.this);
                        }
                    }
                });
                whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
            }

            View divideOne = mDialogView.findViewById(R.id.divider_one);
            View divideTwo = mDialogView.findViewById(R.id.divider_two);
            int count = getButtons();
            if (count == 3) {
                divideOne.setVisibility(View.VISIBLE);
                divideTwo.setVisibility(View.VISIBLE);
            } else if (count == 2) {
                if (hasButton(BUTTON_NEGATIVE)) {
                    divideOne.setVisibility(View.VISIBLE);
                } else {
                    divideTwo.setVisibility(View.VISIBLE);
                }
            } else {
                //如果只有一个按钮的情况下，对话框按钮的背景要重新设置，主要是按钮圆角的考虑
                if (whichButtons == BIT_BUTTON_POSITIVE) {
                    mRightButton.setBackgroundResource(R.drawable.dialog_button_bg);
                } else if (whichButtons == BIT_BUTTON_NEUTRAL) {
                    mMiddleButton.setBackgroundResource(R.drawable.dialog_button_bg);
                } else if (whichButtons == BIT_BUTTON_NEGATIVE) {
                    mLeftButton.setBackgroundResource(R.drawable.dialog_button_bg);
                }
            }
        }
    }

    private boolean hasButtons(){
        return mButtonArray.indexOfValue(true) >= 0;
    }

    private int getButtons(){
        int count = 0;
        for (int i = 0; i < mButtonArray.size(); i++) {
            if (mButtonArray.valueAt(i)) {
                count ++ ;
            }
        }
        return count;
    }

    private boolean hasButton(int whichButton){
        return mButtonArray.get(whichButton);
    }

    public BaseDialog setDialogTitle(CharSequence title) {
        mShowTitle = true;
        mTitle = (String) title;
        return this;
    }

    public BaseDialog setDialogTitle(int resId) {
        String title = mContext.getString(resId);
        return setDialogTitle(title);
    }

    public BaseDialog setDialogMessage(int textResId) {
        String msg = mContext.getString(textResId);
        return setDialogMessage(msg);
    }

    public BaseDialog setRightMenu(String text) {
        mMenuText = text;
        return this;
    }

    public BaseDialog setDialogMessage(CharSequence msg) {
        mHasMessage = true;
        mMessage = (String) msg;
        return this;
    }

    public BaseDialog setDialogIcon(int drawableResId) {
        mIconResId = drawableResId;
        return this;
    }

    /**
     *
     * @param whichButton 设置是哪个Button，可以是这其中的一个
     *                  {@link #BUTTON_POSITIVE},
     *                  {@link #BUTTON_NEUTRAL}, or
     *                  {@link #BUTTON_NEGATIVE}
     * @param text 按钮文字
     * @param listener 按钮点击监听事件
     */
    public BaseDialog setButton(int whichButton, String text, onMMDialogClickListener listener){
        mButtonArray.put(whichButton, true);
        switch (whichButton) {
            case BUTTON_POSITIVE:
                mPositiveMessage = text;
                mPositiveListener = listener;
                break;
            case BUTTON_NEUTRAL:
                mNeutralMessage = text;
                mNeutralListener = listener;
                break;
            case BUTTON_NEGATIVE:
                mNegativeMessage = text;
                mNegativeListener = listener;
                break;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }

        return this;
    }

    public BaseDialog setButton(int whichButton, int textId, onMMDialogClickListener listener){
        return setButton(whichButton, mContext.getString(textId), listener);
    }

    /**
     * 设定“取消”类型按钮文字和事件
     */
    public BaseDialog setNegativeButton(String text, onMMDialogClickListener listener) {
        return setButton(BUTTON_NEGATIVE, text, listener);
    }

    /**
     * 设定“取消”类型按钮文字和事件
     */
    public BaseDialog setNegativeButton(int textId, onMMDialogClickListener listener) {
        String text = mContext.getString(textId);
        return setNegativeButton(text, listener);
    }

    /**
     * 设置“确定”类型按钮文字和监听事件
     */
    public BaseDialog setPositiveButton(String text, onMMDialogClickListener listener) {
        return setButton(BUTTON_POSITIVE, text, listener);
    }

    /**
     * 设置“确定”类型按钮文字和监听事件
     */
    public BaseDialog setPositiveButton(int textId, onMMDialogClickListener listener) {
        String text = mContext.getString(textId);
        return setPositiveButton(text, listener);
    }

    /**
     * 设定中性属性按钮（确定，取消类型以外的）类型文字和监听事件
     */
    public BaseDialog setNeutralButton(String text, onMMDialogClickListener listener) {
        return setButton(BUTTON_NEUTRAL, text, listener);
    }

    /**
     * 设定中性属性按钮（确定，取消类型以外的）类型文字和监听事件
     * @param textId
     * @param listener
     */
    public BaseDialog setNeutralButton(int textId, onMMDialogClickListener listener) {
        String text = mContext.getString(textId);
        return setPositiveButton(text, listener);
    }

    public BaseDialog setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        mCustomView = customView;
        return this;
    }

    public BaseDialog setCustomView(View view) {
        mCustomView = view;
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

}