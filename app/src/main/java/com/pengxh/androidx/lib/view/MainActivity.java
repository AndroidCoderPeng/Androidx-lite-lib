package com.pengxh.androidx.lib.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pengxh.androidx.lib.databinding.ActivityMainBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.kit.ContextKit;
import com.pengxh.androidx.lite.kit.LongKit;
import com.pengxh.androidx.lite.kit.StringKit;
import com.pengxh.androidx.lite.utils.socket.web.OnWebSocketListener;
import com.pengxh.androidx.lite.utils.socket.web.WebSocketClient;
import com.pengxh.androidx.lite.widget.TitleBarView;
import com.pengxh.androidx.lite.widget.audio.AudioPopupWindow;
import com.pengxh.androidx.lite.widget.audio.AudioRecodeHelper;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> implements OnWebSocketListener {

    private static final String TAG = "MainActivity";
    private final Context context = this;
    private final WebSocketClient webSocketClient = new WebSocketClient(this);

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initOnCreate(@Nullable Bundle savedInstanceState) {
        binding.websocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocketClient.isRunning()) {
                    webSocketClient.stop();
                } else {
                    webSocketClient.start("ws://192.168.161.200:8080/websocket/" + System.currentTimeMillis());
                }
            }
        });
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        runOnUiThread(() -> binding.websocketButton.setText("断开"));
    }

    @Override
    public void onMessageResponse(WebSocket webSocket, String message) {

    }

    @Override
    public void onMessageResponse(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onServerDisconnected(WebSocket webSocket, int code, String reason) {
        runOnUiThread(() -> binding.websocketButton.setText("连接"));
    }

    @Override
    public void onClientDisconnected(WebSocket webSocket, int code, String reason) {
        runOnUiThread(() -> binding.websocketButton.setText("连接"));
    }

    @Override
    public void onFailure(WebSocket webSocket) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        binding.titleView.setOnClickListener(new TitleBarView.OnClickListener() {
            @Override
            public void onLeftClick() {
                StringKit.show(context, "onLeftClick");
            }

            @Override
            public void onRightClick() {
                StringKit.show(context, "onRightClick");
            }
        });

        AudioRecodeHelper audioRecorder = new AudioRecodeHelper();
        new AudioPopupWindow.Builder().setContext(this).setOnAudioPopupCallback(new AudioPopupWindow.OnAudioPopupCallback() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onViewCreated(PopupWindow window, ImageView imageView, TextView textView) {
                binding.recodeAudioButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                binding.recodeAudioButton.animate().scaleX(0.75f).scaleY(0.75f).setDuration(100).start();
                                window.showAtLocation(binding.rootView, Gravity.CENTER, 0, 0);

                                //开始录音
                                audioRecorder.initRecorder(context, ContextKit.createAudioFile(context));
                                audioRecorder.startRecord(new AudioRecodeHelper.OnAudioStateUpdateListener() {
                                    @Override
                                    public void onUpdate(Double db, Long time) {
                                        imageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                                        textView.setText(LongKit.millsToTime(time));
                                    }

                                    @Override
                                    public void onStop(File file) {
                                        try {
                                            binding.audioPathView.setText("录音文件路径：" + file.getAbsoluteFile());
                                            binding.audioPlayerView.setAudioSource(file);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                break;

                            case MotionEvent.ACTION_UP:
                                audioRecorder.stopRecord(); //结束录音（保存录音文件）
                                window.dismiss();
                                binding.recodeAudioButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                                break;
                        }
                        return true;
                    }
                });
            }
        }).build().create();
    }

    @Override
    protected void observeRequestState() {

    }
}