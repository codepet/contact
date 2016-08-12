package com.gc.contact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.contact.constant.AppConstant;
import com.gc.contact.entity.PhoneNumber;
import com.gc.contact.model.ContactModel;
import com.gc.contact.util.ContactUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private LinearLayout mPhonesLayout;
    private Button mCodeButton;
    private FloatingActionButton mEditButton;
    private FloatingActionButton mDeleteButton;
    private long _id;
    private String name;
    private List<PhoneNumber> phoneNumbers;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAfterTransition();
                }
            });
        }
        mPhonesLayout = (LinearLayout) findViewById(R.id.id_phones_layout);
        mCodeButton = (Button) findViewById(R.id.id_contact_code);
        mEditButton = (FloatingActionButton) findViewById(R.id.id_contact_edit);
        mDeleteButton = (FloatingActionButton) findViewById(R.id.id_contact_delete);
        mCodeButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
    }

    @Override
    protected void fetchData() {
        name = getIntent().getStringExtra("contact_name");
        _id = getIntent().getLongExtra("contact_id", -1);
        if (_id != -1) {
            phoneNumbers = ContactModel.getPhones(this, _id);
            if (phoneNumbers.size() > 0) {
                LayoutInflater inflater = getLayoutInflater();
                for (final PhoneNumber phone : phoneNumbers) {
                    View view = inflater.inflate(R.layout.item_phone, new RelativeLayout(this), false);
                    TextView phoneNum = (TextView) view.findViewById(R.id.id_contact_phone);
                    TextView phoneType = (TextView) view.findViewById(R.id.id_contact_phone_type);
                    ImageButton smsButton = (ImageButton) view.findViewById(R.id.id_contact_sms);
                    phoneNum.setText(phone.getPhoneNumber());
                    phoneType.setText(ContactUtil.getPhoneType(Integer.parseInt(phone.getPhoneType())));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + phone.getPhoneNumber());
                            intent.setData(data);
                            startActivity(intent);
                        }
                    });
                    smsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("smsto:" + phone.getPhoneNumber());
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                            sendIntent.putExtra("sms_body", "");
                            startActivity(sendIntent);
                        }
                    });
                    mPhonesLayout.addView(view);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setTitle(name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_contact_code:
                Intent intent_qr_code = new Intent(DetailActivity.this, CodeCardActivity.class);
                intent_qr_code.putExtra("contact_name", name);
                startActivity(intent_qr_code);
                break;
            case R.id.id_contact_edit:
                Intent intent_edit = new Intent(DetailActivity.this, EditActivity.class);
                intent_edit.putExtra("contact_name", name);
                for (PhoneNumber phone : phoneNumbers) {
                    intent_edit.putExtra(phone.getPhoneType(), phone.getPhoneNumber());
                }
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(DetailActivity.this, mEditButton, "fab");
                startActivity(intent_edit, options.toBundle());
                break;
            case R.id.id_contact_delete:
                delete();
                break;
        }
    }

    private void delete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("提示:")
                .setMessage("是否删除" + name + "?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContactModel.delete(DetailActivity.this, _id);
                        Intent intent = new Intent();
                        intent.putExtra("position", getIntent().getIntExtra("position", -1));
                        intent.setAction(AppConstant.DELETE_ACTION);
                        sendBroadcast(intent);
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
