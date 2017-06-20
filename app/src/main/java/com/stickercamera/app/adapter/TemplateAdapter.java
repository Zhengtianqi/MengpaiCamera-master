package com.stickercamera.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.stickercamera.base.util.DensityUtil;

import java.util.List;

/**
 */
public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {

    private Context context;
    private List<Integer> templateList;
    private OnRvItemClickListener onRvItemClickListener;

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
    }

    public TemplateAdapter(Context context, List<Integer> templateList) {

        this.context = context;
        this.templateList = templateList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 45), DensityUtil.dip2px(context, 60)));
        layoutParams.setMargins(DensityUtil.dip2px(context, 10), DensityUtil.dip2px(context, 5), DensityUtil.dip2px(context, 10), DensityUtil.dip2px(context, 5));
        ImageView imageView = new ImageView(context);
        relativeLayout.addView(imageView, layoutParams);

        ViewHolder viewHolder = new ViewHolder(relativeLayout);
        viewHolder.imageView = imageView;
        viewHolder.imageView.setPadding(DensityUtil.dip2px(context, 1), DensityUtil.dip2px(context, 1), DensityUtil.dip2px(context, 1), DensityUtil.dip2px(context, 1));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.imageView.setImageResource(templateList.get(position));

        if (onRvItemClickListener != null) {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRvItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnRvItemClickListener {
        void onItemClick(int position);
    }
}
