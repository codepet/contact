package com.gc.contact.model;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;

import com.gc.contact.entity.Contact;
import com.gc.contact.entity.Contact.Builder;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.util.CharacterParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 利用ContentResolver操作系统通讯录逻辑处理
 * <ul>
 * <li>获取所有联系人 {@link ContactModel#getPhoneContact(Context)}</li>
 * <li>根据id获取联系人电话 {@link ContactModel#getPhones(Context, long)}</li>
 * <li>根据id获取联系人邮件 {@link ContactModel#getEmails(Context, long)}</li>
 * <li>添加联系人 {@link ContactModel#insert(Context, Contact)}</li>
 * <li>修改联系人 {@link ContactModel#update(Context, Contact)}</li>
 * <li>根据id删除联系人 {@link ContactModel#delete(Context, long)}</li>
 * <ul/>
 */
public class ContactModel {

    /**
     * 通过静态方法调用，因此不需要实例化
     */
    private ContactModel() {
        throw new AssertionError();
    }

    /**
     * 获取所有联系人
     *
     * @param context 上下文
     * @return 联系人列表
     */
    public static List<Contact> getPhoneContact(Context context) {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        CharacterParser characterParser = CharacterParser.getInstance();
        Cursor cursor = resolver.query(
                Phone.CONTENT_URI,
                new String[]{Phone.CONTACT_ID, Phone.DISPLAY_NAME},
                null,
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long contactID = cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));
                String displayName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                String pinyin = characterParser.getSelling(displayName);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                Builder builder = new Builder()
                        .buildID(contactID)
                        .displayName(displayName);
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    builder.sortLetter(pinyin.toUpperCase());
                } else {
                    builder.sortLetter("#");
                }
                Contact contact = builder.build();
                contacts.add(contact);
            }
            cursor.close();
        }
        return contacts;
    }

    /**
     * 根据id获取联系人电话
     *
     * @param context   上下文
     * @param contactID 联系人id
     * @return 电话列表
     */
    public static List<ContactInfo> getPhones(Context context, long contactID) {
        List<ContactInfo> phones = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        // 查询电话号码
        Cursor cursor = resolver.query(
                Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = " + contactID,
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                int description = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
                ContactInfo number = new ContactInfo.Builder()
                        .data(phoneNumber)
                        .type(Phone.TYPE)
                        .description(description)
                        .build();
                phones.add(number);
            }
            cursor.close();
        }
        return phones;
    }

    /**
     * 根据id获取联系人邮件
     *
     * @param context   上下文
     * @param contactID 联系人id
     * @return 电话列表
     */
    public static List<ContactInfo> getEmails(Context context, long contactID) {
        List<ContactInfo> emails = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        // 查询邮箱
        Cursor cursor = resolver.query(
                Email.CONTENT_URI,
                new String[]{Email.DATA, Email.TYPE},
                Email.CONTACT_ID + " = " + contactID,
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String email = cursor.getString(cursor.getColumnIndex(Email.DATA));
                int description = cursor.getInt(cursor.getColumnIndex(Email.TYPE));
                ContactInfo number = new ContactInfo.Builder()
                        .data(email)
                        .type(Email.TYPE)
                        .description(description)
                        .build();
                emails.add(number);
            }
            cursor.close();
        }
        return emails;
    }


    /**
     * 添加联系人
     *
     * @param context 上下文
     * @param contact 待添加的联系人
     * @return 添加结果，成功与否
     */
    public static long insert(Context context, Contact contact) {
        String name = contact.getDisplayName();
        List<ContactInfo> phones = contact.getPhones();
        List<ContactInfo> emails = contact.getEmails();
        long rawContactId;
        try {
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);
            rawContactId = ContentUris.parseId(rawContactUri);
            // 插入名字
            insertName(context, name, rawContactId);
            // 插入电话
            for (ContactInfo info : phones) {
                insertPhone(context, info, rawContactId);
            }
            // 插入电子邮件
            for (ContactInfo info : emails) {
                insertEmail(context, info, rawContactId);
            }
        } catch (Exception e) {
            return -1;
        }
        return rawContactId;
    }

    /**
     * 插入姓名
     *
     * @param context      上下文
     * @param name         姓名
     * @param rawContactId 联系人id
     */
    private static void insertName(Context context, String name, long rawContactId) {
        if (!TextUtils.isEmpty(name)) {
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.GIVEN_NAME, name);
            resolver.insert(Data.CONTENT_URI, values);
        }
    }

    /**
     * 插入电话
     *
     * @param context      上下文
     * @param info         联系信息
     * @param rawContactId 联系人id
     */
    private static void insertPhone(Context context, ContactInfo info, long rawContactId) {
        String data = info.getData();
        if (!TextUtils.isEmpty(data)) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, data);
            values.put(Phone.TYPE, info.getDescription());
            context.getContentResolver().insert(Data.CONTENT_URI, values);
        }
    }

    /**
     * 插入电话
     *
     * @param context      上下文
     * @param info         联系信息
     * @param rawContactId 联系人id
     */
    private static void insertEmail(Context context, ContactInfo info, long rawContactId) {
        String data = info.getData();
        if (!TextUtils.isEmpty(data)) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
            values.put(Email.DATA, data);
            values.put(Email.TYPE, info.getDescription());
            context.getContentResolver().insert(Data.CONTENT_URI, values);
        }
    }

    /**
     * 修改联系人
     *
     * @param context 上下文
     * @param contact 待修改联系人
     */
    public static void update(Context context, Contact contact) {
        updateName(context, contact);  // 更新姓名
        updatePhone(context, contact);  // 更新电话号码
        updateEmail(context, contact);  // 更新邮箱
    }

    /**
     * 更新姓名
     *
     * @param context 上下文
     * @param contact 联系人
     */
    private static void updateName(Context context, Contact contact) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(Data.DISPLAY_NAME, contact.getDisplayName());
        context.getContentResolver().update(
                RawContacts.CONTENT_URI,
                values,
                RawContacts._ID + "=" + contact.getContactID(),
                null);
    }

    /**
     * 更新电话
     *
     * @param context 上下文
     * @param contact 联系人
     */
    private static void updatePhone(Context context, Contact contact) {
        for (ContactInfo info : contact.getPhones()) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(Phone.NUMBER, info.getData());
            values.put(Phone.TYPE, info.getDescription());
            context.getContentResolver().update(
                    Data.CONTENT_URI,
                    values,
                    Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
                    new String[]{contact.getContactID() + "", Phone.CONTENT_ITEM_TYPE});
        }
    }

    /**
     * 更新邮箱
     *
     * @param context 上下文
     * @param contact 联系人
     */
    private static void updateEmail(Context context, Contact contact) {
        for (ContactInfo info : contact.getEmails()) {
            ContentValues values = new ContentValues();
            values.clear();
            values.clear();
            values.put(Email.DATA, info.getData());
            values.put(Email.TYPE, info.getDescription());
            context.getContentResolver().update(
                    Data.CONTENT_URI,
                    values,
                    Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
                    new String[]{contact.getContactID() + "", Email.CONTENT_ITEM_TYPE});
        }
    }

    /**
     * 根据id删除联系人
     *
     * @param context 上下文
     * @param id      联系人id
     */
    public static void delete(Context context, long id) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(Data.CONTENT_URI, Data.RAW_CONTACT_ID + "=" + id, null);
    }

}
