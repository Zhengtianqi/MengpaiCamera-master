package com.stickercamera.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.skykai.stickercamera.R;
import com.stickercamera.app.adapter.TemplateAdapter;
import com.stickercamera.base.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择模板
 */
public class TemplateDialog {

    private Context context;
    private Dialog dialog;
    private RecyclerView recyclerView;
    //    private HListView hListView;
    private List<Integer> templateList;
    private OnItemClickListener onItemClickListener;
    private TemplateAdapter templateAdapter;

    public TemplateDialog(Context context, int size) {

        this.context = context;

        templateList = new ArrayList<>();
        calculatePics(size);
        init();
    }

    private void init() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {

        dialog = new Dialog(context, R.style.Theme_DataSheet);
        recyclerView = new RecyclerView(context);
        recyclerView.setBackgroundColor(Color.parseColor("#d0d0d0"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//        hListView = new HListView(context);
//        hListView.setBackgroundColor(context.getResources().getColor(R.color.contents_text));
//        hListView.setPadding(DensityTool.dip2px(context,10),0,0,0);
//        hListView.setSelector(R.color.rgb_225_225_225);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 60));

        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.2f;
        lp.gravity = Gravity.BOTTOM;
        dialog.onWindowAttributesChanged(lp);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(recyclerView, layoutParams);

    }

    private void initData() {

        templateAdapter = new TemplateAdapter(context, templateList);
        recyclerView.setAdapter(templateAdapter);
    }

    private void initEvent() {

        templateAdapter.setOnRvItemClickListener(new TemplateAdapter.OnRvItemClickListener() {

            public void onItemClick(int position) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemListener(position);
                }
            }
        });
//        hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.OnItemListener(position, id);
//                }
//            }
//        });
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void calculatePics(int size) {

        templateList.clear();
        switch (size) {
            case 2:
                templateList.add(R.drawable.icon_num_2_1);
                templateList.add(R.drawable.icon_num_2_2);
                break;
            case 3:
                templateList.add(R.drawable.icon_num_3_1);
                templateList.add(R.drawable.icon_num_3_2);
                break;
            case 4:
                templateList.add(R.drawable.icon_num_4_1);
                templateList.add(R.drawable.icon_num_4_2);
                break;
            case 5:
                templateList.add(R.drawable.icon_num_5_1);
                templateList.add(R.drawable.icon_num_5_2);
                break;
            default:
                break;
        }
    }

    public interface OnItemClickListener {

        void OnItemListener(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
