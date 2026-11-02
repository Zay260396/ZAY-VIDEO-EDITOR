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
    layout.addView(title);

    Button button = new Button(this);
    button.setText("IMPORT VIDEO");
    layout.addView(button);

    button.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    });

    setContentView(layout);
}

}
