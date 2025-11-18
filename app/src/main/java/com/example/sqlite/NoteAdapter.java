package com.example.sqlite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long id);
    }

    public NoteAdapter(List<Note> noteList, OnItemClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteId.setText("ID: " + note.getId());
        holder.noteTitle.setText("Tytuł: " + (note.getTitle() == null ? "(Bez tytułu)" : note.getTitle()));
        holder.noteContent.setText("Treść: " + note.getContent());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(note.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteId, noteTitle, noteContent;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteId = itemView.findViewById(R.id.noteId);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
        }
    }
}