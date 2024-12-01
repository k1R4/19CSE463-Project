package com.k1r4.myjournal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private MainActivity main;
    Activity act;

    public JournalAdapter(MainActivity main) {
        this.main = main;
    }

    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public synchronized void onBindViewHolder(JournalViewHolder holder, int position) {
        JournalEntry entry = MyJournal.journalEntries.get(position);
        holder.title.setText(entry.getTitle());
        String content = entry.getContent();
        holder.content.setText(content);
        Context context = holder.itemView.getContext();
        holder.timestamp.setText(entry.getTimestamp());

        holder.content.post(() -> {
            if (holder.content.getLineCount() > 6) {
                int lineEndIndex = holder.content.getLayout().getLineEnd(5);
                String truncatedContent = content.substring(0, lineEndIndex) + "...";
                holder.content.setText(truncatedContent);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewEntryActivity.class);
            intent.putExtra("entryId", entry.getId());
            main.startActivityForResult(intent, 200);
        });
    }

    @Override
    public synchronized int getItemCount() {
        return MyJournal.journalEntries.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, timestamp;

        public JournalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.journalTitle);
            content = itemView.findViewById(R.id.journalContent);
            timestamp = itemView.findViewById(R.id.journalTimestamp);
        }
    }
}

