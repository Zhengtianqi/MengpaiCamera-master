package com.stickercamera.app.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stickercamera.base.util.AppUtil;
import com.stickercamera.base.util.DensityUtil;


/**
 */
public class PicItem extends RelativeLayout {

    private ImageView mImageView;

    public PicItem(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        mImageView = new ImageView(context);
        mImageView.setAdjustViewBounds(true);
        mImageView.setPadding(DensityUtil.dip2px(context, 2), DensityUtil.dip2px(context, 2), DensityUtil.dip2px(context, 2), DensityUtil.dip2px(context, 2));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LayoutParams mImageViewRLP = new LayoutParams(AppUtil.getScreenWidth(context) / 4, AppUtil.getScreenWidth(context) / 4);
        mImageViewRLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mImageView, mImageViewRLP);
    }

    public ImageView getmImageView() {
        return mImageView;
    }
}
