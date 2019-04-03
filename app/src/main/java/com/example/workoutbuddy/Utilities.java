package com.example.workoutbuddy;

import android.content.Context;
import android.widget.Toast;

public class Utilities {

    public static void shortToast(Context context, String message) {
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void longToast(Context context, String message) {
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.show();
    }
}
