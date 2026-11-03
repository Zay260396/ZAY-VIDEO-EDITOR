package com.zay.videoeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private final ArrayList<Uri> uris = new ArrayList<Uri>();
    private final ArrayList<Integer> durs = new ArrayList<Integer>();
    private int selected = -1;
    private boolean prepared = false, dragging = false;
    private VideoView videoView;
    private SeekBar seek;
    private TextView emptyPreview, timeLabel, selectedLabel, playBtn;
    private LinearLayout clipRow;
    private final Handler handler = new Handler();
    private final int bg = Color.rgb(12, 14, 30), panel = Color.rgb(24, 26, 48);
    private final int c1 = Color.rgb(147, 51, 234), c2 = Color.rgb(236, 72, 153);

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (prepared && !dragging && selected >= 0) {
                int d = durs.get(selected);
                int pos = videoView.getCurrentPosition();
                if (d > 0) seek.setProgress((int) (pos * 1000L / d));
                updateTime(pos);
            }
            handler.postDelayed(this, 250);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildUi();
        handler.post(ticker);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable box(int color, int r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(r));
        return g;
    }

    private GradientDrawable grad(int r) {
        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, new int[]{c1, c2});
        g.setCornerRadius(dp(r));
        return g;
    }

    private TextView text(String s, int sp, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        return t;
    }

    private void soon(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View x) {
                Toast.makeText(MainActivity.this, "Fitur ini aktif di Tahap 3",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);
        root.setPadding(dp(14), dp(8), dp(14), dp(12));
        root.setFitsSystemWindows(true);

        // Bar atas
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = text("←", 20, Color.WHITE);
        back.setGravity(Gravity.CENTER);
        back.setBackground(box(panel, 22));
        back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        top.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));
        TextView title = text("ZAY EDITOR", 16, Color.WHITE);
        title.setTypeface(null, 1);
        title.setPadding(dp(12), 0, 0, 0);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView res = text("720P ▾", 13, Color.WHITE);
        res.setPadding(dp(12), dp(8), dp(12), dp(8));
        res.setBackground(box(panel, 12));
        top.addView(res);
        TextView export = text("Export", 14, Color.WHITE);
        export.setTypeface(null, 1);
        export.setGravity(Gravity.CENTER);
        export.setPadding(dp(18), dp(10), dp(18), dp(10));
        export.setBackground(grad(20));
        soon(export);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-2, -2);
        ep.setMargins(dp(8), 0, 0, 0);
        top.addView(export, ep);
        root.addView(top);

        // Preview
        FrameLayout preview = new FrameLayout(this);
        preview.setBackground(box(Color.rgb(35, 20, 80), 20));
        preview.setClipToOutline(true);
        videoView = new VideoView(this);
        preview.addView(videoView, new FrameLayout.LayoutParams(-1, -1));
        emptyPreview = text("Ketuk + Media untuk memilih video", 14, Color.WHITE);
        emptyPreview.setGravity(Gravity.CENTER);
        preview.addView(emptyPreview, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(220));
        pp.setMargins(0, dp(12), 0, 0);
        root.addView(preview, pp);

        // Kontrol putar
        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(10), 0, 0);
        timeLabel = text("00:00 / 00:00", 13, Color.WHITE);
        controls.addView(timeLabel, new LinearLayout.LayoutParams(0, -2, 1));
        TextView prev = text("⏮", 16, Color.WHITE);
        prev.setGravity(Gravity.CENTER);
        prev.setBackground(box(panel, 22));
        prev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { selectClip(selected - 1); }
        });
        controls.addView(prev, new LinearLayout.LayoutParams(dp(44), dp(44)));
        playBtn = text("▶", 20, Color.WHITE);
        playBtn.setGravity(Gravity.CENTER);
        playBtn.setBackground(grad(26));
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!prepared) return;
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playBtn.setText("▶");
                } else {
                    videoView.start();
                    playBtn.setText("II");
                }
            }
        });
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(dp(52), dp(52));
        plp.setMargins(dp(8), 0, dp(8), 0);
        controls.addView(playBtn, plp);
        TextView next = text("⏭", 16, Color.WHITE);
        next.setGravity(Gravity.CENTER);
        next.setBackground(box(panel, 22));
        next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { selectClip(selected + 1); }
        });
        controls.addView(next, new LinearLayout.LayoutParams(dp(44), dp(44)));
        root.addView(controls);

        seek = new SeekBar(this);
        seek.setMax(1000);
        seek.setEnabled(false);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean user) {
                if (user && prepared && selected >= 0) {
                    int pos = (int) (durs.get(selected) * (p / 1000.0));
                    videoView.seekTo(pos);
                    updateTime(pos);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar s) { dragging = true; }
            @Override public void onStopTrackingTouch(SeekBar s) { dragging = false; }
        });
        root.addView(seek);

        // Baris "Terpilih" + tombol Media
        LinearLayout selRow = new LinearLayout(this);
        selRow.setGravity(Gravity.CENTER_VERTICAL);
        selRow.setPadding(0, dp(6), 0, dp(8));
        selectedLabel = text("Belum ada klip", 13, Color.LTGRAY);
        selRow.addView(selectedLabel, new LinearLayout.LayoutParams(0, -2, 1));
        TextView add = text("+  Media", 15, Color.WHITE);
        add.setTypeface(null, 1);
        add.setGravity(Gravity.CENTER);
        add.setPadding(dp(20), dp(11), dp(20), dp(11));
        add.setBackground(grad(22));
        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("video/*");
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(i, 100);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,
                            "Tidak dapat membuka galeri video.", Toast.LENGTH_LONG).show();
                }
            }
        });
        selRow.addView(add);
        root.addView(selRow);

        // Timeline
        LinearLayout tl = new LinearLayout(this);
        tl.setOrientation(LinearLayout.VERTICAL);
        tl.setBackground(box(panel, 18));
        tl.setPadding(dp(10), dp(10), dp(10), dp(10));
        tl.setMinimumHeight(dp(110));
        HorizontalScrollView hs = new HorizontalScrollView(this);
        clipRow = new LinearLayout(this);
        clipRow.setOrientation(LinearLayout.HORIZONTAL);
        hs.addView(clipRow);
        tl.addView(hs, new LinearLayout.LayoutParams(-1, -2));
        root.addView(tl, new LinearLayout.LayoutParams(-1, -2));

        // Pendorong supaya toolbar menempel di bawah
        root.addView(new View(this), new LinearLayout.LayoutParams(-1, 0, 1));

        // Toolbar bawah
        LinearLayout tools = new LinearLayout(this);
        String[] names = {"Potong", "Musik", "Teks", "Efek", "Transisi", "Stiker"};
        String[] icons = {"✂", "♪", "T", "✦", "⇄", "☺"};
        for (int i = 0; i < names.length; i++) {
            TextView item = text(icons[i] + "\n" + names[i], 11, Color.WHITE);
            item.setGravity(Gravity.CENTER);
            item.setBackground(box(panel, 14));
            soon(item);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(64), 1);
            p.setMargins(dp(3), 0, dp(3), 0);
            tools.addView(item, p);
        }
        root.addView(tools);

        // Listener video
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                prepared = true;
                seek.setEnabled(true);
                seek.setProgress(0);
                updateTime(0);
                videoView.start();
                playBtn.setText("II");
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) {
                playBtn.setText("▶");
                videoView.seekTo(0);
                seek.setProgress(0);
                updateTime(0);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MainActivity.this,
                        "Video tidak dapat diputar. Coba video lain.",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        refreshClips();
        setContentView(root);
    }

    private void selectClip(int i) {
        if (i < 0 || i >= uris.size()) return;
        selected = i;
        prepared = false;
        emptyPreview.setVisibility(View.GONE);
        seek.setEnabled(false);
        playBtn.setText("▶");
        videoView.setVideoURI(uris.get(i));
        selectedLabel.setText("Terpilih: Klip " + (i + 1) + " • "
                + String.format(Locale.US, "%.1f", durs.get(i) / 1000.0) + " dtk");
        refreshClips();
    }

    private void refreshClips() {
        clipRow.removeAllViews();
        if (uris.isEmpty()) {
            clipRow.addView(text("Belum ada klip. Ketuk + Media.", 12, Color.GRAY));
            return;
        }
        for (int i = 0; i < uris.size(); i++) {
            final int idx = i;
            int w = Math.max(dp(90), (int) (durs.get(i) / 1000.0 * dp(10)));
            TextView b = text("Klip " + (i + 1), 12, Color.WHITE);
            b.setGravity(Gravity.CENTER_VERTICAL);
            b.setPadding(dp(12), 0, dp(12), 0);
            b.setBackground(i == selected ? grad(12) : box(Color.rgb(59, 91, 219), 12));
            b.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { selectClip(idx); }
            });
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, dp(44));
            p.setMargins(0, 0, dp(4), 0);
            clipRow.addView(b, p);
        }
    }

    private int durationOf(Uri uri) {
        MediaMetadataRetriever r = new MediaMetadataRetriever();
        try {
            r.setDataSource(this, uri);
            String d = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return d == null ? 0 : Integer.parseInt(d);
        } catch (Exception e) {
            return 0;
        } finally {
            try { r.release(); } catch (Exception ignored) {}
        }
    }

    private void updateTime(int pos) {
        if (selected < 0) return;
        timeLabel.setText(format(pos) + " / " + format(durs.get(selected)));
    }

    private String format(int ms) {
        int s = Math.max(0, ms / 1000);
        return String.format(Locale.US, "%02d:%02d", s / 60, s % 60);
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != 100 || res != RESULT_OK || data == null) return;
        ArrayList<Uri> picked = new ArrayList<Uri>();
        if (data.getClipData() != null) {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                picked.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            picked.add(data.getData());
        }
        if (picked.isEmpty()) return;
        for (Uri u : picked) {
            try {
                getContentResolver().takePersistableUriPermission(
                        u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}
            uris.add(u);
            durs.add(durationOf(u));
        }
        if (selected < 0) selectClip(0);
        else refreshClips