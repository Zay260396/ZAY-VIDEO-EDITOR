package com.zay.videoeditor;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;

public class MainActivity extends Activity {

TextView status;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setGravity(Gravity.CENTER);
    layout.setPadding(30, 30, 30, 30);
    layout.setBackgroundColor(Color.rgb(15, 25, 45));

    TextView title = new TextView(this);
    title.setText("ZAY VIDEO EDITOR");
    title.setTextSize(28);
    title.setTextColor(Color.WHITE);
    title.setGravity(Gravity.CENTER);
    layout.addView(title);

    TextView subtitle = new TextView(this);
    subtitle.setText("Create Your Own Video");
    subtitle.setTextColor(Color.LTGRAY);
    subtitle.setTextSize(16);
    layout.addView(subtitle);

    Button importButton = new Button(this);
    importButton.setText("IMPORT VIDEO");
    layout.addView(importButton);

    status = new TextView(this);
    status.setText("Belum ada video dipilih");
    status.setTextColor(Color.WHITE);
    status.setTextSize(16);
    layout.addView(status);

    importButton.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    });

    setContentView(layout);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
        Uri video = data.getData();
        status.setText("Video berhasil dipilih!");
    }
}

}
