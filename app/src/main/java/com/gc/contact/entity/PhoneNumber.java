package com.gc.contact.entity;

import com.gc.contact.util.SerializeUtil;

import java.io.Serializable;

public class PhoneNumber implements Serializable{

    private final String phoneNumber;
    private final String phoneType;

    public PhoneNumber(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.phoneType = builder.phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public static class Builder {
        private String phoneNumber;
        private String phoneType;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder phoneType(String phoneType) {
            this.phoneType = phoneType;
            return this;
        }

        public PhoneNumber build() {
            return new PhoneNumber(this);
        }
    }
}
