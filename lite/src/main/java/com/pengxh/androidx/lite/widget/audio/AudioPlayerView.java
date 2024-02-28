package com.pengxh.androidx.lite.widget.audio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayerView extends AppCompatTextView implements Handler.Callback, View.OnClickListener {

    private static final String TAG = "AudioPlayerView";
    private final WeakReferenceHandler weakReferenceHandler = new WeakReferenceHandler(this);
    private final AsyncPlayer asyncPlayer = new AsyncPlayer(TAG);
    private final AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build();
    private final Context context;
    private int index;
    private boolean isPlaying = false;
    private long duration = 0L;
    private File file;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    public AudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnClickListener(this);
    }

    public void setAudioSource(File file) throws IOException {
        this.file = file;
        //获取音频时长
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream inputStream = new FileInputStream(file);
        FileDescriptor fileDescriptor = inputStream.getFD();
        mmr.setDataSource(fileDescriptor);
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (time == null) {
            duration = 0L;
        } else {
            duration = Long.parseLong(time);
        }

        //格式化时长
        long sec = duration / 1000;
        int m = Integer.parseInt(IntHub.appendZero((int) (sec / 60)));
        int s = Integer.parseInt(IntHub.appendZero((int) (sec % 60)));
        setText(m + ":" + s);
        mmr.release();
    }

    @Override
    public void onClick(View v) {
        if (isPlaying) {
            asyncPlayer.stop();
            stopAnimation();
            isPlaying = false;
        } else {
            asyncPlayer.play(context, Uri.fromFile(file), false, audioAttributes);
            startAnimation();
            isPlaying = true;
            /**
             * 倒计时，判断音频是否播放完毕
             *
             * AsyncPlayer是MediaPlayer的简单异步封装，简单到只提供播放和停止API，所以需要自己监听播放是否完毕
             * */
            new CountDownTimer(duration, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    stop();
                }
            }.start();
        }
    }

    private void startAnimation() {
        //防止被多次点击，每次点击View之后都把之前的动画Runnable清空
        weakReferenceHandler.removeCallbacks(animationRunnable);
        weakReferenceHandler.postDelayed(animationRunnable, 200);
    }

    private void stopAnimation() {
        setDrawable(R.drawable.ic_audio_icon3);
        weakReferenceHandler.removeCallbacks(animationRunnable);
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            weakReferenceHandler.postDelayed(this, 200);
            setDrawable(Constant.AUDIO_DRAWABLES.get(index % 3));
            index++;
        }
    };

    private void setDrawable(@DrawableRes int id) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawables(drawable, null, null, null);
    }

    public void stop() {
        weakReferenceHandler.removeCallbacks(animationRunnable);
        isPlaying = false;
    }
}
