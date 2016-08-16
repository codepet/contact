package com.gc.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import com.gc.contact.widget.AlphabetScrollBar;
import com.gc.contact.widget.DividerItemDecoration;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mContactList;  // 联系人列表视图
    private ContactAdapter mAdapter;  // 列表适配器
    private List<Contact> mContacts;  // 联系人列表数据
    private FloatingActionButton mAddButton;  // 添加按钮
    private UpdateUIReceiver receiver;  // 更新界面广播接收者
    private static CharacterParser characterParser;  // 拼音转换对象

    /**
     * 静态预加载
     */
    static {
        characterParser = CharacterParser.getInstance();  // 获取实例对象
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
                    gotoMyself();
                }
            });
        }
        mAddButton = (FloatingActionButton) findViewById(R.id.id_contact_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAdd();
            }
        });
        // 初始化列表
        mContactList = (RecyclerView) findViewById(R.id.id_contact_list);
        mContactList.setLayoutManager(new LinearLayoutManager(this));  // 线性管理器
        mContactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));  // 添加分割线
        // 初始化字母索引条
        TextView mLetterNotice = (TextView) findViewById(R.id.id_letter_notice);
        AlphabetScrollBar mAlphabetBar = (AlphabetScrollBar) findViewById(R.id.id_alphabet_bar);
        if (mAlphabetBar != null) {
            mAlphabetBar.setTextView(mLetterNotice);  // 设置触碰提示文字
            mAlphabetBar.setOnTouchBarListener(new AlphabetScrollBar.OnTouchBarListener() {
                @Override
                public void onTouch(String letter) {
                    final int pos = mAdapter.getPositionForSection(letter.charAt(0));  // 获取首次出现字母位置
                    if (pos != -1) {
                        ((LinearLayoutManager) mContactList.getLayoutManager()).scrollToPositionWithOffset(pos, 0);  // 滑动到指定位置
                    }
                }
            });
        }
        // 注册广播接收者
        receiver = new UpdateUIReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        // 添加自定义动作
        filter.addAction(AppConstant.ADD_ACTION);
        filter.addAction(AppConstant.DELETE_ACTION);
        filter.addAction(AppConstant.UPDATE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void fetchData() {
        mContacts = ContactModel.getPhoneContact(this);  // 获取系统联系人列表
        Collections.sort(mContacts, new Contact.Builder().build());  // 进行排序
        mAdapter = new ContactAdapter(this, mContacts);  // 初始化适配器
        mContactList.setAdapter(mAdapter);  // 列表设置适配器
        mAdapter.setOnItemListener(new ContactAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                gotoDetail(position);
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
        unregisterReceiver(receiver);  // 注销广播接收者
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:  // 扫一扫
                gotoScan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理扫一扫返回的结果
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Type type = new TypeToken<Contact>() {
            }.getType();
            try {
                Contact contact = new Gson().fromJson(re, type);
                if (contact != null) {
                    Intent intent = new Intent(MainActivity.this, ScanAddActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", contact);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "after scan exception:" + e.getMessage());
            }

        }
    }

    /**
     * 跳转至联系人详情界面
     *
     * @param position 列表位序
     */
    private void gotoDetail(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("contact_id", mContacts.get(position).getContactID());
        intent.putExtra("contact_name", mContacts.get(position).getDisplayName());
        intent.putExtra("position", position);
        // 视图共享,Android5.0后生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mAddButton, "fab");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * 跳转至添加联系人界面
     */
    private void gotoAdd() {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        // 视图共享,Android5.0后生效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, mAddButton, "fab");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * 跳转至我的名片界面
     */
    private void gotoMyself() {
        startActivity(new Intent(MainActivity.this, MyselfActivity.class));
    }

    /**
     * 跳转至扫一扫界面
     */
    private void gotoScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * 更新UI广播接收者
     */
    public class UpdateUIReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();  // 获取请求动作
            if (action == null) {
                return;
            }
            switch (action) {
                case AppConstant.ADD_ACTION:  // 添加联系人动作
                    add(intent);
                    break;
                case AppConstant.DELETE_ACTION:  // 删除联系人动作
                    delete(intent);
                    break;
                case AppConstant.UPDATE_ACTION:
                    update(intent);
                    break;
            }
        }
    }

    /**
     * 添加数据
     *
     * @param intent 数据
     */
    private void add(Intent intent) {
        String add_name = intent.getStringExtra("contact_name");  // 获取添加的联系人姓名
        long add_id = intent.getLongExtra("contact_id", 0);  // 获取添加的联系人ID
        String add_pinyin = characterParser.getSelling(add_name);  // 设置姓名拼音
        String add_sortString = add_pinyin.substring(0, 1).toUpperCase();  // 获取拼音首字母
        Contact.Builder add_builder = new Contact.Builder().displayName(add_name).buildID(add_id);
        // 正则表达式，判断首字母是否是英文字母
        if (add_sortString.matches("[A-Z]")) {
            add_builder.sortLetter(add_pinyin.toUpperCase());
        } else {
            add_builder.sortLetter("#");
        }
        Contact add_contact = new Contact(add_builder);
        mContacts.add(add_contact);  // 添加至列表中
        Collections.sort(mContacts, new Contact.Builder().build());  // 重新排序
        mAdapter.notifyDataSetChanged();  // 通知更新界面
    }

    /**
     * 删除数据
     *
     * @param intent 数据
     */
    private void delete(Intent intent) {
        String delete_name = intent.getStringExtra("contact_name");  // 获取添加的联系人姓名
        long delete_id = intent.getLongExtra("contact_id", 0);  // 获取添加的联系人ID
        String delete_pinyin = characterParser.getSelling(delete_name);  // 设置姓名拼音
        String delete_sortString = delete_pinyin.substring(0, 1).toUpperCase();  // 获取拼音首字母
        Contact.Builder builder = new Contact.Builder().displayName(delete_name).buildID(delete_id);
        // 正则表达式，判断首字母是否是英文字母
        if (delete_sortString.matches("[A-Z]")) {
            builder.sortLetter(delete_pinyin.toUpperCase());
        } else {
            builder.sortLetter("#");
        }
        Contact delete_contact = new Contact(builder);
        mContacts.remove(delete_contact);  // 添加至列表中
        mAdapter.notifyDataSetChanged();  // 通知更新界面
    }

    /**
     * 更新数据
     *
     * @param intent 数据
     */
    private void update(Intent intent) {

    }


}
