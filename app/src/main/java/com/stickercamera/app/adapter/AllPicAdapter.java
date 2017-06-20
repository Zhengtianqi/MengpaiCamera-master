package com.stickercamera.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.skykai.stickercamera.R;
import com.stickercamera.app.entity.ImageBean;
import com.stickercamera.app.view.PicItem;

import java.util.List;

/**
 */
public class AllPicAdapter extends RecyclerView.Adapter<AllPicAdapter.ViewHolder> {

    private Context context;
    private List<ImageBean> picList;
    private OnRvItemClickListener onRvItemClickListener;

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
    }

    public AllPicAdapter(Context context, List<ImageBean> bean) {
        this.context = context;
        this.picList = bean;
    }

    public void taggle(List<ImageBean> bean) {
        this.picList = bean;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new PicItem(context);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImageView = ((PicItem) view).getmImageView();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context)
                .load("file://" + picList.get(position).path)
                .placeholder(R.mipmap.tubiao1)
                .crossFade()
                .into(holder.mImageView);

        if (onRvItemClickListener != null) {
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRvItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return picList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnRvItemClickListener {
        void onItemClick(int position);
    }
}
