package com.k1r4.myjournal;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {JournalEntry.class}, version = 1)
public abstract class JournalDatabase extends RoomDatabase {
    public abstract JournalDao journalDao();

    private static JournalDatabase INSTANCE;

    public static synchronized JournalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            JournalDatabase.class, "journal_database")
                    .setQueryCallback(new QueryCallback() {
                        @Override
                        public void onQuery(@NonNull String s, @NonNull List<?> list) {
                            Log.println(Log.INFO,"RoomSQL", "Executing SQL: " + s);
                        }

                        public void onError(String sqlQuery, Throwable throwable) {
                            // Log the error if there's an issue with the query
                            Log.println(Log.INFO, "RoomSQL", "Error executing SQL: " + sqlQuery);
                        }
                    }, Executors.newSingleThreadExecutor())
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}

