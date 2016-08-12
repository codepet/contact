package com.gc.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gc.contact.adapter.ContactAdapter;
import com.gc.contact.constant.AppConstant;
import com.gc.contact.entity.Contact;
import com.gc.contact.model.ContactModel;
import com.gc.contact.util.CharacterParser;
import com.gc.contact.util.LogUtil;
import com.gc.contact.util.PinyinComparator;
import com.gc.contact.widget.AlphabetScrollBar;
import com.gc.contact.widget.DividerItemDecoration;

import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RecyclerView mContactList;
    private ContactAdapter mAdapter;
    private List<Contact> mContacts;
    private FloatingActionButton mAddButton;
    private UpdateUIReceiver receiver;
    private static CharacterParser characterParser;

    static {
        characterParser = CharacterParser.getInstance();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, MyselfActivity.class));
                }
            });
        }
        mAddButton = (FloatingActionButton) findViewById(R.id.id_contact_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mAddButton, "fab");
                startActivity(intent, options.toBundle());
            }
        });
        // 初始化列表
        mContactList = (RecyclerView) findViewById(R.id.id_contact_list);
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        mContactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        // 初始化字母索引条
        TextView mLetterNotice = (TextView) findViewById(R.id.id_letter_notice);
        AlphabetScrollBar mAlphabetBar = (AlphabetScrollBar) findViewById(R.id.id_alphabet_bar);
        if (mAlphabetBar != null) {
            mAlphabetBar.setTextView(mLetterNotice);
            mAlphabetBar.setOnTouchBarListener(new AlphabetScrollBar.OnTouchBarListener() {
                @Override
                public void onTouch(String letter) {
                    final int pos = mAdapter.getPositionForSection(letter.charAt(0));
                    if (pos != -1) {
                        ((LinearLayoutManager) mContactList.getLayoutManager()).scrollToPositionWithOffset(pos, 0);
                    }
                }
            });
        }
        receiver = new UpdateUIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(AppConstant.ADD_ACTION);
        filter.addAction(AppConstant.DELETE_ACTION);
        filter.addAction(AppConstant.UPDATE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void fetchData() {
        mContacts = ContactModel.getPhoneContact(this);
        Collections.sort(mContacts, new PinyinComparator());
        mAdapter = new ContactAdapter(this, mContacts);
        mContactList.setAdapter(mAdapter);
        mAdapter.setOnItemListener(new ContactAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("contact_id", mContacts.get(position).getContactID());
                intent.putExtra("contact_name", mContacts.get(position).getDisplayName());
                intent.putExtra("position", position);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mAddButton, "fab");
                startActivity(intent, options.toBundle());
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UpdateUIReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            LogUtil.i("UpdateUIReceiver", "receiver action:" + action);
            switch (action) {
                case AppConstant.ADD_ACTION:
                    String add_name = intent.getStringExtra("contact_name");
                    long add_id = intent.getLongExtra("contact_id", 0);
                    String add_pinyin = characterParser.getSelling(add_name);
                    String add_sortString = add_pinyin.substring(0, 1).toUpperCase();
                    Contact.Builder builder = new Contact.Builder().displayName(add_name).buildID(add_id);
                    if (add_sortString.matches("[A-Z]")) {
                        builder.sortLetter(add_pinyin.toUpperCase());
                    } else {
                        builder.sortLetter("#");
                    }
                    Contact add_contact = new Contact(builder);
                    mContacts.add(add_contact);
                    Collections.sort(mContacts, new PinyinComparator());
                    mAdapter.notifyDataSetChanged();
                    break;
                case AppConstant.DELETE_ACTION:
                    int pos = intent.getIntExtra("position", -1);
                    if (pos != -1) {
                        mContacts.remove(pos);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case AppConstant.UPDATE_ACTION:
                    break;
            }
        }
    }


}
