package com.gc.contact;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;

public class CodeCardActivity extends BaseActivity {

    private LinearLayout mNameLayout;
    private static ColorGenerator colorGenerator;

    static {
        colorGenerator = ColorGenerator.MATERIAL;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_qrcode_card);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAfterTransition();
                }
            });
        }
        mNameLayout = (LinearLayout) findViewById(R.id.id_name_layout);
    }

    @Override
    protected void fetchData() {
        String name = getIntent().getStringExtra("contact_name");
        if (name != null && !name.isEmpty()) {
            for (int i = 0; i < name.length(); i++) {
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .fontSize(48)
                        .useFont(Typeface.DEFAULT)
                        .width(120)
                        .height(120)
                        .endConfig()
                        .buildRound(name.charAt(i) + "", colorGenerator.getRandomColor());
                ImageView imageView = new ImageView(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(params);
                imageView.setImageDrawable(drawable);
                imageView.setPadding(10, 10, 10, 10);
                mNameLayout.addView(imageView, params);
            }
        }
    }

}
