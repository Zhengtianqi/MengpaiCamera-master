package com.stickercamera.base.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.stickercamera.app.entity.ImageBean;

import java.util.ArrayList;
import java.util.Collections;

/**
 */
public class PicCursorUtil {

    public static ArrayList<ImageBean> getAllPhotos(Context context) {
        ArrayList<ImageBean> imageBeans = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver mContentResolver = context.getContentResolver();
            final String[] columns = {MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATA};
            // 构建查询条件，且只查询jpeg和png的图片
            StringBuilder selection = new StringBuilder();
            selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");
            selection.append(" or ");
            selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection.toString(), new String[]{
                    "image/jpeg", "image/png"
            }, orderBy);
            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.setPhotoDate(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                    imageBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    imageBeans.add(imageBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // show newest mCirclePhoto at beginning of the list
        Collections.reverse(imageBeans);

        return imageBeans;
    }
}
