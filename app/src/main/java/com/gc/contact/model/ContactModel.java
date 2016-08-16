package com.gc.contact.model;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.TextUtils;

import com.gc.contact.entity.Contact;
import com.gc.contact.entity.Contact.Builder;
import com.gc.contact.entity.ContactInfo;
import com.gc.contact.util.CharacterParser;

import java.util.ArrayList;
import java.util.List;

public class ContactModel {

    private static CharacterParser characterParser;

    static {
        characterParser = CharacterParser.getInstance();
    }

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
                Phone.CONTACT_ID + " = " + contactID,
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
            Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
            rawContactId = ContentUris.parseId(rawContactUri);
            // 插入名字
            if (!TextUtils.isEmpty(name)) {
                values.clear();
                values.put(Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                values.put(StructuredName.GIVEN_NAME, name);
                resolver.insert(Data.CONTENT_URI, values);
            }
            // 插入电话
            for (ContactInfo info : phones) {
                String data = info.getData();
                if (!TextUtils.isEmpty(data)) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    values.put(Phone.NUMBER, data);
                    values.put(Phone.TYPE, info.getDescription());
                    resolver.insert(Data.CONTENT_URI, values);
                }
            }
            // 插入电子邮件
            for (ContactInfo info : emails) {
                String data = info.getData();
                if (!TextUtils.isEmpty(data)) {
                    values.clear();
                    values.put(Data.RAW_CONTACT_ID, rawContactId);
                    values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
                    values.put(Email.DATA, data);
                    values.put(Email.TYPE, info.getDescription());
                    resolver.insert(Data.CONTENT_URI, values);
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return rawContactId;
    }

    /**
     * 修改联系人
     *
     * @param context 上下文
     * @param contact 待修改联系人
     * @return 修改结果，成功与否
     */
    public static boolean update(Context context, Contact contact) {

        return false;
    }

    /**
     * 根据id和姓名删除联系人
     *
     * @param context 上下文
     * @param id      联系人id
     */
    public static void delete(Context context, long id) {
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentResolver resolver = context.getContentResolver();
        //根据id删除data中的相应数据
        resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
    }

}
