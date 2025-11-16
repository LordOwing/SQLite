package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText noteInput, noteTitleInput, deleteIdInput;
    private EditText updateIdInput, updateNoteInput, updateTitleInput;
    private Button saveButton, deleteButton, updateButton;
    private TextView notesDisplay;

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
        updateNoteInput = findViewById(R.id.updateNoteInput);
        updateTitleInput = findViewById(R.id.updateTitleInput);
        updateButton = findViewById(R.id.updateButton);

        notesDisplay = findViewById(R.id.notesDisplay);

        saveButton.setOnClickListener(v -> addNote());
        deleteButton.setOnClickListener(v -> deleteNote());
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

    private void deleteNote() {
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_NOTE
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                projection,
                null, null, null, null, null
        );

        StringBuilder notes = new StringBuilder();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));

            notes.append(id)
                    .append(". ")
                    .append(title == null ? "(Bez tytułu)" : title)
                    .append(":\n")
                    .append(note)
                    .append("\n\n");
        }

        cursor.close();
        db.close();

        notesDisplay.setText(notes.toString());
    }
}
