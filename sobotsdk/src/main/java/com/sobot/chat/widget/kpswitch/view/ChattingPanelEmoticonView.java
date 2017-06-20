package com.sobot.chat.widget.kpswitch.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.widget.emoji.DisplayRules;
import com.sobot.chat.widget.emoji.Emojicon;

import com.sobot.chat.widget.kpswitch.view.emoticon.EmoticonPageView;
import com.sobot.chat.widget.kpswitch.view.emoticon.EmoticonsFuncView;
import com.sobot.chat.widget.kpswitch.view.emoticon.EmoticonsIndicatorView;
import com.sobot.chat.widget.kpswitch.widget.adpater.EmoticonsAdapter;
import com.sobot.chat.widget.kpswitch.widget.adpater.PageSetAdapter;
import com.sobot.chat.widget.kpswitch.widget.data.EmoticonPageEntity;
import com.sobot.chat.widget.kpswitch.widget.data.EmoticonPageSetEntity;
import com.sobot.chat.widget.kpswitch.widget.data.PageSetEntity;
import com.sobot.chat.widget.kpswitch.widget.interfaces.EmoticonClickListener;
import com.sobot.chat.widget.kpswitch.widget.interfaces.EmoticonDisplayListener;
import com.sobot.chat.widget.kpswitch.widget.interfaces.PageViewInstantiateListener;

/**
 * 聊天面板   表情
 */
public class ChattingPanelEmoticonView extends BaseChattingPanelView implements EmoticonsFuncView.OnEmoticonsPageViewListener {

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;

    public ChattingPanelEmoticonView(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        final View view = View.inflate(context, getResLayoutId("sobot_emoticon_layout"), null);
        return view;
    }

    @Override
    public void initData() {
        mEmoticonsFuncView = (EmoticonsFuncView) getRootView().findViewById(getResId("view_epv"));
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) getRootView().findViewById(getResId("view_eiv")));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        setAdapter();
    }

    public void setAdapter() {
        PageSetAdapter pageSetAdapter = new PageSetAdapter();
        EmoticonPageSetEntity kaomojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(4)
                .setRow(7)
                .setEmoticonList(DisplayRules.getListAll(context))
                .setIPageViewInstantiateItem(new PageViewInstantiateListener<EmoticonPageEntity>() {
                    //每个表情加载的回调
                    @Override
                    public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                        if (pageEntity.getRootView() == null) {
                            //下面这个view  就是一个gridview
                            EmoticonPageView pageView = new EmoticonPageView(container.getContext());
                            pageView.setNumColumns(pageEntity.getRow());
                            pageEntity.setRootView(pageView);
                            try {
                                EmoticonsAdapter adapter = new EmoticonsAdapter(container.getContext(), pageEntity, emoticonClickListener);
                                adapter.setItemHeightMaxRatio(1.8);
                                adapter.setOnDisPlayListener(getEmoticonDisplayListener(emoticonClickListener));
                                pageView.getEmoticonsGridView().setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return pageEntity.getRootView();
                    }
                })
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .build();
        pageSetAdapter.add(kaomojiPageSetEntity);
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    /**
     * 这个是adapter里面的bindview回调
     * 作用就是绑定数据用的
     *
     * @param emoticonClickListener 点击表情的回调
     * @return
     */
    public EmoticonDisplayListener<Object> getEmoticonDisplayListener(final EmoticonClickListener emoticonClickListener) {
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                final Emojicon emoticonEntity = (Emojicon) object;
                if (emoticonEntity == null && !isDelBtn) {
                    return;
                }
                //每个表情的背景
                viewHolder.ly_root.setBackgroundResource(getResDrawableId("sobot_bg_emoticon"));

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(getResDrawableId
                            ("sobot_emoticon_del_selector"));
                } else {
                    BitmapUtil.display(context,emoticonEntity.getResId(), viewHolder.iv_emoticon);
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emoticonClickListener != null) {
                            emoticonClickListener.onEmoticonClick(emoticonEntity, isDelBtn);
                        }
                    }
                });
            }
        };
    }

    @Override
    public String getRootViewTag() {
        return "ChattingPanelEmoticonView";
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, boolean isDelBtn) {
            SobotChatActivity act = (SobotChatActivity)context;
            if (isDelBtn) {
                act.backspace();
            } else {
                act.inputEmoticon((Emojicon) o);
            }
        }
    };

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
//        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }
}
