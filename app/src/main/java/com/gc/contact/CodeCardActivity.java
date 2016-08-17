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
    private LinearLayout mNameLayout;  // 姓名布局
    private ImageView mQRCodeImage;  // 二维码图像
    private static ColorGenerator colorGenerator;  // 颜色生成器

    static {
        colorGenerator = ColorGenerator.MATERIAL;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_qrcode_card);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);  // 设置标题栏
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
            contact = (Contact) getIntent().getExtras().getSerializable("contact");  // 获取传递的联系人对象
        }
        if (contact != null) {
            String name = contact.getDisplayName();
            if (name != null && !name.isEmpty()) {
                for (int i = 0; i < name.length(); i++) {  // 给每一个字构造一张图片
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)  // 字体颜色
                            .fontSize(48)  // 字体大小
                            .useFont(Typeface.DEFAULT)  // 字体样式
                            .width(120)  // 宽度
                            .height(120)  // 高度
                            .endConfig()
                            .buildRound(name.charAt(i) + "", colorGenerator.getRandomColor());
                    ImageView imageView = new ImageView(this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
                    imageView.setImageDrawable(drawable);
                    imageView.setPadding(10, 10, 10, 10);
                    mNameLayout.addView(imageView);
                }
            }
            String config = contact.toString();  // 二维码包含的信息
            try {
                Bitmap qrBitmap = QRCodeUtil.createQRImage(config);  // 生成二维码
                mQRCodeImage.setImageBitmap(qrBitmap);
            } catch (WriterException e) {
                LogUtil.e(TAG, "generate qr_code WriterException:" + e.getMessage());
            }
        }
    }

}
