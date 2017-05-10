package com.shansong.securenotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.shansong.securenotes.R;
import com.shansong.securenotes.models.SecureNote;
import com.shansong.securenotes.database.DatabaseHelper;


public class EditNote extends AppCompatActivity implements View.OnClickListener {
    private EditText mTitleEditText;
    private EditText mContentEditText;

    private int noteId;
    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentEditText = (EditText) findViewById(R.id.content_edittext) ;
        mTitleEditText = (EditText) findViewById(R.id.title_edittext);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("Note", -1);
        if(noteId != -1) {
            SecureNote note = mDbHelper.getSecureNote(noteId);

            mTitleEditText.setText(note.getTitle());
            mContentEditText.setText(note.getContent());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton saveBtn = (FloatingActionButton) findViewById(R.id.fab_save);
        saveBtn.setOnClickListener(this);

        FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.fab_delete);
        deleteBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.fab_save:
                String newTitle = mTitleEditText.getText().toString();
                String newContent = mContentEditText.getText().toString();
                SecureNote updatedNote = new SecureNote(newTitle, newContent);
                updatedNote.setId(noteId);

                mDbHelper.updateSecureNote(updatedNote);

                Intent saveIntent = new Intent(getApplicationContext(), NoteListActivity.class);
                startActivity(saveIntent);
                finish();

                break;

            case R.id.fab_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(EditNote.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDbHelper.deleteSecureNote(noteId);

                        Intent deleteIntent = new Intent(getApplicationContext(), NoteListActivity.class);
                        startActivity(deleteIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

                break;

            default:
                //do nothing
                break;
        }
    }
}
