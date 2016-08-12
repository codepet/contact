package com.gc.contact;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gc.contact.entity.Contact;
import com.gc.contact.entity.PhoneNumber;
import com.gc.contact.util.LogUtil;
import com.gc.contact.util.SerializeUtil;
import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;

import java.io.IOException;

public class MyselfActivity extends BaseActivity {

    private static final String TAG = MyselfActivity.class.getSimpleName();
    private TextView mUserNameText;
    private Button mUserQrCodeButton;
    private FloatingActionButton mEditButton;
    private TextView mUserPhoneText;
    private TextView mUserHomeText;
    private TextView mUserWorkText;
    private TextView mUserFaxText;
    private CardView mCardView;
    private static ColorGenerator colorGenerator;

    static {
        colorGenerator = ColorGenerator.MATERIAL;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_myself);
        TextView mBarTagText = (TextView) findViewById(R.id.id_bar_tag);
        mUserNameText = (TextView) findViewById(R.id.id_user_name);
        mUserQrCodeButton = (Button) findViewById(R.id.id_user_code);
        mEditButton = (FloatingActionButton) findViewById(R.id.id_edit_config);
        mUserPhoneText = (TextView) findViewById(R.id.id_user_phone);
        mUserHomeText = (TextView) findViewById(R.id.id_user_home);
        mUserWorkText = (TextView) findViewById(R.id.id_user_work);
        mUserFaxText = (TextView) findViewById(R.id.id_user_fax);
        mCardView = (CardView) findViewById(R.id.id_contact_card);
        if (mBarTagText != null) {
            mBarTagText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mUserQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyselfActivity.this, CodeCardActivity.class);
                intent.putExtra("contact_name", mUserNameText.getText().toString());
                startActivity(intent);
            }
        });
        mEditButton.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyselfActivity.this, EditActivity.class);

                intent.putExtra("contact_name", mUserNameText.getText().toString());
                intent.putExtra("1", mUserHomeText.getText().toString());
                intent.putExtra("2", mUserPhoneText.getText().toString());
                intent.putExtra("3", mUserWorkText.getText().toString());
                intent.putExtra("4", mUserFaxText.getText().toString());

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                MyselfActivity.this,
                                new Pair<View, String>(mCardView, "card"),
                                new Pair<View, String>(mEditButton, "fab"));
                startActivityForResult(intent, 200, options.toBundle());
            }
        });
    }

    @Override
    protected void fetchData() {
        try {
            Contact contact = (Contact) SerializeUtil.getObject(this, "user");
            if (contact != null) {
                String displayName = contact.getDisplayName();
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .fontSize(60)
                        .useFont(Typeface.DEFAULT)
                        .width(200)
                        .height(200)
                        .endConfig()
                        .buildRound(displayName.charAt(0) + "", colorGenerator.getColor(displayName));
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mUserNameText.setCompoundDrawables(null, drawable, null, null);
                mUserNameText.setText(displayName);
                for (PhoneNumber phone : contact.getPhoneNumber()) {
                    switch (phone.getPhoneType()) {
                        case "1":
                            mUserHomeText.setText(phone.getPhoneNumber());
                            break;
                        case "2":
                            mUserPhoneText.setText(phone.getPhoneNumber());
                            break;
                        case "3":
                            mUserWorkText.setText(phone.getPhoneNumber());
                            break;
                        case "4":
                            mUserFaxText.setText(phone.getPhoneNumber());
                            break;
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "getObject --> IOException:" + e.getMessage());
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, "getObject --> ClassNotFoundException:" + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data != null) {
                Contact contact = (Contact) data.getExtras().getSerializable("contact");
                if (contact != null) {
                    String name = contact.getDisplayName();
                    String phone = contact.getPhoneNumber().get(1).getPhoneNumber();
                    String home = contact.getPhoneNumber().get(0).getPhoneNumber();
                    String work = contact.getPhoneNumber().get(2).getPhoneNumber();
                    String fax = contact.getPhoneNumber().get(3).getPhoneNumber();
                    if (name != null && !name.isEmpty()) {
                        TextDrawable drawable = TextDrawable.builder()
                                .beginConfig()
                                .textColor(Color.WHITE)
                                .fontSize(60)
                                .useFont(Typeface.DEFAULT)
                                .width(200)
                                .height(200)
                                .endConfig()
                                .buildRound(name.charAt(0) + "", colorGenerator.getColor(name));
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mUserNameText.setCompoundDrawables(null, drawable, null, null);
                    } else {
                        mUserNameText.setCompoundDrawables(null, null, null, null);
                    }
                    mUserNameText.setText(name);
                    mUserPhoneText.setText(phone);
                    mUserHomeText.setText(home);
                    mUserWorkText.setText(work);
                    mUserFaxText.setText(fax);
                    try {
                        SerializeUtil.saveObject(this, contact, "user");
                    } catch (IOException e) {
                        LogUtil.e(TAG, "saveObject --> IOException:" + e.getMessage());
                    }
                }
            }
        }
    }
}
