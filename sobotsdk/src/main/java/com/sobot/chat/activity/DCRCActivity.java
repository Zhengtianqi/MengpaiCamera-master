package com.sobot.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.callback.StringResultCallBack;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

public class DCRCActivity extends Activity implements OnClickListener {

    private RelativeLayout sobot_out_side_id;
    private LinearLayout hideLayout;/* 点击"否"按钮以后显示的布局 */
    private LinearLayout sobot_button_style;/* 立即结束和取消按钮都有 */
    private RelativeLayout sobot_robot;
    private RatingBar sobot_ratingBar;/* 评价人工的五颗星 */
    private GridView gv_demo;
    private MyAdapter adapter;
    private float score = 0f;/* 打分 */
    private Button btnCancle, btnSubmit;/* 取消按钮，提交评价按钮 */
    private RadioButton sobot_btn_no_robot;/* 取消按钮，提交评价按钮 */
    private RadioButton sobot_btn_ok_robot;/* 取消按钮，提交评价按钮 */
    private Button sobot_close_now;/* 立即结束 */

    private EditText sobot_add_content;
    private TextView sobot_center_title;
    private TextView sobot_center_title_tip;


    private String current_client_model;/* 当前模式 */
    private String robotCommentTitle;/* 机器人评价语 */
    private String manualCommentTitle;/* 客服评价语 */
    private String cid, uid;
    private int  mCommentType;/*mCommentType 评价类型 主动评价1 邀请评价0*/
    private ZhiChiApi zhiChiApi;
    private List<String> listChecked = new ArrayList<String>();
    private Bundle intentBundle;
    private boolean isShowFinish;//是否显示暂不评价按钮
    boolean mEvaluationCompletedExit = false;
    //用户提交人工满意度评价后释放会话

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        setContentView(ResourceUtils.getIdByName(getApplicationContext(), "layout", "sobot_dialog"));

        initBundleData(savedInstanceState);
        initView();
        initListener();
        initAdapter();
    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (null != getIntent()) {
                intentBundle = getIntent().getBundleExtra("bundle");
            }
        } else {
            intentBundle = savedInstanceState.getBundle("bundle");
        }

        current_client_model = intentBundle.getString("current_client_model");
        robotCommentTitle = intentBundle.getString("robotCommentTitle");
        manualCommentTitle = intentBundle.getString("manualCommentTitle");
        cid = intentBundle.getString("cid");
        uid = intentBundle.getString("uid");
        mCommentType = intentBundle.getInt("commentType");
        isShowFinish = intentBundle.getBoolean("isShowFinish");

        mEvaluationCompletedExit = SharedPreferencesUtil.getBooleanData
                (getApplicationContext(),ZhiChiConstant.SOBOT_CHAT_EVALUATION_COMPLETED_EXIT,false);
    }


    public int getResId(String name) {
        return ResourceUtils.getIdByName(DCRCActivity.this, "id", name);
    }

    public String getResString(String name) {
        return getResources().getString(getResStringId(name));
    }

    public int getResStringId(String name) {
        return ResourceUtils.getIdByName(DCRCActivity.this, "string", name);
    }

    public int getResDrawableId(String name) {
        return ResourceUtils.getIdByName(DCRCActivity.this, "drawable", name);
    }

    private void initView() {
        sobot_out_side_id = (RelativeLayout) findViewById(getResId("sobot_out_side_id"));
        gv_demo = (GridView) findViewById(getResId("gv_demo"));
        sobot_ratingBar = (RatingBar) findViewById(getResId("sobot_ratingBar"));
        hideLayout = (LinearLayout) findViewById(getResId("sobot_hide_layout"));
        btnCancle = (Button) findViewById(getResId("sobot_negativeButton"));
        btnSubmit = (Button) findViewById(getResId("sobot_btn_submit"));
        sobot_robot = (RelativeLayout) findViewById(getResId("sobot_robot"));

        sobot_btn_ok_robot = (RadioButton) findViewById(getResId("sobot_btn_ok_robot"));
        sobot_btn_no_robot = (RadioButton) findViewById(getResId("sobot_btn_no_robot"));
        sobot_add_content = (EditText) findViewById(getResId("sobot_add_content"));
        sobot_close_now = (Button) findViewById(getResId("sobot_close_now"));
        sobot_center_title = (TextView) findViewById(getResId("sobot_center_title"));
        sobot_center_title_tip = (TextView) findViewById(getResId("sobot_center_title_tip"));

        sobot_button_style = (LinearLayout) findViewById(getResId("sobot_button_style"));

        if (Integer.parseInt(current_client_model) == ZhiChiConstant.client_model_robot) {
            sobot_center_title.setText(getResString("sobot_question"));
            sobot_ratingBar.setVisibility(View.GONE);
        } else if (Integer.parseInt(current_client_model) == ZhiChiConstant.client_model_customService) {
            sobot_center_title.setText(getResString("sobot_dcrc"));
            sobot_robot.setVisibility(View.GONE);
            //显示提醒
            sobot_center_title_tip.setVisibility(mEvaluationCompletedExit?View.VISIBLE:View.GONE);
        }

        if (isShowFinish) {
            sobot_button_style.setVisibility(View.VISIBLE);
        } else {
            sobot_button_style.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        sobot_out_side_id.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(sobot_out_side_id);
            }
        });
        sobot_ratingBar
                .setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar arg0, float arg1,
                                                boolean arg2) {
                        score = sobot_ratingBar.getRating();
                        if (0 < score && score < 5) {
                            hideLayout.setVisibility(View.VISIBLE);
                            sobot_button_style.setVisibility(View.GONE);
                        } else {
                            hideLayout.setVisibility(View.GONE);
                            comment("1", score + "", "", "", 0);
                        }
                    }
                });

        btnCancle.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        sobot_btn_no_robot.setOnClickListener(this);
        sobot_btn_ok_robot.setOnClickListener(this);
        sobot_close_now.setOnClickListener(this);

        gv_demo.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView text = (TextView) view.findViewById(getResId("sobot_every_case"));
                Boolean isChecked = (Boolean) text.getTag();
                if (!isChecked) {
                    text.setTextColor(getResources().getColor(ResourceUtils.getIdByName(getApplicationContext(),
                            "color","sobot_color_evaluate_text_pressed")));
                    text.setBackgroundResource(getResDrawableId("sobot_login_edit_pressed"));
                    listChecked.add(text.getText().toString());
                } else {
                    text.setTextColor(getResources().getColor(ResourceUtils.getIdByName(getApplicationContext(),
                            "color","sobot_color_evaluate_text_normal")));
                    text.setBackgroundResource(getResDrawableId("sobot_login_edit_nomal"));
                    listChecked.remove(text.getText().toString());
                }
                text.setTag(!isChecked);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean keyBoardShowing = SharedPreferencesUtil.getBooleanData(getApplicationContext(), "keyBoardShowing", false);
        if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
            if (keyBoardShowing) {
                KeyboardUtil.hideKeyboard(sobot_out_side_id);
            } else {
                finish();
            }
        }
        return true;
    }

    private boolean isOutOfBounds(Activity context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = context.getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
    }

    private void initAdapter() {
        String tmpData[] = null;
        List<String> data = new ArrayList<String>();
        data.clear();
        if (Integer.parseInt(current_client_model) == ZhiChiConstant.client_model_robot) {
            tmpData = convertStrToArray(robotCommentTitle);
        } else if (Integer.parseInt(current_client_model) == ZhiChiConstant.client_model_customService) {
            tmpData = convertStrToArray(manualCommentTitle);
        }

        if (tmpData != null) {
            for (int i = 0; i < tmpData.length; i++) {
                data.add(tmpData[i]);
            }
        }

        adapter = new MyAdapter();
        gv_demo.setAdapter(adapter);
        adapter.addDatas(data);
    }

    @Override
    public void onClick(View v) {
        StringBuffer str = new StringBuffer();
        String suggest = sobot_add_content.getText().toString();

        for (int i = 0; i < listChecked.size(); i++) {
            str.append(listChecked.get(i) + ",");
        }

        if (v == btnSubmit) { /* 提交评价按钮 */
            if (Integer.parseInt(current_client_model) == ZhiChiConstant.client_model_robot) {
                comment("0", "", str + "", suggest, 1);
            } else {
                comment("1", score + "", str + "", suggest, 1);
            }
            hideLayout.setVisibility(View.GONE);
        }

        if (v == btnCancle) {
            finish();
        }

        if (v == sobot_btn_ok_robot) {
            comment("0", "", "", "", 0);
        }

        if (v == sobot_btn_no_robot) {
            sobot_button_style.setVisibility(View.GONE);
            hideLayout.setVisibility(View.VISIBLE);
        }

        if (v == sobot_close_now) {
            finish();
            Intent intent = new Intent();
            intent.setAction(ZhiChiConstants.sobot_close_now);/*立即结束*/
            CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
        }
    }

    /* 评价谁，人工客服分数，人工客服和机器人客服的问题，人工客服和机器人客服的建议，机器人客服是否解决问题 */
    private void comment(String type, String source, String problem,
                         String suggest, int isresolve) {

        zhiChiApi = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        zhiChiApi.comment(cid, uid, type, source, problem, suggest, isresolve,mCommentType,
                new StringResultCallBack<CommonModel>() {
                    @Override
                    public void onSuccess(CommonModel result) {
                        //评论成功 发送广播
                        Intent intent = new Intent();
                        intent.setAction(ZhiChiConstants.dcrc_comment_state);
                        intent.putExtra("commentState", true);
                        intent.putExtra("isFinish", isShowFinish);
                        CommonUtils.sendLocalBroadcast(getApplicationContext(), intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception arg0, String arg1) {
                        LogUtils.i("失败" + arg1 + "***" + arg0.toString());
                    }
                });
    }

    public class MyAdapter extends BaseAdapter {

        private List<String> list = new ArrayList<String>();

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        ResourceUtils.getIdByName(DCRCActivity.this, "layout",
                                "sobot_gridview_item"), null);
                viewHolder = new ViewHolder();
                viewHolder.sobot_every_case = (TextView) convertView
                        .findViewById(getResId("sobot_every_case"));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.sobot_every_case.setText(list.get(position));
            viewHolder.sobot_every_case.setTag(false);
            return convertView;
        }

        public void addDatas(List<String> list) {
            this.list.clear();
            this.list.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView sobot_every_case;
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private static String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putBundle("bundle", intentBundle);
        super.onSaveInstanceState(outState);
    }
}