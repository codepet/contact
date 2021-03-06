package com.gc.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.contact.constant.AppConstant;
import com.gc.contact.entity.Contact;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.model.ContactModel;

import java.util.List;

public class DetailActivity extends BaseActivity {

    private Toolbar mToolbar;  // 标题栏
    private LinearLayout mPhonesLayout;  // 电话布局
    private LinearLayout mEmailsLayout;  // 邮件布局
    private FloatingActionButton mEditButton;  // 编辑按钮
    private long _id;  // 联系人id
    private String name;   // 联系人姓名
    private List<ContactInfo> mPhones;  // 电话列表
    private List<ContactInfo> mEmails;  // 邮件列表
    private LayoutInflater inflater;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAfterTransition();
                }
            });
        }
        mPhonesLayout = (LinearLayout) findViewById(R.id.id_contact_phone_layout);
        mEmailsLayout = (LinearLayout) findViewById(R.id.id_contact_email_layout);
        mEditButton = (FloatingActionButton) findViewById(R.id.id_contact_edit);
        if (mEditButton != null) {
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoEdit();
                }
            });
        }
        Button mCodeButton = (Button) findViewById(R.id.id_contact_code);
        if (mCodeButton != null) {
            mCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoCode();
                }
            });
        }
        FloatingActionButton mDeleteButton = (FloatingActionButton) findViewById(R.id.id_contact_delete);
        if (mDeleteButton != null) {
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
        }
        inflater = LayoutInflater.from(this);
    }

    @Override
    protected void fetchData() {
        name = getIntent().getStringExtra("contact_name");
        _id = getIntent().getLongExtra("contact_id", -1);
        if (_id != -1) {
            mPhones = ContactModel.getPhones(this, _id);
            mEmails = ContactModel.getEmails(this, _id);
            setConfig();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setTitle(name);  // 标题设置为联系人姓名，此方法仅在onResume()中生效
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Contact contact = (Contact) data.getExtras().getSerializable("contact");
                if (contact != null) {
                    name = contact.getDisplayName();
                    mPhones = contact.getPhones();
                    mEmails = contact.getEmails();
                    setConfig();
                }
            }
        }
    }

    /**
     * 根据联系人信息设置界面信息
     */
    private void setConfig() {
        // 电话号码布局
        mPhonesLayout.removeAllViews();
        if (mPhones != null && mPhones.size() > 0) {
            for (ContactInfo phone : mPhones) {
                addPhoneVew(phone);
            }
        } else {
            View emptyPhone = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mPhonesLayout.addView(emptyPhone);
        }
        // 电子邮件
        mEmailsLayout.removeAllViews();
        if (mEmails != null && mEmails.size() > 0) {
            for (ContactInfo email : mEmails) {
                addEmailView(email);
            }
        } else {
            View emptyEmail = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mEmailsLayout.addView(emptyEmail);
        }
    }

    /**
     * 添加电话布局
     *
     * @param phone 添加的电话信息
     */
    private void addPhoneVew(final ContactInfo phone) {
        View view = inflater.inflate(R.layout.item_phone, new RelativeLayout(this), false);
        TextView data = (TextView) view.findViewById(R.id.id_contact_phone);
        TextView type = (TextView) view.findViewById(R.id.id_contact_phone_type);
        ImageButton smsButton = (ImageButton) view.findViewById(R.id.id_contact_sms);
        data.setText(phone.getData());
        type.setText(Phone.getTypeLabelResource(phone.getDescription()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone.getData());
                intent.setData(data);
                startActivity(intent);
            }
        });
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 发送信息
                Uri uri = Uri.parse("smsto:" + phone.getData());
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                sendIntent.putExtra("sms_body", "");
                startActivity(sendIntent);
            }
        });
        mPhonesLayout.addView(view);
    }

    /**
     * 添加邮箱布局
     *
     * @param email 添加的邮箱信息
     */
    private void addEmailView(final ContactInfo email) {
        View view = inflater.inflate(R.layout.item_email, new RelativeLayout(this), false);
        TextView data = (TextView) view.findViewById(R.id.id_contact_email);
        TextView type = (TextView) view.findViewById(R.id.id_contact_email_type);
        data.setText(email.getData());
        type.setText(Email.getTypeLabelResource(email.getDescription()));  // 根据标签获取类型名字
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mEmailsLayout.addView(view);
    }

    /**
     * 跳转至二维码名片界面
     */
    private void gotoCode() {
        Contact contact = new Contact.Builder().displayName(name).phones(mPhones).emails(mEmails).build();
        Intent intent_qr_code = new Intent(DetailActivity.this, CodeCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent_qr_code.putExtras(bundle);
        startActivity(intent_qr_code);
    }

    /**
     * 跳转至编辑界面
     */
    private void gotoEdit() {
        Contact contact = new Contact.Builder().buildID(_id).displayName(name).phones(mPhones).emails(mEmails).build();
        Intent intent_edit = new Intent(DetailActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        bundle.putString("type", "edit");
        bundle.putInt("position", getIntent().getIntExtra("position", 0));
        intent_edit.putExtras(bundle);
        // 视图共享,Android5.0后生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(DetailActivity.this, mEditButton, "fab");
            startActivityForResult(intent_edit, 200, options.toBundle());
        } else {
            startActivityForResult(intent_edit, 200);
        }
    }

    /**
     * 删除提示
     */
    private void delete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("提示:")
                .setMessage("是否删除" + name + "?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContactModel.delete(DetailActivity.this, _id);  // 删除联系人
                        Intent intent = new Intent();
                        intent.putExtra("contact_id", _id);
                        intent.putExtra("contact_name", name);
                        intent.setAction(AppConstant.DELETE_ACTION);  // 设置删除动作
                        sendBroadcast(intent);  // 发送广播更新界面
                        dialog.dismiss();
                        finish();  // 关闭当前页面
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
