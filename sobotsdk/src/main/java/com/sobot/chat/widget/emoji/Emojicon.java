package com.sobot.chat.widget.emoji;

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

/**
 * @author kymjs (http://www.kymjs.com)
 */
public class Emojicon {
    private final String resName; // 图片资源名称
    private final int value; // 一个emoji对应唯一一个value
    private final String emojiStr; // emoji在互联网传递的字符串
    private final String remote;
    private final int resId;

    public Emojicon(String resName, int value, String name, String remote, int resId) {
        this.resName = resName;
        this.value = value;
        this.emojiStr = name;
        this.remote = remote;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public String getResName() {
        return resName;
    }

    public String getRemote() {
        return remote;
    }

    public int getValue() {
        return value;
    }

    public String getEmojiStr() {
        return emojiStr;
    }
}