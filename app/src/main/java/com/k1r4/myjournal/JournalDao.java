package com.k1r4.myjournal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert
    void insert(JournalEntry journalEntry);

    @Update
    void update(JournalEntry journalEntry);

    @Delete
    void delete(JournalEntry journalEntry);

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    LiveData<JournalEntry> getById(int id);

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    LiveData<List<JournalEntry>> getAllEntries();
}

