/*
 * Copyright (c) 2016.  PIMA Mobile
 */

package com.pimamobile.pima.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class ToastMessage {
    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
