package com.stickercamera.app.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.skykai.stickercamera.R;
import com.stickercamera.base.util.DensityUtil;


/**
 */
public class SelectItem extends RelativeLayout {


    private ImageView mImageView;
    private ImageView checkImg;

    public SelectItem(Context context) {
        super(context);
//        setGravity(Gravity.CENTER);
        mImageView = new ImageView(context);
        mImageView.setAdjustViewBounds(true);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams mImageViewRLP = new LayoutParams(DensityUtil.dip2px(context, 60), DensityUtil.dip2px(context, 60));
        mImageViewRLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageViewRLP.setMargins(0, DensityUtil.dip2px(context, 30), DensityUtil.dip2px(context, 20), 0);
        addView(mImageView, mImageViewRLP);

        checkImg = new ImageView(context);
        checkImg.setImageResource(R.drawable.delete_pic);
        LayoutParams checkImgRLP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkImgRLP.setMargins(DensityUtil.dip2px(context, 2), DensityUtil.dip2px(context, 5), 0, 0);
        addView(checkImg, checkImgRLP);
    }

    public ImageView getmImageView() {
        return mImageView;
    }

    public ImageView getCheckImg() {
        return checkImg;
    }
}
