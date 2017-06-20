package com.stickercamera.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;


/**
 * topView
 */
public class TopView extends RelativeLayout implements View.OnClickListener {

    private Context context;
    private RelativeLayout topBarRL;
    private TextView leftView;
    private TextView centerView;
    private TextView rightView;
    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;
    public final static int LEFT = 0;
    public final static int CENTER = 1;
    public final static int RIGHT = 2;


    public TopView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        initView();
        initBaseData();
        initEvent();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.top_bar, null);
        topBarRL = (RelativeLayout) view.findViewById(R.id.top_bar_rl);
        leftView = (TextView) view.findViewById(R.id.tv_left);
        centerView = (TextView) view.findViewById(R.id.tv_title);
        rightView = (TextView) view.findViewById(R.id.tv_right);
        addView(view);
    }

    private void initBaseData() {

        centerView.setText("标题");
    }

    private void initEvent() {
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);
    }

    public void setTitle(String title) {
        if (title != null) {
            centerView.setText(title);
        }
    }

    //隐藏某一个view
    public void hide(int STYLE) {

        switch (STYLE) {
            case LEFT:
                leftView.setVisibility(INVISIBLE);
                break;
            case CENTER:
                centerView.setVisibility(INVISIBLE);
                break;
            case RIGHT:
                rightView.setVisibility(INVISIBLE);
                break;
        }
    }

    public void setTitleColor(int color) {
        centerView.setTextColor(color);
    }

    public void setRightWord(String title) {
        if (title != null) {
            rightView.setText(title);
        }
    }

    public void setLeftIcon(int drawable) {

        leftView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawable), null, null, null);
    }

    public void setRightIcon(int drawable) {

        rightView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawable), null, null, null);
    }

    public void setBgColor(int color) {
        topBarRL.setBackgroundColor(color);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_left:
                onLeftClickListener.leftClick();
                break;

            case R.id.tv_right:
                onRightClickListener.rightClick();
                break;
        }
    }

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public interface OnLeftClickListener {
        void leftClick();
    }

    public interface OnRightClickListener {
        void rightClick();
    }
}
