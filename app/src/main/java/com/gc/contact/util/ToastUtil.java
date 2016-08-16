package com.gc.contact.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;

    public static void show(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

}
