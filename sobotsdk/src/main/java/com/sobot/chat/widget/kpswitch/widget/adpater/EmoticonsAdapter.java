package com.sobot.chat.widget.kpswitch.widget.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.widget.kpswitch.widget.data.EmoticonPageEntity;
import com.sobot.chat.widget.kpswitch.widget.interfaces.EmoticonClickListener;
import com.sobot.chat.widget.kpswitch.widget.interfaces.EmoticonDisplayListener;

import java.util.ArrayList;

public class EmoticonsAdapter<T> extends BaseAdapter {

    protected final int DEF_HEIGHTMAXTATIO = 2;
    protected final int mDefalutItemHeight;

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<T> mData = new ArrayList<>();
    protected EmoticonPageEntity mEmoticonPageEntity;
    protected double mItemHeightMaxRatio;
    protected int mItemHeightMax;
    protected int mItemHeightMin;
    protected int mItemHeight;
    protected int mDelbtnPosition;
    protected EmoticonDisplayListener mOnDisPlayListener;
    protected EmoticonClickListener mOnEmoticonClickListener;

    public EmoticonsAdapter(Context context, EmoticonPageEntity emoticonPageEntity, EmoticonClickListener onEmoticonClickListener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mEmoticonPageEntity = emoticonPageEntity;
        this.mOnEmoticonClickListener = onEmoticonClickListener;
        this.mItemHeightMaxRatio = DEF_HEIGHTMAXTATIO;
        this.mDelbtnPosition = -1;
        this.mDefalutItemHeight = this.mItemHeight = (int) context.getResources().getDimension
                (getResDimenId("sobot_item_emoticon_size_default"));
        this.mData.addAll(emoticonPageEntity.getEmoticonList());
        checkDelBtn(emoticonPageEntity);
    }

    private void checkDelBtn(EmoticonPageEntity entity) {
        EmoticonPageEntity.DelBtnStatus delBtnStatus = entity.getDelBtnStatus();
        if (EmoticonPageEntity.DelBtnStatus.GONE.equals(delBtnStatus)) {
            return;
        }
        if (EmoticonPageEntity.DelBtnStatus.FOLLOW.equals(delBtnStatus)) {
            mDelbtnPosition = getCount();
            mData.add(null);
        } else if (EmoticonPageEntity.DelBtnStatus.LAST.equals(delBtnStatus)) {
            int max = entity.getLine() * entity.getRow();
            while (getCount() < max) {
                mData.add(null);
            }
            mDelbtnPosition = getCount() - 1;
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(getResLayoutId("sobot_list_item_emoticon"), null);
            viewHolder.rootView = convertView;
            viewHolder.ly_root = (LinearLayout) convertView.findViewById(getResId("sobot_ly_root"));
            viewHolder.iv_emoticon = (ImageView) convertView.findViewById(getResId("sobot_iv_emoticon"));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindView(position, parent, viewHolder);
        updateUI(viewHolder, parent);
        return convertView;
    }

    protected void bindView(int position, ViewGroup parent, ViewHolder viewHolder) {
        if (mOnDisPlayListener != null) {
            mOnDisPlayListener.onBindView(position, parent, viewHolder, mData.get(position), position == mDelbtnPosition);
        }
    }

    protected boolean isDelBtn(int position) {
        return position == mDelbtnPosition;
    }

    protected void updateUI(ViewHolder viewHolder, ViewGroup parent) {
        if(mDefalutItemHeight != mItemHeight){
            viewHolder.iv_emoticon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight));
        }
        mItemHeightMax = this.mItemHeightMax != 0 ? this.mItemHeightMax : (int) (mItemHeight * mItemHeightMaxRatio);
        mItemHeightMin = this.mItemHeightMin != 0 ? this.mItemHeightMin : mItemHeight;
        int realItemHeight = ((View) parent.getParent()).getMeasuredHeight() / mEmoticonPageEntity.getLine();
        realItemHeight = Math.min(realItemHeight, mItemHeightMax);
        realItemHeight = Math.max(realItemHeight, mItemHeightMin);
        viewHolder.ly_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, realItemHeight));
    }

    public void setOnDisPlayListener(EmoticonDisplayListener mOnDisPlayListener) {
        this.mOnDisPlayListener = mOnDisPlayListener;
    }

    public void setItemHeightMaxRatio(double mItemHeightMaxRatio) {
        this.mItemHeightMaxRatio = mItemHeightMaxRatio;
    }

    public void setItemHeightMax(int mItemHeightMax) {
        this.mItemHeightMax = mItemHeightMax;
    }

    public void setItemHeightMin(int mItemHeightMin) {
        this.mItemHeightMin = mItemHeightMin;
    }

    public void setItemHeight(int mItemHeight) {
        this.mItemHeight = mItemHeight;
    }

    public void setDelbtnPosition(int mDelbtnPosition) {
        this.mDelbtnPosition = mDelbtnPosition;
    }

    public static class ViewHolder {
        public View rootView;
        public LinearLayout ly_root;
        public ImageView iv_emoticon;
    }

    public int getResId(String name) {
        return ResourceUtils.getIdByName(mContext, "id", name);
    }

    public int getResDimenId(String name) {
        return ResourceUtils.getIdByName(mContext, "dimen", name);
    }

    public int getResLayoutId(String name) {
        return ResourceUtils.getIdByName(mContext, "layout", name);
    }
}