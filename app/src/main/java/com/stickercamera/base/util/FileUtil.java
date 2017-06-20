package com.stickercamera.base.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 文件读取
 */
public class FileUtil {

    private Context context;

    public FileUtil(Context context) {
        this.context = context;
    }

    //读取模板路径文件
    public String readAsset(String fileName) {
        AssetManager am = context.getAssets();

        String data = "";

        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = readDataFromInputStream(is);
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String readDataFromInputStream(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is);

        String str = "", s = "";

        int c = 0;
        byte[] buf = new byte[1024];
        while (true) {
            try {
                c = bis.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (c == -1)
                break;
            else {
                try {
                    s = new String(buf, 0, c, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                str += s;
            }
        }

        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }


    //保存图片
    public static File saveBitmapJPG(Context context, String mBitmapName, Bitmap mBitmap) throws IOException {

        File fileDir = new File(context.getExternalCacheDir() + "/dd");
        if (!fileDir.exists()) fileDir.mkdirs();
        String fileName = "dd" + mBitmapName + ".jpg";
        File f = new File(fileDir, fileName);
        FileOutputStream fOut = new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
        mBitmap.recycle();
        mBitmap = null;
        return f;
    }
}
