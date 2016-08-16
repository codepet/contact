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

    private EditText mNameText;
    private Spinner mPhoneType;
    private EditText mPhoneText;
    private Spinner mEmailType;
    private EditText mEmailText;
    private LinearLayout mPhonesLayout;
    private LinearLayout mEmailLayout;
    private List<Spinner> mPhoneTypeList;
    private List<EditText> mPhoneDataList;
    private List<Spinner> mEmailTypeList;
    private List<EditText> mEmailDataList;
    private LayoutInflater inflater;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_contact);
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
        mNameText = (EditText) findViewById(R.id.id_add_contact_name);
        mPhoneType = (Spinner) findViewById(R.id.id_phone_type);
        mPhoneText = (EditText) findViewById(R.id.id_add_contact_phone);
        mEmailType = (Spinner) findViewById(R.id.id_email_type);
        mEmailText = (EditText) findViewById(R.id.id_add_contact_email);

        mPhonesLayout = (LinearLayout) findViewById(R.id.id_contact_phone_layout);
        mEmailLayout = (LinearLayout) findViewById(R.id.id_contact_email_layout);
        inflater = LayoutInflater.from(this);
        //
        FloatingActionButton mDoneButton = (FloatingActionButton) findViewById(R.id.id_manager_done);
        if (mDoneButton != null) {
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveConfig();
                }
            });
        }
        //
        Button mAddMorePhoneButton = (Button) findViewById(R.id.id_add_phone_type);
        if (mAddMorePhoneButton != null) {
            mAddMorePhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhoneView();
                }
            });
        }
        //
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
        mPhoneTypeList = new ArrayList<>();
        mPhoneDataList = new ArrayList<>();
        mEmailTypeList = new ArrayList<>();
        mEmailDataList = new ArrayList<>();
        mPhoneTypeList.add(mPhoneType);
        mPhoneDataList.add(mPhoneText);
        mEmailTypeList.add(mEmailType);
        mEmailDataList.add(mEmailText);
    }

    private void addPhoneView() {
        View view = inflater.inflate(R.layout.item_edit_phone, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        mPhoneTypeList.add(spinner);
        mPhoneDataList.add(data);
        mPhonesLayout.addView(view);
    }

    private void addEmailView() {
        View view = inflater.inflate(R.layout.item_edit_email, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        mEmailTypeList.add(spinner);
        mEmailDataList.add(data);
        mEmailLayout.addView(view);
    }

    private void saveConfig() {
        String name = mNameText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(this, getString(R.string.name_null_tips));
            return;
        }
        List<ContactInfo> phones = new ArrayList<>();
        List<ContactInfo> emails = new ArrayList<>();
        Contact.Builder builder = new Contact.Builder();
        for (int i = 0; i < mPhoneDataList.size(); i++) {
            String data = mPhoneDataList.get(i).getText().toString().trim();
            if (TextUtils.isEmpty(data)) {
                continue;
            }
            ContactInfo info = new ContactInfo.Builder()
                    .data(data)
                    .type(ContactsContract.CommonDataKinds.Phone.TYPE)
                    .description(mPhoneTypeList.get(i).getSelectedItemPosition())
                    .build();
            phones.add(info);
        }
        for (int i = 0; i < mEmailDataList.size(); i++) {
            String data = mEmailDataList.get(i).getText().toString().trim();
            if (TextUtils.isEmpty(data)) {
                continue;
            }
            ContactInfo info = new ContactInfo.Builder()
                    .data(data)
                    .type(ContactsContract.CommonDataKinds.Email.TYPE)
                    .description(mEmailTypeList.get(i).getSelectedItemPosition())
                    .build();
            emails.add(info);
        }
        builder.displayName(name).phones(phones).emails(emails);
        Contact contact = builder.build();
        long _id = ContactModel.insert(this, contact);
        if (_id != 0) {
            Intent intent = new Intent();
            intent.putExtra("contact_id", _id);
            intent.putExtra("contact_name", name);
            intent.setAction(AppConstant.ADD_ACTION);
            sendBroadcast(intent);
            intent.setClass(AddActivity.this, DetailActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
