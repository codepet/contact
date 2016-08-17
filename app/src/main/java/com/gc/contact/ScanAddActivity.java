package com.gc.contact;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.contact.constant.AppConstant;
import com.gc.contact.entity.Contact;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.model.ContactModel;
import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;

import java.util.List;

public class ScanAddActivity extends BaseActivity {

    private TextView mNameText;  // 姓名布局
    private LinearLayout mPhoneLayout;  // 电话布局
    private LinearLayout mEmailLayout;  // 邮件布局
    private static ColorGenerator colorGenerator;  // 颜色生成器
    private Contact contact;

    /**
     * 静态预加载数据
     */
    static {
        colorGenerator = ColorGenerator.MATERIAL;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan_add);
        mNameText = (TextView) findViewById(R.id.id_contact_name);
        mPhoneLayout = (LinearLayout) findViewById(R.id.id_contact_phone_layout);
        mEmailLayout = (LinearLayout) findViewById(R.id.id_contact_email_layout);
        FloatingActionButton mAddButton = (FloatingActionButton) findViewById(R.id.id_scan_add);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long _id = ContactModel.insert(ScanAddActivity.this, contact);
                    if (_id != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("contact_id", _id);
                        intent.putExtra("contact_name", contact.getDisplayName());
                        intent.setAction(AppConstant.ADD_ACTION);
                        sendBroadcast(intent);
                        intent.setClass(ScanAddActivity.this, DetailActivity.class);
                        startActivity(intent);
                        ScanAddActivity.this.finish();
                    }
                }
            });
        }
    }

    @Override
    protected void fetchData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            contact = (Contact) getIntent().getExtras().getSerializable("contact");
            if (contact != null) {
                setConfig(contact);
            }
        }
    }

    /**
     * 根据联系人对象将信息展示到界面上
     */
    private void setConfig(Contact contact) {
        String name = contact.getDisplayName();  // 获取联系人姓名
        if (name != null && !name.isEmpty()) {  // 不为空时添加文字图片
            TextDrawable drawable = TextDrawable.builder()  // 设置图片属性
                    .beginConfig()
                    .textColor(Color.WHITE)  // 字体颜色
                    .fontSize(64)  // 字体大小
                    .useFont(Typeface.DEFAULT)  // 字体样式
                    .width(128)  // 宽度
                    .height(128)  // 高度
                    .endConfig()
                    .buildRect(name.charAt(0) + "", colorGenerator.getColor(name));  // 绘制矩形图片
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  // 必须设置此属性，否则图片不显示
            mNameText.setCompoundDrawables(drawable, null, null, null);  // 图片设置在文字顶部
            mNameText.setText(name);  // 显示姓名
        } else {
            mNameText.setCompoundDrawables(null, null, null, null);  // 清空视图
            mNameText.setText("");  // 清空文字
        }
        LayoutInflater inflater = LayoutInflater.from(this);  // 获取布局填充器
        String[] phoneTypes = getResources().getStringArray(R.array.phoneType);  // 从资源文件读取电话类型
        List<ContactInfo> phones = contact.getPhones();  // 获取联系人的电话列表
        mPhoneLayout.removeAllViews();  // 添加布局前先移除所有布局
        if (phones == null || phones.size() == 0) {  // 电话列表为空时，添加空提示布局
            View emptyPhone = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mPhoneLayout.addView(emptyPhone);
        } else {
            for (int i = 0; i < phones.size(); i++) {  // 遍历电话列表
                View view = inflater.inflate(R.layout.item_scan_add, new LinearLayout(this), false);
                TextView type = (TextView) view.findViewById(R.id.id_user_type);  // 电话的类型
                TextView data = (TextView) view.findViewById(R.id.id_user_data);  // 电话
                type.setText(phoneTypes[phones.get(i).getDescription()]);
                data.setText(phones.get(i).getData());
                mPhoneLayout.addView(view);
            }
        }
        String[] emailTypes = getResources().getStringArray(R.array.emailType);  // 从资源文件读取邮件类型
        List<ContactInfo> emails = contact.getEmails();  // 获取联系人的邮件列表
        mEmailLayout.removeAllViews();  // 添加布局前先移除所有布局
        if (emails == null || emails.size() == 0) {  // 邮件列表为空时，添加空提示布局
            View emptyEmail = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mEmailLayout.addView(emptyEmail);
        } else {
            for (int i = 0; i < emails.size(); i++) {  // 遍历邮件列表
                View view = inflater.inflate(R.layout.item_scan_add, new LinearLayout(this), false);
                TextView type = (TextView) view.findViewById(R.id.id_user_type);  // 邮件的类型
                TextView data = (TextView) view.findViewById(R.id.id_user_data);  // 邮件
                type.setText(emailTypes[emails.get(i).getDescription()]);
                data.setText(emails.get(i).getData());
                mEmailLayout.addView(view);
            }
        }
    }
}
