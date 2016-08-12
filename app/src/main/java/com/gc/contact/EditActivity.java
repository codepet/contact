package com.gc.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gc.contact.entity.Contact;
import com.gc.contact.entity.PhoneNumber;
import com.gc.contact.model.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends BaseActivity {

    private TextView mTypeText;
    private EditText mNameText;
    private EditText mPhoneText;
    private EditText mHomeText;
    private EditText mWorkText;
    private EditText mFaxText;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_manager_contact);
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
        mTypeText = (TextView) findViewById(R.id.id_type);
        mNameText = (EditText) findViewById(R.id.id_manager_contact_name);
        mPhoneText = (EditText) findViewById(R.id.id_manager_contact_phone);
        mHomeText = (EditText) findViewById(R.id.id_manager_contact_home);
        mWorkText = (EditText) findViewById(R.id.id_manager_contact_work);
        mFaxText = (EditText) findViewById(R.id.id_manager_contact_fax);
        FloatingActionButton mDoneButton = (FloatingActionButton) findViewById(R.id.id_manager_done);
        if (mDoneButton != null) {
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveConfig();
                }
            });
        }
    }

    @Override
    protected void fetchData() {
        String name = getIntent().getStringExtra("contact_name");
        String home = getIntent().getStringExtra("1");
        String phone = getIntent().getStringExtra("2");
        String work = getIntent().getStringExtra("3");
        String fax = getIntent().getStringExtra("4");
        mTypeText.setText(getString(R.string.edit_description));
        mNameText.setText(name);
        mPhoneText.setText(phone);
        mHomeText.setText(home);
        mWorkText.setText(work);
        mFaxText.setText(fax);
        if (!name.isEmpty()) {
            mNameText.setSelection(name.length());
        }
    }

    private void saveConfig() {
        String name = mNameText.getText().toString();
        String phone = mPhoneText.getText().toString();
        String home = mHomeText.getText().toString();
        String work = mWorkText.getText().toString();
        String fax = mFaxText.getText().toString();
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(new PhoneNumber.Builder().phoneNumber(home).phoneType("1").build());
        phones.add(new PhoneNumber.Builder().phoneNumber(phone).phoneType("2").build());
        phones.add(new PhoneNumber.Builder().phoneNumber(work).phoneType("3").build());
        phones.add(new PhoneNumber.Builder().phoneNumber(fax).phoneType("4").build());
        Contact.Builder builder = new Contact.Builder();
        builder.displayName(name).phoneNumber(phones);
        Contact contact = builder.build();
        ContactModel.update(this, contact);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finishAfterTransition();
    }

}
