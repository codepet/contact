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
import com.gc.contact.entity.PhoneNumber;
import com.gc.contact.util.CharacterParser;

import java.util.ArrayList;
import java.util.List;

public class ContactModel {

    private static final String[] PHONE_PROJECTION = new String[]{
            Phone.CONTACT_ID, Phone.DISPLAY_NAME};
    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;
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
        Cursor cursor = resolver.query(Phone.CONTENT_URI, PHONE_PROJECTION, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long contactID = cursor.getLong(CONTACT_ID_INDEX);
                String displayName = cursor.getString(DISPLAY_NAME_INDEX);
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
     * 根据id获取联系人联系方式
     *
     * @param context   上下文
     * @param contactID 联系人id
     * @return 电话列表
     */
    public static List<PhoneNumber> getPhones(Context context, long contactID) {
        List<PhoneNumber> phones = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID,
                null,
                null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
                String phoneType = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.TYPE));
                PhoneNumber number = new PhoneNumber.Builder()
                        .phoneNumber(phoneNumber)
                        .phoneType(phoneType)
                        .build();
                phones.add(number);
            }
            phoneCursor.close();
        }
        return phones;
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
        List<PhoneNumber> phoneNumbers = contact.getPhoneNumber();
        String home = phoneNumbers.get(0).getPhoneNumber();
        String phone = phoneNumbers.get(1).getPhoneNumber();
        String work = phoneNumbers.get(2).getPhoneNumber();
        String email = phoneNumbers.get(3).getPhoneNumber();
        long rawContactId;
        try {
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
            rawContactId = ContentUris.parseId(rawContactUri);

            // 插入名字
            if (!TextUtils.isEmpty(name)) {
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                values.put(StructuredName.GIVEN_NAME, name);
                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
            }

            if (!TextUtils.isEmpty(home)) {
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                values.put(Phone.NUMBER, home);
                values.put(Phone.TYPE, Phone.TYPE_HOME);
                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
            }

            if (!TextUtils.isEmpty(phone)) {
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                values.put(Phone.NUMBER, phone);
                values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
            }

            if (!TextUtils.isEmpty(work)) {
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                values.put(Phone.NUMBER, work);
                values.put(Phone.TYPE, Phone.TYPE_WORK);
                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
            }

            if (!TextUtils.isEmpty(email)) {
                values.clear();
                values.put(Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
                values.put(Email.DATA, email);
                values.put(Email.TYPE, Email.TYPE_WORK);
                resolver.insert(ContactsContract.Data.CONTENT_URI, values);
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
