package com.gc.contact.entity;

import java.io.Serializable;
import java.util.List;

public class Contact implements Serializable {

    private final long contactID;
    private final String displayName;
    private final List<PhoneNumber> phoneNumber;
    private final String sortLetter;

    public Contact(Builder builder) {
        contactID = builder.contactID;
        displayName = builder.displayName;
        phoneNumber = builder.phoneNumber;
        sortLetter = builder.sortLetter;
    }

    public long getContactID() {
        return contactID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<PhoneNumber> getPhoneNumber() {
        return phoneNumber;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public static class Builder {
        private long contactID;
        private String displayName;
        private List<PhoneNumber> phoneNumber;
        private String sortLetter;

        public Builder buildID(long val) {
            contactID = val;
            return this;
        }

        public Builder displayName(String val) {
            displayName = val;
            return this;
        }

        public Builder phoneNumber(List<PhoneNumber> val) {
            phoneNumber = val;
            return this;
        }

        public Builder sortLetter(String val) {
            sortLetter = val;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }
    }

}
