package com.k1r4.myjournal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private Button saveButton;

    private JournalEntry je;

    private JournalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        db = JournalDatabase.getInstance(MyJournal.getAppContext());
        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        saveButton = findViewById(R.id.buttonSave);

        int entryId = getIntent().getIntExtra("entryId", -1);

        je = null;
        if (entryId != -1) {
            db.journalDao().getById(entryId).observe(this, entry -> {
                if (entry != null) {
                    je = entry;
                    titleEditText.setText(entry.getTitle());
                    descriptionEditText.setText(entry.getContent());
                }
            });
        }

        saveButton.setOnClickListener(v -> {

            synchronized (MyJournal.journalEntries) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                Intent resultIntent = new Intent();

                if (je == null) {
                    String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    je = new JournalEntry(title, description, timestamp);
                    MyJournal.journalEntries.add(0, je);
                    MyJournal.journalAdapter.notifyItemInserted(0);
                    setResult(RESULT_OK, resultIntent);

                    new Thread(() -> {
                        db.journalDao().insert(je);
                        runOnUiThread(() -> finish());
                    }).start();

                } else {
                    int index = -1;
                    for(int i = 0; i < MyJournal.journalEntries.size(); i++) {
                        if (MyJournal.journalEntries.get(i).getId() == je.getId()) {
                            index = i;
                            break;
                        }
                    }

                    if(index == -1) {
                        setResult(RESULT_CANCELED, resultIntent);
                        finish();
                    }

                    je.setTitle(title);
                    je.setContent(description);
                    MyJournal.journalEntries.set(index, je);
                    MyJournal.journalAdapter.notifyItemChanged(index);
                    setResult(RESULT_OK, resultIntent);

                    new Thread(() -> {
                        db.journalDao().update(je);
                        runOnUiThread(() -> finish());
                    }).start();
                }
            }
        });
    }
}

