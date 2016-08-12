package com.gc.contact.util;

import com.gc.contact.entity.Contact;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Contact> {

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
}
