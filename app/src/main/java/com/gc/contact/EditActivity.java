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

import com.gc.contact.entity.Contact;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.model.ContactModel;
import com.gc.contact.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends BaseActivity {

    private EditText mNameText;
    private LinearLayout mPhonesLayout;
    private LinearLayout mEmailLayout;
    private List<Spinner> mPhoneTypeList;
    private List<EditText> mPhoneDataList;
    private List<Spinner> mEmailTypeList;
    private List<EditText> mEmailDataList;
    private LayoutInflater inflater;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_contact);
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
        mNameText = (EditText) findViewById(R.id.id_edit_contact_name);
        mPhonesLayout = (LinearLayout) findViewById(R.id.id_edit_phone_layout);
        mEmailLayout = (LinearLayout) findViewById(R.id.id_edit_email_layout);
        inflater = LayoutInflater.from(this);
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
        Button mAddMorePhoneButton = (Button) findViewById(R.id.id_edit_phone_type);
        if (mAddMorePhoneButton != null) {
            mAddMorePhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhoneView();
                }
            });
        }
        //
        Button mAddMoreEmailButton = (Button) findViewById(R.id.id_edit_email_type);
        if (mAddMoreEmailButton != null) {
            mAddMoreEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addEmailView();
                }
            });
        }
    }

    private void addPhoneView() {
        View view = inflater.inflate(R.layout.item_edit_phone, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        mPhoneTypeList.add(spinner);
        mPhoneDataList.add(data);
        mPhonesLayout.addView(view);
    }

    private void addPhoneView(ContactInfo info) {
        View view = inflater.inflate(R.layout.item_edit_phone, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        spinner.setSelection(info.getDescription());
        data.setText(info.getData());
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

    private void addEmailView(ContactInfo info) {
        View view = inflater.inflate(R.layout.item_edit_email, new LinearLayout(this), false);
        Spinner spinner = (Spinner) view.findViewById(R.id.id_add_contact_type);
        EditText data = (EditText) view.findViewById(R.id.id_add_contact_data);
        spinner.setSelection(info.getDescription());
        data.setText(info.getData());
        mEmailTypeList.add(spinner);
        mEmailDataList.add(data);
        mEmailLayout.addView(view);
    }

    @Override
    protected void fetchData() {
        mPhoneTypeList = new ArrayList<>();
        mPhoneDataList = new ArrayList<>();
        mEmailTypeList = new ArrayList<>();
        mEmailDataList = new ArrayList<>();
        Contact contact = (Contact) getIntent().getExtras().getSerializable("contact");
        if (contact != null) {
            String name = contact.getDisplayName();
            mNameText.setText(name);
            if (!TextUtils.isEmpty(name)) {
                mNameText.setSelection(name.length());
            }
            if (contact.getPhones() != null && contact.getPhones().size() > 0) {
                for (ContactInfo info : contact.getPhones()) {
                    addPhoneView(info);
                }
            } else {
                addPhoneView();
            }
            if (contact.getEmails() != null && contact.getEmails().size() > 0) {
                for (ContactInfo info : contact.getEmails()) {
                    addEmailView(info);
                }
            } else {
                addEmailView();
            }
        }
    }

    private void saveConfig() {
        String name = mNameText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(this, getString(R.string.name_null_tips));
            return;
        }
        List<ContactInfo> phones = new ArrayList<>();
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
        List<ContactInfo> emails = new ArrayList<>();
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
        Contact.Builder builder = new Contact.Builder();
        builder.displayName(name).phones(phones).emails(emails);
        Contact contact = builder.build();
        String type = getIntent().getExtras().getString("type");
        if (type != null && type.equals("edit")) {
            ContactModel.update(this, contact);
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finishAfterTransition();
    }

}
