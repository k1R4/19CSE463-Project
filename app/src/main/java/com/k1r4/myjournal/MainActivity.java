package com.k1r4.myjournal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.color.DynamicColors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.concurrent.Executor;
//import com.k1r4.myjournal.EntryActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // private JournalAdapter journalAdapter;
    private FloatingActionButton fabAddEntry;
    private JournalAdapter adapter;
    private JournalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);

        // Initialize BiometricPrompt
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                loadEntries(); // Load entries after authentication
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Authentication failed: " + errString, Toast.LENGTH_SHORT).show();
                finish(); // Exit app if authentication fails
            }
        });

        // Configure BiometricPrompt
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock MyJournal")
                .setSubtitle("Authenticate with your biometrics")
                .setNegativeButtonText("Cancel")
                .build();

        // Prompt the user
        biometricPrompt.authenticate(promptInfo);
        setContentView(R.layout.activity_main);
    }

    private void loadEntries() {

        db = JournalDatabase.getInstance(this.getApplicationContext());
        fabAddEntry = findViewById(R.id.fabAddEntry);
        recyclerView = findViewById(R.id.recyclerView);

        fabAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EntryActivity.class);
            startActivityForResult(intent, 100); // Use 100 as the request code
        });

        db.journalDao().getAllEntries().observe(this, entries -> {
            MyJournal.journalEntries = entries;
            MyJournal.journalAdapter = new JournalAdapter(this);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(MyJournal.journalAdapter);

            MyJournal.journalAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_CANCELED) return;

        super.onActivityResult(requestCode, resultCode, data);

        boolean update = data.getBooleanExtra("update", false);

        if (resultCode == RESULT_OK) {
            if(update) {
                Intent intent = new Intent(MainActivity.this, EntryActivity.class);
                intent.putExtra("entryId", data.getIntExtra("entryId", -1));
                startActivityForResult(intent, 100);
                return;
            }
            recyclerView.scrollToPosition(0);
        }
    }
}

