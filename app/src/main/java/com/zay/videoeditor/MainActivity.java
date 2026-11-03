package com.zay.videoeditor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends Activity {
    private VideoView videoView;
    private SeekBar timeline;
    private TextView timeLabel, emptyPreview;
    private Button playButton;
    private boolean prepared = false, dragging = false;
    private int duration = 0;
    private final Handler handler = new Handler();
    private final int bg = Color.rgb(12,14,22), panel = Color.rgb(27,31,45);
    private final int accent = Color.rgb(117,94,255);

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (prepared && videoView != null && !dragging && duration > 0) {
                int pos = videoView.getCurrentPosition();
                timeline.setProgress((int)(pos * 1000L / duration));
                updateTime(pos);
            }
            handler.postDelayed(this, 250);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        showEditor();
        handler.post(ticker);
    }

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView label(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        return t;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        return b;
    }

    private void showEditor() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(15),dp(8),dp(15),dp(12));
        root.setBackgroundColor(bg);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = label("ZAY",22,Color.WHITE);
        brand.setTypeface(null,1);
        top.addView(brand,new LinearLayout.LayoutParams(0,dp(48),1));
        Button add = button("+ Tambah video");
        add.setTextSize(12);
        add.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
        top.addView(add);
        root.addView(top);

        TextView sub = label("VIDEO EDITOR  •  PROJECT BARU",10,Color.LTGRAY);
        sub.setPadding(0,0,0,dp(10)); root.addView(sub);

        FrameLayout preview = new FrameLayout(this);
        preview.setBackgroundColor(Color.BLACK);
        videoView = new VideoView(this);
        preview.addView(videoView,new FrameLayout.LayoutParams(-1,-1));
        emptyPreview = label("Pilih video untuk mulai",15,Color.WHITE);
        emptyPreview.setGravity(Gravity.CENTER);
        preview.addView(emptyPreview,new FrameLayout.LayoutParams(-1,-1));
        root.addView(preview,new LinearLayout.LayoutParams(-1,dp(225)));

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0,dp(6),0,dp(6));
        playButton = button("▶"); playButton.setEnabled(false);
        controls.addView(playButton,new LinearLayout.LayoutParams(dp(58),dp(46)));
        timeLabel = label("00:00 / 00:00",12,Color.WHITE);
        timeLabel.setGravity(Gravity.CENTER_VERTICAL);
        timeLabel.setPadding(dp(12),0,0,0); controls.addView(timeLabel);
        root.addView(controls);

        TextView th = label("TIMELINE",12,Color.WHITE);
        th.setTypeface(null,1); th.setPadding(0,dp(12),0,dp(6)); root.addView(th);
        LinearLayout tp = new LinearLayout(this);
        tp.setOrientation(LinearLayout.VERTICAL);
        tp.setPadding(dp(12),dp(8),dp(12),dp(8)); tp.setBackgroundColor(panel);
        timeline = new SeekBar(this); timeline.setMax(1000); timeline.setEnabled(false);
        tp.addView(timeline);
        TextView track = label("▰  VIDEO 1",11,Color.rgb(195,185,255));
        track.setPadding(0,dp(3),0,0); tp.addView(track);
        TextView audio = label("+  TAMBAH AUDIO",10,Color.LTGRAY);
        audio.setPadding(0,dp(8),0,0); tp.addView(audio);
        root.addView(tp);

        TextView toolsTitle = label("ALAT EDIT",12,Color.WHITE);
        toolsTitle.setTypeface(null,1); toolsTitle.setPadding(0,dp(16),0,dp(8));
        root.addView(toolsTitle);
        LinearLayout tools = new LinearLayout(this);
        tools.setGravity(Gravity.CENTER);
        String[] names = {"Potong","Musik","Teks","Efek"};
        for (String name : names) {
            TextView item = label(name,12,Color.WHITE);
            item.setGravity(Gravity.CENTER); item.setBackgroundColor(panel);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,dp(48),1);
            p.setMargins(dp(3),0,dp(3),0); tools.addView(item,p);
        }
        root.addView(tools);
        TextView note = label("Pilih video • Putar • Geser timeline untuk mencari adegan",10,Color.GRAY);
        note.setGravity(Gravity.CENTER); note.setPadding(0,dp(14),0,0); root.addView(note);

        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("video/*");
                    startActivityForResult(i,100);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"Tidak dapat membuka galeri video.",Toast.LENGTH_LONG).show();
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!prepared) return;
                if (videoView.isPlaying()) { videoView.pause(); playButton.setText("▶"); }
                else { videoView.start(); playButton.setText("Ⅱ"); }
            }
        });
        timeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s,int p,boolean user) {
                if (user && prepared && duration > 0) {
                    int pos = (int)(duration * (p / 1000.0));
                    videoView.seekTo(pos); updateTime(pos);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar s) { dragging=true; }
            @Override public void onStopTrackingTouch(SeekBar s) { dragging=false; }
        });
        setContentView(root);
    }

    private void updateTime(int pos) {
        timeLabel.setText(format(pos)+" / "+format(duration));
    }

    private String format(int ms) {
        int s = Math.max(0,ms/1000);
        return String.format(Locale.US,"%02d:%02d",s/60,s%60);
    }

    @Override protected void onActivityResult(int request,int result,Intent data) {
        super.onActivityResult(request,result,data);
        if (request!=100 || result!=RESULT_OK || data==null || data.getData()==null) return;
        Uri uri=data.getData();
        try {
            getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {}
        prepared=false; emptyPreview.setVisibility(View.GONE);
        playButton.setEnabled(false); timeline.setEnabled(false);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(android.media.MediaPlayer mp) {
                prepared=true; duration=videoView.getDuration();
                timeline.setEnabled(true); playButton.setEnabled(true);
                timeline.setProgress(0); updateTime(0);
                videoView.start(); playButton.setText("Ⅱ");
            }
        });
        videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(android.media.MediaPlayer mp) {
                playButton.setText("▶"); videoView.seekTo(0);
                timeline.setProgress(0); updateTime(0);
            }
        });
        videoView.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
            @Override public boolean onError(android.media.MediaPlayer mp,int what,int extra) {
                Toast.makeText(MainActivity.this,"Video tidak dapat diputar. Coba video lain.",Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        if (videoView!=null && videoView.isPlaying()) {
            videoView.pause();
            if (playButton!=null) playButton.setText("▶");
        }
    }

    @Override protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
