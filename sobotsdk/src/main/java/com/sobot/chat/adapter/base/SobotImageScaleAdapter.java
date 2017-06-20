package com.sobot.chat.adapter.base;

import android.content.Context;
import android.view.ViewGroup;

import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.widget.photoview.PhotoView;

import java.util.ArrayList;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotImageScaleAdapter extends SobotBasePagerAdapter<ZhiChiUploadAppFileModelResult> {

    public SobotImageScaleAdapter(Context context, ArrayList<ZhiChiUploadAppFileModelResult> list) {
        super(context, list);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(context);

        BitmapUtil.display(context, list.get(position).getFileUrl(), imageView);

        //将ImageView加入到ViewPager中
        container.addView(imageView);
        return imageView;
    }

}