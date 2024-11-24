package com.k1r4.myjournal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textview.MaterialTextView;

public class ViewEntryActivity extends AppCompatActivity {

    private TextView titleView, contentView, timestampView;
    private Button editButton, deleteButton;

    private JournalEntry currentEntry;
    private JournalDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        titleView = findViewById(R.id.textViewTitle);
        contentView = findViewById(R.id.textViewContent);
        timestampView = findViewById(R.id.textViewTimestamp);
        editButton = findViewById(R.id.buttonEdit);
        deleteButton = findViewById(R.id.buttonDelete);

        // Get the journal entry ID passed via Intent
        int entryId = getIntent().getIntExtra("entryId", -1);

        // Initialize database
        database = JournalDatabase.getInstance(this);
        Intent resultIntent = new Intent();

        // Fetch the journal entry by ID from the database
        database.journalDao().getById(entryId).observe(this, entry -> {
            currentEntry = entry;
            if (currentEntry != null) {
                titleView.setText(currentEntry.getTitle());
                contentView.setText(currentEntry.getContent());
                timestampView.setText(currentEntry.getTimestamp());
            }
        });

        // Handle Edit Button click
        editButton.setOnClickListener(v -> {
            resultIntent.putExtra("entryId", entryId);
            resultIntent.putExtra("update", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Handle Delete Button click
        deleteButton.setOnClickListener(v -> {
            synchronized (MyJournal.journalEntries) {;
                MyJournal.journalEntries.remove(currentEntry);
                MyJournal.journalAdapter.notifyItemRemoved(
                        MyJournal.journalEntries.indexOf(currentEntry));

                new Thread(() -> {
                    if (currentEntry != null) {
                        database.journalDao().delete(currentEntry);
                        setResult(RESULT_OK, resultIntent);
                        runOnUiThread(() -> finish()); // Close the activity after deletion
                    }
                }).start();
            }
        });
    }
}


