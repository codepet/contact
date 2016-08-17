package com.gc.contact;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.gc.contact.constant.AppConstant;
import com.gc.contact.entity.Contact;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.model.ContactModel;
import com.gc.contact.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends BaseActivity {

    private EditText mNameText;  // 姓名编辑框
    private Spinner mPhoneType;  // 电话类型下拉选项
    private EditText mPhoneText;  // 电话编辑框
    private Spinner mEmailType;  // 邮件类型下拉选项
    private EditText mEmailText;  // 邮件编辑框
    private LinearLayout mPhonesLayout;  // 动态添加电话布局
    private LinearLayout mEmailLayout;  // 动态添加邮件布局
    private List<Spinner> mPhoneTypeList;  // 电话下来选项列表
    private List<EditText> mPhoneDataList;  // 电话编辑列表
    private List<Spinner> mEmailTypeList;  // 邮件下拉选项列表
    private List<EditText> mEmailDataList;  // 邮件编辑列表
    private LayoutInflater inflater;  // 布局填充器

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_contact);
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
        mNameText = (EditText) findViewById(R.id.id_add_contact_name);
        mPhoneType = (Spinner) findViewById(R.id.id_phone_type);
        mPhoneText = (EditText) findViewById(R.id.id_add_contact_phone);
        mEmailType = (Spinner) findViewById(R.id.id_email_type);
        mEmailText = (EditText) findViewById(R.id.id_add_contact_email);
        mPhonesLayout = (LinearLayout) findViewById(R.id.id_contact_phone_layout);
        mEmailLayout = (LinearLayout) findViewById(R.id.id_contact_email_layout);
        inflater = LayoutInflater.from(this);
        //  完成按钮
        FloatingActionButton mDoneButton = (FloatingActionButton) findViewById(R.id.id_manager_done);
        if (mDoneButton != null) {
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveConfig();
                }
            });
        }
        //  添加更多电话项按钮
        Button mAddMorePhoneButton = (Button) findViewById(R.id.id_add_phone_type);
        if (mAddMorePhoneButton != null) {
            mAddMorePhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhoneView();
                }
            });
        }
        //  添加更多邮件项按钮
        Button mAddMoreEmailButton = (Button) findViewById(R.id.id_add_email_type);
        if (mAddMoreEmailButton != null) {
            mAddMoreEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addEmailView();
                }
            });
        }
    }

    @Override
    protected void fetchData() {
        // 初始化各列表
        mPhoneTypeList = new ArrayList<>();
        mPhoneDataList = new ArrayList<>();
        mEmailTypeList = new ArrayList<>();
        mEmailDataList = new ArrayList<>();
        // 把默认的控件加入至列表中
        mPhoneTypeList.add(mPhoneType);
        mPhoneDataList.add(mPhoneText);
        mEmailTypeList.add(mEmailType);
        mEmailDataList.add(mEmailText);
    }

    /**
     * 添加电话布局
     */
    private void addPhoneView() {
        View view = inflater.inflate(R.layout.item_edit_phone, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        // 把新增的控件加入至列表中进行管理
        mPhoneTypeList.add(spinner);
        mPhoneDataList.add(data);
        mPhonesLayout.addView(view);
    }

    /**
     * 添加邮件布局
     */
    private void addEmailView() {
        View view = inflater.inflate(R.layout.item_edit_email, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        // 把新增的控件加入至列表中进行管理
        mEmailTypeList.add(spinner);
        mEmailDataList.add(data);
        mEmailLayout.addView(view);
    }

    /**
     * 保存联系人
     */
    private void saveConfig() {
        String name = mNameText.getText().toString();
        if (TextUtils.isEmpty(name)) {  // 限制条件，名字不能为空
            ToastUtil.show(this, getString(R.string.name_null_tips));
            return;
        }
        List<ContactInfo> phones = new ArrayList<>();
        List<ContactInfo> emails = new ArrayList<>();
        Contact.Builder builder = new Contact.Builder();
        for (int i = 0; i < mPhoneDataList.size(); i++) {  // 遍历电话列表
            String data = mPhoneDataList.get(i).getText().toString().trim();
            if (TextUtils.isEmpty(data)) {  // 过滤为空的选项
                continue;
            }
            ContactInfo info = new ContactInfo.Builder()
                    .data(data)
                    .type(ContactsContract.CommonDataKinds.Phone.TYPE)
                    .description(mPhoneTypeList.get(i).getSelectedItemPosition())
                    .build();
            phones.add(info);  // 添加电话属性
        }
        for (int i = 0; i < mEmailDataList.size(); i++) {  // 遍历邮件列表
            String data = mEmailDataList.get(i).getText().toString().trim();
            if (TextUtils.isEmpty(data)) {  // 过滤为空的选项
                continue;
            }
            ContactInfo info = new ContactInfo.Builder()
                    .data(data)
                    .type(ContactsContract.CommonDataKinds.Email.TYPE)
                    .description(mEmailTypeList.get(i).getSelectedItemPosition())
                    .build();
            emails.add(info);  // 添加邮件属性
        }
        builder.displayName(name).phones(phones).emails(emails);
        Contact contact = builder.build();
        long _id = ContactModel.insert(this, contact);  // 新增联系人，并返回插入数据库后的id
        if (_id != -1) {  // id = -1 表示插入失败
            Intent intent = new Intent();
            intent.putExtra("contact_id", _id);
            intent.putExtra("contact_name", name);
            intent.setAction(AppConstant.ADD_ACTION);  // 设置更新动作
            sendBroadcast(intent);  // 发送广播通知主界面更新列表
            intent.setClass(AddActivity.this, DetailActivity.class);
            startActivity(intent);  // 跳转至联系人详情界面
            this.finish();
        }
    }
}
