package com.gc.contact;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.contact.app.BaseApplication;
import com.gc.contact.entity.Contact;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.util.LogUtil;
import com.gc.contact.util.SerializeUtil;
import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;

import java.io.IOException;
import java.util.List;

public class MyselfActivity extends BaseActivity {

    private static final String TAG = MyselfActivity.class.getSimpleName();
    private TextView mUserNameText;  // 姓名显示文本
    private FloatingActionButton mEditButton;  // 编辑按钮
    private CardView mPhoneCardView;  // 电话布局
    private CardView mEmailCardView;  // 邮件布局
    private LinearLayout mPhoneLayout;  // 电话列表布局
    private LinearLayout mEmailLayout;  // 邮件列表不u剧
    private static ColorGenerator colorGenerator;  // 颜色生成器
    private static Contact contact;  // 联系人对象

    /**
     * 静态预加载数据
     */
    static {
        colorGenerator = ColorGenerator.MATERIAL;
        try {
            contact = (Contact) SerializeUtil.getObject(BaseApplication.getContext(), "user");  // 从本地获取序列化对象
        } catch (IOException e) {
            LogUtil.e(TAG, "getObject --> IOException:" + e.getMessage());
        } catch (ClassNotFoundException e) {
            LogUtil.e(TAG, "getObject --> ClassNotFoundException:" + e.getMessage());
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_myself);
        mUserNameText = (TextView) findViewById(R.id.id_user_name);
        mPhoneCardView = (CardView) findViewById(R.id.id_my_phone_card);
        mEmailCardView = (CardView) findViewById(R.id.id_my_email_card);
        mPhoneLayout = (LinearLayout) findViewById(R.id.id_my_phone_layout);
        mEmailLayout = (LinearLayout) findViewById(R.id.id_my_email_layout);
        mEditButton = (FloatingActionButton) findViewById(R.id.id_edit_config);
        mEditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoEdit();  // 跳转至编辑界面
            }
        });
        TextView mBarTagText = (TextView) findViewById(R.id.id_bar_tag);  // 顶部返回文字
        if (mBarTagText != null) {  // 控件声明为局部变量时，有可能产生空指针错误，所以需判空
            mBarTagText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();  // 关闭返回
                }
            });
        }
        Button mUserQrCodeButton = (Button) findViewById(R.id.id_user_code); // 二维码名片按钮
        if (mUserQrCodeButton != null) {  // 控件声明为局部变量时，有可能产生空指针错误，所以需判空
            mUserQrCodeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    gotoCode();  // 跳转至二维码名片界面
                }
            });
        }

    }

    @Override
    protected void fetchData() {
        if (contact != null) {
            setConfig();
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);  // 获取布局填充器
            View emptyPhone = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            View emptyEmail = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mPhoneLayout.removeAllViews();
            mPhoneLayout.addView(emptyPhone);
            mEmailLayout.removeAllViews();
            mEmailLayout.addView(emptyEmail);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 200 && resultCode == RESULT_OK) {  // 返回成功码
            if (intent != null && intent.getExtras() != null) {
                contact = (Contact) intent.getExtras().getSerializable("contact");  // 获取联系人对象
                if (contact != null) {
                    setConfig();  // 设置信息
                    try {
                        SerializeUtil.saveObject(this, contact, "user");  // 信息序列化保存至本地
                    } catch (IOException e) {
                        LogUtil.e(TAG, "saveObject --> IOException:" + e.getMessage());
                    }
                } else {
                    LayoutInflater inflater = LayoutInflater.from(this);  // 获取布局填充器
                    View emptyPhone = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
                    View emptyEmail = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
                    mPhoneLayout.removeAllViews();
                    mPhoneLayout.addView(emptyPhone);
                    mEmailLayout.removeAllViews();
                    mEmailLayout.addView(emptyEmail);
                }
            }
        }
    }

    /**
     * 跳转至编辑界面
     */
    @SuppressWarnings("unchecked")
    private void gotoEdit() {
        Intent intent = new Intent(MyselfActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);  // 启动Activity传递联系人对象
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 多个视图共享,Android5.0后生效
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            MyselfActivity.this,
                            new Pair<View, String>(mPhoneCardView, "phone_card"),
                            new Pair<View, String>(mEmailCardView, "email_card"),
                            new Pair<View, String>(mEditButton, "fab"));
            startActivityForResult(intent, 200, options.toBundle());
        } else {
            startActivityForResult(intent, 200);
        }
    }

    /**
     * 跳转至二维码名片界面
     */
    private void gotoCode() {
        Intent intent = new Intent(MyselfActivity.this, CodeCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);  // 启动Activity传递联系人对象
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 根据联系人对象将信息展示到界面上
     */
    private void setConfig() {
        String name = contact.getDisplayName();  // 获取联系人姓名
        if (name != null && !name.isEmpty()) {  // 不为空时添加文字图片
            TextDrawable drawable = TextDrawable.builder()  // 设置图片属性
                    .beginConfig()
                    .textColor(Color.WHITE)  // 字体颜色
                    .fontSize(60)  // 字体大小
                    .useFont(Typeface.DEFAULT)  // 字体样式
                    .width(200)  // 宽度
                    .height(200)  // 高度
                    .endConfig()
                    .buildRound(name.charAt(0) + "", colorGenerator.getColor(name));  // 绘制圆形图片
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  // 必须设置此属性，否则图片不显示
            mUserNameText.setCompoundDrawables(null, drawable, null, null);  // 图片设置在文字顶部
            mUserNameText.setText(name);  // 显示姓名
        } else {
            mUserNameText.setCompoundDrawables(null, null, null, null);  // 清空视图
            mUserNameText.setText("");  // 清空文字
        }
        LayoutInflater inflater = LayoutInflater.from(this);  // 获取布局填充器
        String[] phoneTypes = getResources().getStringArray(R.array.phoneType);  // 从资源文件读取电话类型
        List<ContactInfo> phones = contact.getPhones();  // 获取联系人的电话列表
        mPhoneLayout.removeAllViews();  // 添加布局前先移除所有布局
        if (phones != null && phones.size() > 0) {
            for (int i = 0; i < phones.size(); i++) {  // 遍历电话列表
                View view = inflater.inflate(R.layout.item_text_phone, new LinearLayout(this), false);
                TextView type = (TextView) view.findViewById(R.id.id_user_phone_type);  // 电话的类型
                TextView data = (TextView) view.findViewById(R.id.id_user_phone);  // 电话
                type.setText(phoneTypes[phones.get(i).getDescription()]);
                data.setText(phones.get(i).getData());
                mPhoneLayout.addView(view);
            }
        } else { // 电话列表为空时，添加空提示布局
            View emptyPhone = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mPhoneLayout.addView(emptyPhone);
        }
        String[] emailTypes = getResources().getStringArray(R.array.emailType);  // 从资源文件读取邮件类型
        List<ContactInfo> emails = contact.getEmails();  // 获取联系人的邮件列表
        mEmailLayout.removeAllViews();  // 添加布局前先移除所有布局
        if (emails != null && emails.size() > 0) {
            for (int i = 0; i < emails.size(); i++) {  // 遍历邮件列表
                View view = inflater.inflate(R.layout.item_text_email, new LinearLayout(this), false);
                TextView type = (TextView) view.findViewById(R.id.id_user_email_type);  // 邮件的类型
                TextView data = (TextView) view.findViewById(R.id.id_user_email);  // 邮件
                type.setText(emailTypes[emails.get(i).getDescription()]);
                data.setText(emails.get(i).getData());
                mEmailLayout.addView(view);
            }
        } else {  // 邮件列表为空时，添加空提示布局
            View emptyEmail = inflater.inflate(R.layout.empty_view, new LinearLayout(this), false);  // 数据为空时显示的布局
            mEmailLayout.addView(emptyEmail);
        }
    }
}
