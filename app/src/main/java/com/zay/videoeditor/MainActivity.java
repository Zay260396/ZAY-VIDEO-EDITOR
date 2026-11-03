
package com.zay.videoeditor;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(10, 12, 20));
        getWindow().setNavigationBarColor(Color.rgb(10, 12, 20));

        showHome();
    }

    private void showHome() {
        try {
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setGravity(Gravity.CENTER_HORIZONTAL);
            root.setPadding(dp(24), dp(36), dp(24), dp(24));
            root.setBackgroundColor(Color.rgb(15, 18, 30));

            TextView title = new TextView(this);
            title.setText("ZAY VIDEO EDITOR");
            title.setTextColor(Color.WHITE);
            title.setTextSize(27);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, 1);
            root.addView(title, new LinearLayout.LayoutParams(-1, dp(58)));

            TextView subtitle = new TextView(this);
            subtitle.setText("Editor video gratis • Tanpa watermark");
            subtitle.setTextColor(Color.LTGRAY);
            subtitle.setTextSize(14);
            subtitle.setGravity(Gravity.CENTER);
            root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(42)));

            Button importButton = new Button(this);
            importButton.setText("PILIH VIDEO");
            root.addView(importButton, new LinearLayout.LayoutParams(-1, dp(56)));

            status = new TextView(this);
            status.setText("Pilih video dari perangkat untuk memulai.");
            status.setTextColor(Color.WHITE);
            status.setTextSize(14);
            status.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(-1, dp(72));
            statusParams.topMargin = dp(16);
            root.addView(status, statusParams);

            TextView note = new TextView(this);
            note.setText("Versi awal. Fitur pemotongan, musik, efek, dan ekspor akan dikembangkan.");
            note.setTextColor(Color.GRAY);
            note.setTextSize(12);
            note.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams noteParams =
                new LinearLayout.LayoutParams(-1, dp(64));
            noteParams.topMargin = dp(24);
            root.addView(note, noteParams);

            importButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("video/*");
                        startActivityForResult(intent, 100);
                    } catch (Exception e) {
                        Toast.makeText(
                            MainActivity.this,
                            "Tidak dapat membuka pemilih video.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                }
            });

            setContentView(root);

        } catch (Exception e) {
            TextView fallback = new TextView(this);
            fallback.setText("ZAY VIDEO EDITOR\nSilakan tutup lalu buka kembali aplikasi.");
            fallback.setTextColor(Color.WHITE);
            fallback.setTextSize(18);
            fallback.setGravity(Gravity.CENTER);
            fallback.setPadding(dp(24), dp(24), dp(24), dp(24));
            fallback.setBackgroundColor(Color.rgb(15, 18, 30));
            setContentView(fallback);
        }
    }

    private int dp(int value) {
        return (int) (
            value * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null &&
            data.getData() != null) {

            Uri videoUri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                    videoUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
            }

            if (status != null) {
                status.setText(
                    "Video berhasil dipilih. Siap untuk tahap pengeditan."
                );
            }
        }
    }
}
