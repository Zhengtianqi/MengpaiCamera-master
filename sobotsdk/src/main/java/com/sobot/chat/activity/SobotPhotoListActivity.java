package com.sobot.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sobot.chat.activity.base.SobotBaseActivity;
import com.sobot.chat.adapter.base.SobotImageScaleAdapter;
import com.sobot.chat.api.model.ZhiChiUploadAppFileModelResult;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotDeleteWorkOrderDialog;
import com.sobot.chat.widget.photoview.HackyViewPager;

import java.util.ArrayList;

/**
 * 图片列表
 * Created by jxl on 2016/5/22.
 */
public class SobotPhotoListActivity extends SobotBaseActivity {

    private ArrayList<ZhiChiUploadAppFileModelResult> pic_list;//全部的图片集合
    private int currentPic;//当前的图片
    private HackyViewPager viewPager;//骇客
    protected SobotDeleteWorkOrderDialog seleteMenuWindow;
    private SobotImageScaleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(getApplicationContext(), "layout", "sobot_activity_photo_list"));

        String bg_color = SharedPreferencesUtil.getStringData(this, "robot_current_themeColor", "");
        if (bg_color != null && bg_color.trim().length() != 0) {
            relative.setBackgroundColor(Color.parseColor(bg_color));
        }

        int robot_current_themeImg = SharedPreferencesUtil.getIntData(this, "robot_current_themeImg", 0);
        if (robot_current_themeImg != 0) {
            relative.setBackgroundResource(robot_current_themeImg);
        }

        if(savedInstanceState == null){
            Intent intent = getIntent();
            pic_list = (ArrayList<ZhiChiUploadAppFileModelResult>) intent.getSerializableExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
            currentPic = intent.getIntExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, 0);
        } else {
            pic_list = (ArrayList<ZhiChiUploadAppFileModelResult>) savedInstanceState.getSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST);
            currentPic = savedInstanceState.getInt(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM);
        }
        initData();
        initTitle();
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putInt(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST_CURRENT_ITEM, currentPic);
        outState.putSerializable(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST,pic_list);
        super.onSaveInstanceState(outState);
    }

    private void initData() {

        viewPager = (HackyViewPager) findViewById(getResId("sobot_viewPager"));
        //填充数据
        adapter = new SobotImageScaleAdapter(SobotPhotoListActivity.this, pic_list);
        viewPager.setAdapter(adapter);
        //设置默认选中的页
        viewPager.setCurrentItem(currentPic);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTitlePageNum(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTitle() {
        showRightView(getResDrawableId("sobot_pic_delete_selector"),"",true);
        setTitlePageNum(currentPic);
        sobot_tv_left.setOnClickListener(this);
        showLeftView(getResString("sobot_back"),getResDrawableId("sobot_btn_back_selector"));
    }

    public void setTitlePageNum(int currentPic) {
        setTitle((currentPic + 1) + "/" + pic_list.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            seleteMenuWindow.dismiss();
            if(v.getId() == getResId("btn_pick_photo")){
                //删除
                int currentItem = viewPager.getCurrentItem();
                String url = pic_list.get(currentItem).getFileUrl();
                Intent intent = new Intent();
                intent.putExtra(ZhiChiConstant.SOBOT_KEYTYPE_PIC_LIST,pic_list);
                setResult(ZhiChiConstant.SOBOT_KEYTYPE_DELETE_FILE_SUCCESS,intent);
                pic_list.remove(viewPager.getCurrentItem());
                if (pic_list.size() == 0) {
                    finish();
                } else {
                    adapter = new SobotImageScaleAdapter(SobotPhotoListActivity.this, pic_list);
                    viewPager.setAdapter(adapter);
                }
            }
        }
    };

    @Override
    public void forwordMethod() {
        seleteMenuWindow = new SobotDeleteWorkOrderDialog(SobotPhotoListActivity.this,"要删除这张图片吗？", itemsOnClick);
        seleteMenuWindow.show();
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_tv_left) {
            finish();
        }
    }
}
