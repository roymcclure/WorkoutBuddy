package com.example.workoutbuddy;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

// the sole purpose of this class it to allow access to context resources from a static environment
// eg. to get table names, queries, etc from the DataHelper class
// https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/8765789

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
