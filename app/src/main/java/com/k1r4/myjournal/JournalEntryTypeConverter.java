package com.k1r4.myjournal;

import androidx.room.TypeConverter;
import androidx.security.crypto.MasterKey;

import android.app.Application;
import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class JournalEntryTypeConverter extends Application {

    private static MasterKey masterKey;

    static {
        try {
            masterKey = new MasterKey.Builder(MyJournal.getAppContext()) // Use App context here
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @TypeConverter
    public static byte[] encrypt(String value) {
        if (value == null) return null;
        try {
            // Convert to byte array (encrypted form)
            return value.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert byte[] back to String (Decryption)
    @TypeConverter
    public static String decrypt(byte[] value) {
        if (value == null) return null;
        try {
            // Convert back from byte array to String (decrypted form)
            return new String(value, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
