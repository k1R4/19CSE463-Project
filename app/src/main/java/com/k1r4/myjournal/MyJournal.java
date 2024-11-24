package com.k1r4.myjournal;

import android.app.Application;
import android.content.Context;

import java.util.List;

public class MyJournal extends Application {

    private static Context context;

    public static JournalAdapter journalAdapter;

    public static List<JournalEntry> journalEntries;

    public void onCreate() {
        super.onCreate();
        MyJournal.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyJournal.context;
    }
}