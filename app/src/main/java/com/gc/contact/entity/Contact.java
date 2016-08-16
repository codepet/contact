package com.gc.contact.entity;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Contact implements Serializable, Comparator<Contact> {

    public static final long serialVersionUID = 3113005995L;
    private final long contactID;  // 联系人ID
    private final String displayName;  // 联系人姓名
    private final String sortLetter;  // 联系人姓名拼音
    private final List<ContactInfo> phones; // 联系人电话列表
    private final List<ContactInfo> emails; // 联系人邮件列表

    public Contact(Builder builder) {
        contactID = builder.contactID;
        displayName = builder.displayName;
        sortLetter = builder.sortLetter;
        phones = builder.phones;
        emails = builder.emails;
    }

    public long getContactID() {
        return contactID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public List<ContactInfo> getPhones() {
        return phones;
    }

    public List<ContactInfo> getEmails() {
        return emails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        if (contactID != contact.contactID) return false;
        return displayName.equals(contact.displayName);
    }

    @Override
    public int hashCode() {
        int result = (int) (contactID ^ (contactID >>> 32));
        result = 31 * result + displayName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * 这里主要是用来对List里面的数据根据ABCDEFG...来排序
     */
    @Override
    public int compare(Contact lhs, Contact rhs) {
        if (rhs.getSortLetter().equals("#")) {
            return -1;
        } else if (lhs.getSortLetter().equals("#")) {
            return 1;
        } else {
            return lhs.getSortLetter().compareTo(rhs.getSortLetter());
        }
    }

    public static class Builder {
        private long contactID; // 联系人ID
        private String displayName; // 联系人姓名
        private String sortLetter; // 联系人姓名拼音
        private List<ContactInfo> phones; // 联系人电话列表
        private List<ContactInfo> emails; // 联系人邮件列表

        public Builder buildID(long val) {
            this.contactID = val;
            return this;
        }

        public Builder displayName(String val) {
            this.displayName = val;
            return this;
        }

        public Builder phones(List<ContactInfo> val) {
            this.phones = val;
            return this;
        }

        public Builder emails(List<ContactInfo> val) {
            this.emails = val;
            return this;
        }

        public Builder sortLetter(String val) {
            this.sortLetter = val;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }
    }

}
