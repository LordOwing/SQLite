package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnItemClickListener {

    private DatabaseHelper dbHelper;
    private EditText noteTitleInput, noteInput, deleteIdInput, updateIdInput, updateTitleInput, updateNoteInput;
    private Button saveButton, deleteButton, updateButton;
    private RecyclerView notesRecyclerView;
    private TextView emptyView;
    private NoteAdapter adapter;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);


        noteTitleInput = findViewById(R.id.noteTitleInput);
        noteInput = findViewById(R.id.noteInput);
        saveButton = findViewById(R.id.saveButton);

        deleteIdInput = findViewById(R.id.deleteIdInput);
        deleteButton = findViewById(R.id.deleteButton);

        updateIdInput = findViewById(R.id.updateIdInput);
        updateTitleInput = findViewById(R.id.updateTitleInput);
        updateNoteInput = findViewById(R.id.updateNoteInput);
        updateButton = findViewById(R.id.updateButton);


        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        emptyView = findViewById(R.id.emptyView);


        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList, this);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);


        saveButton.setOnClickListener(v -> addNote());
        deleteButton.setOnClickListener(v -> deleteNoteById());
        updateButton.setOnClickListener(v -> updateNote());

        loadNotes();
    }

    private void addNote() {
        String titleText = noteTitleInput.getText().toString().trim();
        String noteText = noteInput.getText().toString().trim();

        if (noteText.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, titleText);
        values.put(DatabaseHelper.COLUMN_NOTE, noteText);

        db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();

        noteTitleInput.setText("");
        noteInput.setText("");
        loadNotes();
    }

    private void deleteNoteById() {
        String idText = deleteIdInput.getText().toString().trim();
        if (idText.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{idText});
        db.close();

        deleteIdInput.setText("");
        loadNotes();
    }


    private void deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();

        loadNotes();
    }

    @Override
    public void onItemClick(long id) {

        deleteNote(id);
    }

    private void updateNote() {
        String idText = updateIdInput.getText().toString().trim();
        String newTitle = updateTitleInput.getText().toString().trim();
        String newContent = updateNoteInput.getText().toString().trim();

        if (idText.isEmpty() || (newTitle.isEmpty() && newContent.isEmpty())) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!newTitle.isEmpty()) {
            values.put(DatabaseHelper.COLUMN_TITLE, newTitle);
        }
        if (!newContent.isEmpty()) {
            values.put(DatabaseHelper.COLUMN_NOTE, newContent);
        }

        db.update(DatabaseHelper.TABLE_NOTES,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{idText});

        db.close();

        updateIdInput.setText("");
        updateTitleInput.setText("");
        updateNoteInput.setText("");

        loadNotes();
    }

    private void loadNotes() {
        noteList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_NOTE
        };


        String sortOrder = DatabaseHelper.COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                projection,
                null, null, null, null, sortOrder
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));
            noteList.add(new Note(id, title, note));
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();


        if (noteList.isEmpty()) {
            notesRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            notesRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}