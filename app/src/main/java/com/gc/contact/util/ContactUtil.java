package com.gc.contact.util;

public class ContactUtil {

    public enum PhoneType {
        HOME_PHONE, MOBILE_PHONE, WORK_PHONE, FAX, UNKNOWN;

        @Override
        public String toString() {
            switch (this) {
                case HOME_PHONE:
                    return "家庭";
                case MOBILE_PHONE:
                    return "手机";
                case WORK_PHONE:
                    return "单位";
                case FAX:
                    return "电子邮件";
                case UNKNOWN:
                    return "未知类型";
            }
            return null;
        }
    }

    public static String getPhoneType(int type) {
        switch (type) {
            case 1:
                return PhoneType.HOME_PHONE.toString();
            case 2:
                return PhoneType.MOBILE_PHONE.toString();
            case 3:
                return PhoneType.WORK_PHONE.toString();
            case 4:
                return PhoneType.FAX.toString();
        }
        return PhoneType.UNKNOWN.toString();
    }
}
