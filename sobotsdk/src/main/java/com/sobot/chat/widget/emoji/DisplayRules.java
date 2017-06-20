/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sobot.chat.widget.emoji;

import android.content.Context;

import com.sobot.chat.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Emoji在手机上的显示规则
 *
 * @author kymjs (http://www.kymjs.com)
 */
public enum DisplayRules {
    // 注意：value不能从0开始，因为0会被库自动设置为删除按钮
    // int type, int value, int resId, String cls
    QQBIAOQING0(0, 1, "expression_1", "[微笑]", "[微笑]"),
    QQBIAOQING1(0, 1, "expression_2", "[撇嘴]", "[撇嘴]"),
    QQBIAOQING2(0, 1, "expression_3", "[色]", "[色]"),
    QQBIAOQING3(0, 1, "expression_4", "[发呆]", "[发呆]"),
    QQBIAOQING4(0, 1, "expression_5", "[得意]", "[得意]"),
    QQBIAOQING5(0, 1, "expression_6", "[流泪]", "[流泪]"),
    QQBIAOQING6(0, 1, "expression_7", "[害羞]", "[害羞]"),
    QQBIAOQING7(0, 1, "expression_8", "[闭嘴]", "[闭嘴]"),
    QQBIAOQING8(0, 1, "expression_9", "[睡]", "[睡]"),
    QQBIAOQING9(0, 1, "expression_10", "[大哭]", "[大哭]"),
    QQBIAOQING10(0, 1, "expression_11", "[尴尬]", "[尴尬]"),
    QQBIAOQING11(0, 1, "expression_12", "[发怒]", "[发怒]"),
    QQBIAOQING12(0, 1, "expression_13", "[调皮]", "[调皮]"),
    QQBIAOQING13(0, 1, "expression_14", "[呲牙]", "[呲牙]"),
    QQBIAOQING14(0, 1, "expression_15", "[惊讶]", "[惊讶]"),
    QQBIAOQING15(0, 1, "expression_16", "[难过]", "[难过]"),
    QQBIAOQING16(0, 1, "expression_17", "[酷]", "[酷]"),
    QQBIAOQING17(0, 1, "expression_18", "[冷汗]", "[冷汗]"),
    QQBIAOQING18(0, 1, "expression_19", "[抓狂]", "[抓狂]"),
    QQBIAOQING19(0, 1, "expression_20", "[吐]", "[吐]"),
    QQBIAOQING20(0, 1, "expression_21", "[偷笑]", "[偷笑]"),
    QQBIAOQING21(0, 1, "expression_22", "[愉快]", "[愉快]"),
    QQBIAOQING22(0, 1, "expression_23", "[白眼]", "[白眼]"),
    QQBIAOQING23(0, 1, "expression_24", "[傲慢]", "[傲慢]"),
    QQBIAOQING24(0, 1, "expression_25", "[饥饿]", "[饥饿]"),
    QQBIAOQING25(0, 1, "expression_26", "[困]", "[困]"),
    QQBIAOQING26(0, 1, "expression_27", "[惊恐]", "[惊恐]"),
    QQBIAOQING27(0, 1, "expression_28", "[流汗]", "[流汗]"),
    QQBIAOQING28(0, 1, "expression_29", "[憨笑]", "[憨笑]"),
    QQBIAOQING29(0, 1, "expression_30", "[悠闲]", "[悠闲]"),
    QQBIAOQING30(0, 1, "expression_31", "[奋斗]", "[奋斗]"),
    QQBIAOQING31(0, 1, "expression_32", "[咒骂]", "[咒骂]"),
    QQBIAOQING32(0, 1, "expression_33", "[疑问]", "[疑问]"),
    QQBIAOQING33(0, 1, "expression_34", "[嘘]", "[嘘]"),
    QQBIAOQING34(0, 1, "expression_35", "[晕]", "[晕]"),
    QQBIAOQING35(0, 1, "expression_36", "[疯了]", "[疯了]"),
    QQBIAOQING36(0, 1, "expression_37", "[衰]", "[衰]"),
    QQBIAOQING37(0, 1, "expression_38", "[骷髅]", "[骷髅]"),
    QQBIAOQING38(0, 1, "expression_39", "[敲打]", "[敲打]"),
    QQBIAOQING39(0, 1, "expression_40", "[再见]", "[再见]"),
    QQBIAOQING40(0, 1, "expression_41", "[擦汗]", "[擦汗]"),
    QQBIAOQING41(0, 1, "expression_42", "[抠鼻]", "[抠鼻]"),
    QQBIAOQING42(0, 1, "expression_43", "[鼓掌]", "[鼓掌]"),
    QQBIAOQING43(0, 1, "expression_44", "[糗大了]", "[糗大了]"),
    QQBIAOQING44(0, 1, "expression_45", "[坏笑]", "[坏笑]"),
    QQBIAOQING45(0, 1, "expression_46", "[左哼哼]", "[左哼哼]"),
    QQBIAOQING46(0, 1, "expression_47", "[右哼哼]", "[右哼哼]"),
    QQBIAOQING47(0, 1, "expression_48", "[哈欠]", "[哈欠]"),
    QQBIAOQING48(0, 1, "expression_49", "[鄙视]", "[鄙视]"),
    QQBIAOQING49(0, 1, "expression_50", "[委屈]", "[委屈]"),
    QQBIAOQING50(0, 1, "expression_51", "[快哭了]", "[快哭了]"),
    QQBIAOQING51(0, 1, "expression_52", "[阴险]", "[阴险]"),
    QQBIAOQING52(0, 1, "expression_53", "[亲亲]", "[亲亲]"),
    QQBIAOQING53(0, 1, "expression_54", "[吓]", "[吓]"),
    QQBIAOQING54(0, 1, "expression_55", "[可怜]", "[可怜]"),
    QQBIAOQING55(0, 1, "expression_56", "[菜刀]", "[菜刀]"),
    QQBIAOQING56(0, 1, "expression_57", "[西瓜]", "[西瓜]"),
    QQBIAOQING57(0, 1, "expression_58", "[啤酒]", "[啤酒]"),
    QQBIAOQING58(0, 1, "expression_59", "[篮球]", "[篮球]"),
    QQBIAOQING59(0, 1, "expression_60", "[乒乓]", "[乒乓]"),
    QQBIAOQING60(0, 1, "expression_61", "[咖啡]", "[咖啡]"),
    QQBIAOQING61(0, 1, "expression_62", "[饭]", "[饭]"),
    QQBIAOQING62(0, 1, "expression_63", "[猪头]", "[猪头]"),
    QQBIAOQING63(0, 1, "expression_64", "[玫瑰]", "[玫瑰]"),
    QQBIAOQING64(0, 1, "expression_65", "[调谢]", "[调谢]"),
    QQBIAOQING65(0, 1, "expression_66", "[嘴唇]", "[嘴唇]"),
    QQBIAOQING66(0, 1, "expression_67", "[爱心]", "[爱心]"),
    QQBIAOQING67(0, 1, "expression_68", "[心碎]", "[心碎]"),
    QQBIAOQING68(0, 1, "expression_69", "[蛋糕]", "[蛋糕]"),
    QQBIAOQING69(0, 1, "expression_70", "[闪电]", "[闪电]"),
    QQBIAOQING70(0, 1, "expression_71", "[炸弹]", "[炸弹]"),
    QQBIAOQING71(0, 1, "expression_72", "[刀]", "[刀]"),
    QQBIAOQING72(0, 1, "expression_73", "[足球]", "[足球]"),
    QQBIAOQING73(0, 1, "expression_74", "[瓢虫]", "[瓢虫]"),
    QQBIAOQING74(0, 1, "expression_75", "[便便]", "[便便]"),
    QQBIAOQING75(0, 1, "expression_76", "[月亮]", "[月亮]"),
    QQBIAOQING76(0, 1, "expression_77", "[太阳]", "[太阳]"),
    QQBIAOQING77(0, 1, "expression_78", "[礼物]", "[礼物]"),
    QQBIAOQING78(0, 1, "expression_79", "[拥抱]", "[拥抱]"),
    QQBIAOQING79(0, 1, "expression_80", "[强]", "[强]"),
    QQBIAOQING80(0, 1, "expression_81", "[弱]", "[弱]"),
    QQBIAOQING81(0, 1, "expression_82", "[握手]", "[握手]"),
    QQBIAOQING82(0, 1, "expression_83", "[胜利]", "[胜利]"),
    QQBIAOQING83(0, 1, "expression_84", "[抱拳]", "[抱拳]"),
    QQBIAOQING84(0, 1, "expression_85", "[勾引]", "[勾引]"),
    QQBIAOQING85(0, 1, "expression_86", "[拳头]", "[拳头]"),
    QQBIAOQING86(0, 1, "expression_87", "[差劲]", "[差劲]"),
    QQBIAOQING87(0, 1, "expression_88", "[爱你]", "[爱你]"),
    QQBIAOQING88(0, 1, "expression_89", "[NO]", "[NO]"),
    QQBIAOQING89(0, 1, "expression_90", "[OK]", "[OK]"),
    QQBIAOQING90(0, 1, "expression_91", "[爱情]", "[爱情]"),
    QQBIAOQING91(0, 1, "expression_92", "[飞吻]", "[飞吻]"),
    QQBIAOQING92(0, 1, "expression_93", "[跳跳]", "[跳跳]"),
    QQBIAOQING93(0, 1, "expression_94", "[发抖]", "[发抖]"),
    QQBIAOQING94(0, 1, "expression_95", "[怄火]", "[怄火]"),
    QQBIAOQING95(0, 1, "expression_96", "[转圈]", "[转圈]"),
    QQBIAOQING96(0, 1, "expression_97", "[磕头]", "[磕头]"),
    QQBIAOQING97(0, 1, "expression_98", "[回头]", "[回头]"),
    QQBIAOQING98(0, 1, "expression_99", "[跳绳]", "[跳绳]"),
    QQBIAOQING99(0, 1, "expression_100", "[投降]", "[投降]"),
    QQBIAOQING100(0, 1, "expression_101", "[激动]", "[激动]"),
    QQBIAOQING101(0, 1, "expression_102", "[乱舞]", "[乱舞]"),
    QQBIAOQING102(0, 1, "expression_103", "[献吻]", "[献吻]"),
    QQBIAOQING103(0, 1, "expression_104", "[左太极]", "[左太极]"),
    QQBIAOQING104(0, 1, "expression_105", "[右太极]", "[右太极]");

    /*********************************
     * 操作
     **************************************/
    private String emojiStr;
    private String remote;
    private int value;
    private String resName;
    private int type;
    private static Map<String, Integer> sEmojiMap;

    private DisplayRules(int type, int value, String resName, String cls,
                         String remote) {
        this.type = type;
        this.emojiStr = cls;
        this.value = value;
        this.resName = resName;
        this.remote = remote;
    }

    public String getRemote() {
        return remote;
    }

    public String getEmojiStr() {
        return emojiStr;
    }

    public int getValue() {
        return value;
    }

    public String getResName() {
        return resName;
    }

    public int getType() {
        return type;
    }

    /**
     * 提高效率，忽略线程安全
     */
    public static Map<String, Integer> getMapAll(Context context) {
        context = context.getApplicationContext();
        if (sEmojiMap == null) {
            sEmojiMap = new HashMap<String, Integer>();
            for (DisplayRules data : values()) {
                int drawableId = ResourceUtils.getIdByName(context, "drawable",
                        data.getResName());
                if(drawableId != 0){
                    sEmojiMap.put(data.getEmojiStr(), ResourceUtils.getIdByName(context, "drawable",
                            data.getResName()));
                }
            }
        }
        return sEmojiMap;
    }

    public static ArrayList<Emojicon> getListAll(Context context) {
        context = context.getApplicationContext();
        ArrayList<Emojicon> sEmojiList = new ArrayList<>();
        for (DisplayRules data : values()) {
            int drawableId = ResourceUtils.getIdByName(context, "drawable",
                    data.getResName());
            if(drawableId != 0){
                sEmojiList.add(new Emojicon(data.getResName(),data.getValue(),data.getEmojiStr(),data
                        .getRemote(),ResourceUtils.getIdByName(context, "drawable", data.getResName())));
            }
        }
        return sEmojiList;
    }
}