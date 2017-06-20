package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.VersionUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.ArrayList;

/**
 * 富文本消息
 * Created by jinxl on 2017/3/17.
 */
public class RichTextMessageHolder extends MessageHolderBase {
    public ZhiChiMessageBase message;
    private Context mContext;
    private LinearLayout sobot_real_ll_content;
    private TextView msg; // 聊天的消息内容
    private TextView sobot_msg_title; // 机会人回复的富文本标题
    private LinearLayout ll_content;
    private LinearLayout answersList;
    private LinearLayout my_msg;
    private TextView stripe;
    // 答案
    private ImageView bigPicImage; // 大的图片的展示
    private TextView rendAllText; // 阅读全文
    private View read_alltext_line;
    private ImageView simple_picture;// 单图片
    private TextView isGif;
    private RelativeLayout sobot_rl_real_pic;
    private TextView sobot_tv_transferBtn;
    private TextView sobot_tv_likeBtn;//机器人评价 顶 的按钮
    private TextView sobot_tv_dislikeBtn;//机器人评价 踩 的按钮

    public RichTextMessageHolder(Context context, View convertView){
        super(context,convertView);
        this.mContext = context;
        sobot_rl_real_pic = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_rl_real_pic"));
        isGif = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_pic_isgif"));
        sobot_real_ll_content = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context,"id","sobot_real_ll_content"));
        imgHead = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_imgHead"));
        name = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_name"));
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
        sobot_msg_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg_title"));
        read_alltext_line = convertView.findViewById(ResourceUtils.getIdByName(context, "id", "read_alltext_line"));

        // 纯图片的信息
        simple_picture = (ImageView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_simple_picture"));

        // 富文本的大图片
        bigPicImage = (ImageView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_bigPicImage"));
        // 阅读全文
        rendAllText = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_rendAllText"));


        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));


        ll_content = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_ll_content"));
        my_msg = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_my_msg"));

        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        this.message = message;
        isGif.setVisibility(View.GONE);
        sobot_rl_real_pic.setVisibility(View.GONE);
        sobot_msg_title.setVisibility(View.GONE);
        stripe.setText(null);
        // 更具消息类型进行对布局的优化
        if (message.getAnswer() != null) {
            // 获取的消息类型
            if ("0".equals(message.getAnswer().getMsgType())) {// 文本
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    msg.setVisibility(View.VISIBLE);
                    HtmlTools.getInstance(context).setRichText(msg, message.getAnswer().getMsg(),
                            ResourceUtils.getIdByName(context, "color", "sobot_color_link"));
                } else {
                    msg.setVisibility(View.GONE);
                    message.getAnswer().setMsg(null);
                }
            } else if ("1".equals(message.getAnswer().getMsgType())) {// 图片
                if (message.getAnswer() != null
                        && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    simple_picture.setVisibility(View.VISIBLE);
                    String picPath = CommonUtils.encode(message.getAnswer().getMsg());
                    sobot_rl_real_pic.setVisibility(View.VISIBLE);
                    if(picPath.endsWith("gif") || picPath.endsWith("GIF")){
                        isGif.setVisibility(View.VISIBLE);
                    }else{
                        isGif.setVisibility(View.GONE);
                    }
                    BitmapUtil.display(context, CommonUtils.encode(message.getAnswer().getMsg()), simple_picture);
                    simple_picture.setOnClickListener(new ImageClickLisenter(context,
                            message.getAnswer().getMsg()));
                } else {
                    simple_picture.setVisibility(View.GONE);
                    message.getAnswer().setMsg(null);
                }
            } else if ("3".equals(message.getAnswer().getMsgType())) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    msg.setVisibility(View.VISIBLE);
                    HtmlTools.getInstance(context).setRichText(msg, message.getAnswer().getMsg(),
                            ResourceUtils.getIdByName(context, "color", "sobot_color_link"));
                } else {
                    message.getAnswer().setMsg(null);
                    msg.setVisibility(View.GONE);
                }
            } else if ("4".equals(message.getAnswer().getMsgType())) {// 富文本中有图片
                // 设置富文本的文字的消息
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    msg.setVisibility(View.VISIBLE);
                    HtmlTools.getInstance(context).setRichText(msg, message.getAnswer().getMsg(),
                            ResourceUtils.getIdByName(context, "color","sobot_color_link"));
                } else {
                    msg.setVisibility(View.GONE);
                }

                if (1 == message.getSugguestionsFontColor()){
                    if (message.getSdkMsg() != null && !TextUtils.isEmpty(message.getSdkMsg().getQuestion())){
                        sobot_msg_title.setVisibility(View.VISIBLE);
                        sobot_msg_title.setText(message.getSdkMsg().getQuestion());
                    } else {
                        sobot_msg_title.setVisibility(View.GONE);
                    }
                } else if (!TextUtils.isEmpty(message.getQuestion())){
                    sobot_msg_title.setVisibility(View.VISIBLE);
                    sobot_msg_title.setText(message.getQuestion());
                } else {
                    sobot_msg_title.setVisibility(View.GONE);
                }

                if (message.getAnswer().getRichpricurl() != null) {
                    bigPicImage.setVisibility(View.VISIBLE);
                    BitmapUtil.display(context, CommonUtils.encode(message.getAnswer()
                            .getRichpricurl()), bigPicImage);
                    // 点击大图 查看大图的内容
                    bigPicImage.setOnClickListener(new ImageClickLisenter(context,message
                            .getAnswer().getRichpricurl()));
                } else {
                    bigPicImage.setVisibility(View.GONE);
                }
            } else if ("5".equals(message.getAnswer().getMsgType())) {// 富文本中纯文字
                if (message.getAnswer()!=null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    msg.setVisibility(View.VISIBLE);
                    String RobotAnaser = message.getAnswer().getMsg().replaceAll("\n", "<br/>");
                    if (RobotAnaser.startsWith("<br/>")) {
                        RobotAnaser = RobotAnaser.substring(5, RobotAnaser.length());
                    }
                    if (RobotAnaser.endsWith("<br/>")) {
                        RobotAnaser = RobotAnaser.substring(0, RobotAnaser.length() - 5);
                    }
                    HtmlTools.getInstance(context).setRichText(msg,RobotAnaser, ResourceUtils.getIdByName(context, "color","sobot_color_link"));
                } else {
                    msg.setVisibility(View.GONE);
                    message.getAnswer().setMsg(null);
                }
            } else if ("6".equals(message.getAnswer().getMsgType())) {// 富文本中有视频
                // 暂时不解析
            } else if ("7".equals(message.getAnswer().getMsgType())) {
                if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    ZhiChiReplyAnswer msgAnswer = GsonUtil.jsonToZhiChiReplyAnswer(message.getAnswer().getMsg());
                    if (msgAnswer != null && !TextUtils.isEmpty(msgAnswer.getMsg())) {
                        msg.setVisibility(View.VISIBLE);
                        HtmlTools.getInstance(context).setRichText(msg,msgAnswer.getMsg(), ResourceUtils.getIdByName(context, "color","sobot_color_link"));
                    } else {
                        msg.setVisibility(View.GONE);
                        msg.setText(null);
                    }

                    if (msgAnswer != null && !TextUtils.isEmpty(msgAnswer.getRichpricurl())) {
                        bigPicImage.setVisibility(View.VISIBLE);
                        BitmapUtil.display(context, CommonUtils.encode(msgAnswer
                                .getRichpricurl()), bigPicImage);
                        // 点击大图 查看大图的内容
                        bigPicImage.setOnClickListener(new ImageClickLisenter(context, msgAnswer.getRichpricurl()));
                    } else {
                        bigPicImage.setVisibility(View.GONE);
                    }

                    if (msgAnswer != null && !TextUtils.isEmpty(msgAnswer.getRichmoreurl())) {
                        read_alltext_line.setVisibility(View.VISIBLE);
                        rendAllText.setVisibility(View.VISIBLE);
                        rendAllText.setOnClickListener(new ReadAllTextLisenter(context, msgAnswer.getRichmoreurl()));
                    } else {
                        read_alltext_line.setVisibility(View.GONE);
                        rendAllText.setVisibility(View.GONE);
                    }
                    if(msgAnswer != null && !TextUtils.isEmpty(msgAnswer.getMsgType())){
                        // 隐藏不该显示的内容
                        hideViewByType(this, msgAnswer.getMsgType());
                    } else {
                        hideViewByType(this, "0");
                    }
                }
                return;
            }

            if (message.getAnswer().getRichmoreurl() != null
                    && message.getAnswer().getRichmoreurl().length() > 0) {
                read_alltext_line.setVisibility(View.VISIBLE);
                rendAllText.setVisibility(View.VISIBLE);
                rendAllText.setOnClickListener(new ReadAllTextLisenter(context,message.getAnswer().getRichmoreurl()));
                msg.setMaxLines(3);
            } else {
                read_alltext_line.setVisibility(View.GONE);
                rendAllText.setVisibility(View.GONE);
                msg.setMaxLines(Integer.MAX_VALUE);
            }
            // 隐藏不改显示的内容
            hideViewByType(this,message.getAnswer().getMsgType());
        }
        if (!TextUtils.isEmpty(message.getRictype())) {
            if ("0".equals(message.getRictype())) {// 代表无图片的格式
                bigPicImage.setVisibility(View.GONE);
                rendAllText.setVisibility(View.GONE);
            } else if ("1".equals(message.getRictype())) {
                bigPicImage.setVisibility(View.VISIBLE);
                rendAllText.setVisibility(View.VISIBLE);
                BitmapUtil.display(context, CommonUtils.encode(message.getPicurl()), bigPicImage);
                rendAllText.setVisibility(View.VISIBLE);
                rendAllText.setOnClickListener(new ReadAllTextLisenter(context,message
                        .getAnswer().getRichmoreurl()));
            }
        }
        // 回复语的答复
        String stripeContent = null;

        if (message.getStripe() != null) {
            stripeContent = message.getStripe().trim();
        }
        if (stripeContent != null && stripeContent.length() > 0) {
            // 设置提醒的内容
            stripe.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(stripe,stripeContent, ResourceUtils.getIdByName(context, "color","sobot_color_link"));
        } else {
            stripe.setText(null);
            stripe.setVisibility(View.GONE);
        }
        answersList.setVisibility(View.GONE);
        if (message.getSugguestions() != null
                && message.getSugguestions().length > 0) {
            if (message.getListSuggestions() != null && message.getListSuggestions().size() > 0){
                ArrayList<Suggestions> listSuggestions = message.getListSuggestions();
                answersList.setVisibility(View.VISIBLE);
                answersList.removeAllViews();
                for (int i = 0; i < listSuggestions.size(); i++) {
                    TextView answer = new TextView(context);
                    answer.setTextSize(16);
                    answer.setLineSpacing(2f, 1f);
                    int currentItem = i + 1;
                    // 设置字体的颜色的样式
                    answer.setTextColor(context.getResources()
                            .getColor(ResourceUtils.getIdByName(context,
                                    "color","sobot_color_link")));
                    answer.setOnClickListener(new AnsWerClickLisenter(context,null,
                            currentItem + "、" + listSuggestions.get(i).getQuestion(), null ,listSuggestions.get(i).getDocId()));
                    String tempStr = currentItem + "、" + listSuggestions.get(i).getQuestion();
                    answer.setText(tempStr);
                    answersList.addView(answer);
                }
            } else {
                String[] answerStringList = message.getSugguestions();
                answersList.setVisibility(View.VISIBLE);
                answersList.removeAllViews();
                for (int i = 0; i < answerStringList.length; i++) {
                    TextView answer = new TextView(context);
                    answer.setTextSize(16);
                    answer.setLineSpacing(2f, 1f);
                    int currentItem = i + 1;
                    answer.setTextColor(context.getResources().getColor(
                            ResourceUtils.getIdByName(context, "color","sobot_color_suggestion_history")));
                    String tempStr = currentItem + "、" + answerStringList[i];
                    answer.setText(tempStr);
                    answersList.addView(answer);
                }
            }
        }

        checkShowTransferBtn();
        resetRevaluateBtn();

        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())){
                    ToastUtil.showCopyPopWindows(context,view, message.getAnswer().getMsg(), 30,0);
                }
                return false;
            }
        });
    }

    private void checkShowTransferBtn(){
        if(message.isShowTransferBtn()){
            showTransferBtn();
        }else {
            hideTransferBtn();
        }
    }

    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn(){
        sobot_tv_transferBtn.setVisibility(View.GONE);
        if(message != null){
            message.setShowTransferBtn(false);
        }
    }

    /**
     * 显示转人工按钮
     */
    public void showTransferBtn(){
        sobot_tv_transferBtn.setVisibility(View.VISIBLE);
        if(message != null){
            message.setShowTransferBtn(true);
        }
        sobot_tv_transferBtn.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if(mContext != null){
                    ((SobotChatActivity) mContext).doClickTransferBtn();
                }
            }
        });
    }

    public void resetRevaluateBtn(){
        //顶 踩的状态 0 不显示顶踩按钮  1显示顶踩 按钮  2 显示顶之后的view  3显示踩之后view
        switch (message.getRevaluateState()){
            case 1:
                showRevaluateBtn();
                break;
            case 2:
                showLikeWordView();
                break;
            case 3:
                showDislikeWordView();
                break;
            default:
                hideRevaluateBtn();
                break;
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn(){
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setText("");
        if(mContext != null){
            sobot_tv_dislikeBtn.setBackgroundResource(ResourceUtils.getIdByName(mContext, "drawable", "sobot_cai_selector"));
        }
        sobot_tv_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });
    }

    /**
     * 顶踩 操作
     * @param revaluateFlag true 顶  false 踩
     */
    private void doRevaluate(boolean revaluateFlag){
        if(mContext != null){
            ((SobotChatActivity) mContext).doRevaluate(revaluateFlag,message);
        }
    }

    /**
     * 隐藏 顶踩 按钮
     */
    public void hideRevaluateBtn(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        VersionUtils.setBackground(null,sobot_tv_dislikeBtn);
        sobot_tv_dislikeBtn.setText(getResStringId("sobot_robot_like"));
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView(){
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        VersionUtils.setBackground(null,sobot_tv_dislikeBtn);
        sobot_tv_dislikeBtn.setText(getResStringId("sobot_robot_dislike"));
    }

    private void hideViewByType(RichTextMessageHolder textHolder, String type) {

		/*
		 * Int 消息类型0文本 1图片 2音频 4 富文本中有图片 5 富文本中纯文字 6 富文本中有视频
		 */
        if ("0".equals(type)) { // 纯文本
            textHolder.bigPicImage.setVisibility(View.GONE);
            textHolder.simple_picture.setVisibility(View.GONE);

            // 阅读全文
            textHolder.rendAllText.setVisibility(View.GONE);
        } else if ("1".equals(type)) {// 图片
            textHolder.msg.setVisibility(View.GONE);
            textHolder.bigPicImage.setVisibility(View.GONE);
            textHolder.rendAllText.setVisibility(View.GONE);
        } else if ("2".equals(type)) {// 音频
            textHolder.msg.setVisibility(View.GONE);
            textHolder.bigPicImage.setVisibility(View.GONE);
            textHolder.simple_picture.setVisibility(View.GONE);
        } else if ("3".equals(type)) {
            textHolder.bigPicImage.setVisibility(View.GONE);
            textHolder.simple_picture.setVisibility(View.GONE);
            textHolder.rendAllText.setVisibility(View.GONE);
        } else if ("4".equals(type)) {// 富文本中有图片
            textHolder.simple_picture.setVisibility(View.GONE);
        } else if ("5".equals(type)) {// 富文本中纯文字
            textHolder.bigPicImage.setVisibility(View.GONE);
            textHolder.simple_picture.setVisibility(View.GONE);
        } else if ("6".equals(type)) {// 富文本中有视频

        }
    }

    public int getResStringId(String name) {
        if (mContext != null){
            return ResourceUtils.getIdByName(mContext, "string", name);
        } else {
            return 0;
        }
    }

    // 查看阅读全文的监听
    public static class ReadAllTextLisenter implements View.OnClickListener {
        private String mUrlContent;
        private Context context;

        public ReadAllTextLisenter(Context context,String urlContent) {
            super();
            this.mUrlContent = urlContent;
            this.context = context;
        }

        @Override
        public void onClick(View arg0) {

            if (!mUrlContent.startsWith("http://")
                    && !mUrlContent.startsWith("https://")) {
                mUrlContent = "http://" + mUrlContent;
            }
            // 内部浏览器
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", mUrlContent);
            context.startActivity(intent);
        }
    }

    // 问题的回答监听
    public static class AnsWerClickLisenter implements View.OnClickListener {

        private String msgContent;
        private String id;
        private ImageView img;
        private String docId;
        private Context context;

        public AnsWerClickLisenter(Context context,String id, String msgContent, ImageView image,
                                   String docId) {
            super();
            this.context = context;
            this.msgContent = msgContent;
            this.id = id;
            this.img = image;
            this.docId = docId;
        }

        @Override
        public void onClick(View arg0) {
            if (img != null) {
                img.setVisibility(View.GONE);
            }

            if (context != null){
                SobotChatActivity activity = (SobotChatActivity) context;
				/* 点击回答语。强制隐藏键盘 */
                activity.hidePanelAndKeyboard(activity.mPanelRoot);
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                activity.sendMessageToRobot(msgObj, 0, 1, docId);
            }
        }
    }


}
