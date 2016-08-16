package com.gc.contact.entity;

import java.io.Serializable;

public class ContactInfo implements Serializable {

    private final String data;  // 具体值
    private final String type;  // 类型（电话Phone / 邮件Email）
    private final int description;  // 描述（0...n）

    public ContactInfo(Builder builder) {
        this.data = builder.data;
        this.type = builder.type;
        this.description = builder.description;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public int getDescription() {
        return description;
    }

    public static class Builder {
        private String data; // 具体值
        private String type; // 类型（电话Phone / 邮件Email）
        private int description; // 描述（0...n）

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder description(int description) {
            this.description = description;
            return this;
        }

        public ContactInfo build() {
            return new ContactInfo(this);
        }
    }
}
