package com.gc.contact;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gc.contact.entity.Contact;
import com.gc.contact.util.LogUtil;
import com.gc.contact.util.QRCodeUtil;
import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;
import com.google.zxing.WriterException;

public class CodeCardActivity extends BaseActivity {

    private static final String TAG = CodeCardActivity.class.getSimpleName();
    private LinearLayout mNameLayout;
    private ImageView mQRCodeImage;
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
        mQRCodeImage = (ImageView) findViewById(R.id.id_contact_qr_code);
    }

    @Override
    protected void fetchData() {
        Contact contact = null;
        if (getIntent().getExtras() != null) {
            contact = (Contact) getIntent().getExtras().getSerializable("contact");
        }
        if (contact != null) {
            String name = contact.getDisplayName();
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
                            .buildRound(name.charAt(i) + "", colorGenerator.getColor(name));
                    ImageView imageView = new ImageView(this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
                    imageView.setImageDrawable(drawable);
                    imageView.setPadding(10, 10, 10, 10);
                    mNameLayout.addView(imageView);
                }
            }
            String config = contact.toString();
            LogUtil.d(TAG, config);
            try {
                Bitmap qrBitmap = QRCodeUtil.createQRImage(config);
                mQRCodeImage.setImageBitmap(qrBitmap);
            } catch (WriterException e) {
                LogUtil.e(TAG, "generate qr_code WriterException:" + e.getMessage());
            }
        }
    }

}
