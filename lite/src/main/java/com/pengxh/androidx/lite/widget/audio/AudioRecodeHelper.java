package com.pengxh.androidx.lite.widget.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import java.io.File;
import java.io.IOException;

public class AudioRecodeHelper implements Handler.Callback {

    private final WeakReferenceHandler stateUpdateHandler = new WeakReferenceHandler(this);
    private final Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private Long startTime;
    private OnAudioStateUpdateListener stateUpdateListener;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    /**
     * 设置保存文件路径，mediaRecorder初始化
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initRecorder(Context context, File audioFile) {
        this.audioFile = audioFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaRecorder = new MediaRecorder(context);
        } else {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置麦克风
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mediaRecorder.setOutputFile(audioFile.getAbsoluteFile());
        mediaRecorder.setMaxDuration(Constant.MAX_LENGTH);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始录音 使用amr格式
     *
     * @return
     */
    public void startRecord(OnAudioStateUpdateListener stateUpdateListener) {
        this.stateUpdateListener = stateUpdateListener;
        mediaRecorder.start();
        startTime = System.currentTimeMillis();
        updateMicStatus();
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        //调用时音频采样的最大绝对振幅
        int amplitude = mediaRecorder.getMaxAmplitude();
        double db;
        if (amplitude > 1) {
            db = 20 * Math.log10(amplitude / 0.1);
        } else {
            db = 0.0;
        }
        stateUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
        stateUpdateHandler.postDelayed(updateStatusRunnable, 100);
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        stateUpdateListener.onStop(audioFile);
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        audioFile = null;
        stateUpdateHandler.removeCallbacks(updateStatusRunnable);
    }

    public interface OnAudioStateUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(Double db, Long time);

        /**
         * 停止录音
         *
         * @param file 保存文件
         */
        void onStop(File file);
    }
}
