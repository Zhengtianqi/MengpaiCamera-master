package com.sobot.chat.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

@SuppressLint("ViewConstructor")
public class SelectPicPopupWindow extends PopupWindow {

	private Button sobot_btn_take_photo, sobot_btn_cancel;
	private View mView;
	private String imgUrl;
	private Context context;
	private String type;
	private LayoutInflater inflater;
	private String uid;

	public SelectPicPopupWindow(final Activity context,String uid){
		this.context = context;
		this.uid = uid;
		initView();
	}

	@SuppressWarnings("deprecation")
	public SelectPicPopupWindow(final Activity context,String url,String type) {
		super(context);
		imgUrl = url;
		this.type = type;
		this.context = context.getApplicationContext();
		initView();
	}

	private void initView(){
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(ResourceUtils.getIdByName(context,"layout","sobot_picture_popup"), null);
		sobot_btn_take_photo = (Button) mView.findViewById(ResourceUtils.getIdByName(context,"id","sobot_btn_take_photo"));
		sobot_btn_cancel = (Button) mView.findViewById(ResourceUtils.getIdByName(context,"id","sobot_btn_cancel"));

		// 设置SelectPicPopupWindow的View
		this.setContentView(mView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(ResourceUtils.getIdByName(context,"style","AnimBottom"));
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = mView.findViewById(ResourceUtils.getIdByName(context,"id","sobot_pop_layout")).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

		if(!TextUtils.isEmpty(imgUrl)){
			sobot_btn_take_photo.setTextColor(context.getResources()
					.getColor(ResourceUtils.getIdByName(context, "color", "sobot_color_evaluate_text_btn")));
			sobot_btn_cancel.setTextColor(context.getResources()
					.getColor(ResourceUtils.getIdByName(context, "color", "sobot_color_evaluate_text_btn")));
			// 取消按钮
			sobot_btn_cancel.setOnClickListener(savePictureOnClick);
			// 设置按钮监听
			sobot_btn_take_photo.setOnClickListener(savePictureOnClick);
		}
	}

	// 为弹出窗口popupwindow实现监听类
	private OnClickListener savePictureOnClick = new OnClickListener() {
		public void onClick(View v) {
			dismiss();
			if (v == sobot_btn_take_photo){
				LogUtils.i("imgUrl:" + imgUrl);
				if (type.equals("gif")){
					saveImageToGallery(context,imgUrl);
				}else{
					Bitmap bitmap = BitmapUtil.compress(imgUrl,context);
					saveImageToGallery(context, bitmap);
				}
			}

			if (v == sobot_btn_cancel){

			}
		}
	};

	private void showHint(String content){
		CustomToast mCToast = CustomToast.makeText(context, content, 1000,
				ResourceUtils.getIdByName(context,"drawable","sobot_iv_login_right"));
		mCToast.show();
	}

	public void saveImageToGallery(Context context, Bitmap bmp) {
		if(!isSdCardExist()){
			ToastUtil.showToast(context,"保存失败，sd卡不存在");
			return;
		}
		if (bmp == null){
			ToastUtil.showToast(context, "保存失败，图片不存在");
			return;
		}
		String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File
				.separator + "Sobot";
		// 首先保存图片
		File appDir = new File(savePath, "sobot_pic");
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			ToastUtil.showToast(context, "保存失败，文件未发现");
			e.printStackTrace();
		} catch (IOException e) {
			ToastUtil.showToast(context, "保存失败");
			e.printStackTrace();
		}catch (Exception e){
			ToastUtil.showToast(context, "保存失败");
			e.printStackTrace();
		}

		notifyUpdatePic(file, fileName);
	}

	public boolean isSdCardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public void saveImageToGallery(Context context, String bmp) {
		if(!isSdCardExist()){
			ToastUtil.showToast(context,"保存失败，sd卡不存在");
			return;
		}
		if (TextUtils.isEmpty(bmp)){
			ToastUtil.showToast(context, "保存失败，图片不存在");
			return;
		}
		String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File
				.separator + "Sobot";
		// 首先保存图片
		File appDir = new File(savePath, "sobot_pic");
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		String fileName = System.currentTimeMillis() + ".gif";
		File file = new File(appDir, fileName);
		if(fileChannelCopy(new File(bmp),file)){
			notifyUpdatePic(file,fileName);
		}
	}

	// 最后通知图库更新
	public void notifyUpdatePic(File file,String fileName){
//		try {
//			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		context.sendBroadcast(intent);
		showHint(ResourceUtils.getIdByName(context,"string","sobot_already_save_to_picture") + "");
	}

	/**
	 * 使用文件通道的方式复制文件
	 *
	 * @param s
	 *            源文件
	 * @param t
	 *            复制到的新文件
	 */
	public boolean fileChannelCopy(File s, File t) {
		boolean isSuccess = true;
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();//得到对应的文件通道
			out = fo.getChannel();//得到对应的文件通道
			in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			isSuccess = false;
			ToastUtil.showToast(context, "保存失败!");
			e.printStackTrace();
		} finally {
			try {
				if(fi!=null){
					fi.close();
				}
				if(in!=null){
					in.close();
				}
				if(fo!=null){
					fo.close();
				}
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				isSuccess = false;
			}
		}
		return true;
	}
}