package com.sobot.chat.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.File;
import java.io.IOException;

public class AudioPlayPresenter {

    private Context mContent;
    private ZhiChiMessageBase mCurrentMsg;
    private AudioPlayCallBack mCallbak;

    public AudioPlayPresenter(Context content) {
        this.mContent = content;
    }

    public synchronized void clickAudio(final ZhiChiMessageBase message, AudioPlayCallBack callbak) {
        if (AudioTools.getInstance().isPlaying()) {
            AudioTools.stop();// 停止语音的播放
        }
        this.mCallbak = callbak;
        if (mCurrentMsg != message) {
            if (mCurrentMsg != null) {
                mCurrentMsg.setVoideIsPlaying(false);
                if (mCallbak != null) {
                    mCallbak.onPlayEnd(mCurrentMsg);
                    mCurrentMsg = null;
                }
            }
            playVoiceByPath(message);
        } else {
            // 点击同一个的元素
            AudioTools.stop();// 停止语音的播放
            message.setVoideIsPlaying(false);
            if (mCallbak != null) {
                mCallbak.onPlayEnd(message);
                mCurrentMsg = null;
            }
        }
    }

    private void playVoiceByPath(final ZhiChiMessageBase message) {
        final String path = message.getAnswer().getMsg();
        String contentPath;
        if (!TextUtils.isEmpty(path)) {
            if (message.getSugguestionsFontColor() == 1) {
                //是历史记录  就创建文件夹进行下载
                contentPath = path.substring(path.indexOf("msg") + 4, path.length());
                File directory = new File(ZhiChiConstant.voicePositionPath + contentPath).getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    try {
                        boolean success = directory.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                contentPath = ZhiChiConstant.voicePositionPath + contentPath;
            } else {
                contentPath = path;
            }
            LogUtils.i("contentPath：" + contentPath);
            final File file = new File(contentPath);
            if (!file.exists()) {
                // 下载
                HttpUtils.getInstance().download(path, file, null, new HttpUtils.FileCallBack() {

                    @Override
                    public void onResponse(File response) {
                        playVoice(message, response);
                    }

                    @Override
                    public void onError(Exception e, String msg, int responseCode) {
                    }

                    @Override
                    public void inProgress(int progress) {
                    }
                });

            } else {
                //直接拿地址播放
                playVoice(message, file);
            }
        }
    }

    private void playVoice(final ZhiChiMessageBase message, File voidePath) {
        try {
            AudioTools.getInstance();
            if (AudioTools.getIsPlaying()) {
                AudioTools.stop();
            }
            AudioTools.getInstance().setAudioStreamType(
                    AudioManager.STREAM_MUSIC);

            AudioTools.getInstance().reset();
            // 设置要播放的文件的路径
            AudioTools.getInstance().setDataSource(voidePath.toString());
            // 准备播放
            // AudioTools.getInstance().prepare();
            AudioTools.getInstance().prepareAsync();
            // 开始播放
            // mMediaPlayer.start();
            AudioTools.getInstance().setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            message.setVoideIsPlaying(true);
                            if (mCallbak != null) {
                                mCurrentMsg = message;
                                mCallbak.onPlayStart(message);
                            }
                        }
                    });
            // 这在播放的动画
            AudioTools.getInstance().setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            // 停止播放
                            message.setVoideIsPlaying(false);
                            AudioTools.getInstance().stop();
                            LogUtils.i("----语音播放完毕----");
                            if (mCallbak != null) {
                                mCallbak.onPlayEnd(message);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("音频播放失败");
            message.setVoideIsPlaying(false);
            AudioTools.getInstance().stop();
            if (mCallbak != null) {
                mCallbak.onPlayEnd(message);
            }
        }
    }
}