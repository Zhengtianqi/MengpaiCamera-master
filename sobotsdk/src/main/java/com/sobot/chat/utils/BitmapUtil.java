package com.sobot.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.FileInputStream;

public class BitmapUtil {
    public static void display(Context context, String url,
                               ImageView imageView, Drawable defaultPic, Drawable error) {
        Picasso.with(context).load(url)
                .placeholder(defaultPic) // 设置等待的图片
                .error(error)// 加载错误显示的图片
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    public static void display(Context context, String url,
                               ImageView imageView, int defaultPic, int error) {
        Picasso.with(context).load(url)
                .placeholder(defaultPic) // 设置等待的图片
                .error(error)// 加载错误显示的图片
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    @SuppressWarnings("deprecation")
    public static void display(Context context, String url, ImageView imageView) {

        if (!url.startsWith("http")) {
            url = "file://" + url;
        }
        Picasso.with(context).load(url)
                .placeholder(ResourceUtils.getIdByName(context, "drawable",
                        "sobot_default_pic")) // 设置等待的图片
                .fit().centerCrop()
                .config(Bitmap.Config.RGB_565)
                .error(ResourceUtils.getIdByName(context, "drawable",
                        "sobot_default_pic_err"))// 加载错误显示的图片
                .into(imageView);
    }

    public static void display(Context context, int resourceId, ImageView imageView) {
        Picasso.with(context).load(resourceId)
                .into(imageView);
    }

    /**
     * 加载头像的方法
     * @param context
     * @param url 头像的路径
     * @param imageView
     * @param defId 默认图片的id
     */
    public static void displayRound(Context context, String url, ImageView imageView, int defId) {
        Picasso.with(context).load(url)
//				.transform(new PicassoRoundTransform(8))
                .placeholder(defId) // 设置等待的图片
                .error(defId)// 加载错误显示的图片
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    /**
     * 加载头像的方法
     * @param context
     * @param resId 头像的资源ID
     * @param imageView
     * @param defId 默认图片的id
     */
    public static void displayRound(Context context, int resId, ImageView imageView, int
            defId) {
        Picasso.with(context).load(resId)
//				.transform(new PicassoRoundTransform(8))
                .placeholder(defId) // 设置等待的图片
                .error(defId)// 加载错误显示的图片
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }

    public static boolean isGif(String srcFileName) {
        FileInputStream imgFile = null;
        byte[] b = new byte[3];
        int l = -1;
        try {
            imgFile = new FileInputStream(srcFileName);
            l = imgFile.read(b);
            imgFile.close();
        } catch (Exception e) {
            return false;
        }
        if (l == 3) {
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];
            if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static Bitmap compress(String filePath, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置后decode图片不会返回一个bitmap对象，但是会将图片的信息封装到Options中
        BitmapFactory.decodeFile(filePath, options);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        options.inSampleSize = calculateInSampleSize(options, width,height);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算采样大小
     *
     * @param options   选项
     * @param reqWidth  最大宽度
     * @param reqHeight 最大高度
     * @return 采样大小
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}