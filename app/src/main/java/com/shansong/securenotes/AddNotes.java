package com.shansong.securenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.shansong.securenotes.data.SecureNote;
import com.shansong.securenotes.database.DatabaseHelper;
import com.shansong.securenotes.utils.APPEnv;

import static com.shansong.securenotes.R.id.prg_saving;

public class AddNotes extends AppCompatActivity {

    private static final String TAG = AddNotes.class.getName();

    private ProgressBar mPrgSaving;
    private EditText mTitleEditText;
    private EditText mContentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_add_notes);

        final DatabaseHelper mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrgSaving = (ProgressBar) findViewById(prg_saving);
        mTitleEditText = (EditText) findViewById(R.id.title_edittext);
        mContentEditText = (EditText) findViewById(R.id.content_edittext);

        FloatingActionButton saveBtn = (FloatingActionButton) findViewById(R.id.fab_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                String title = mTitleEditText.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNotes.this)
                            .setMessage("You have not entered a Note title")
                            .setCancelable(false)
                            .setPositiveButton("OK", null);
                    AlertDialog ad = builder.create();
                    ad.setTitle("Incomplete Info");
                    ad.show();
                } else {
                    mPrgSaving.setIndeterminate(true);
                    mPrgSaving.setVisibility(View.VISIBLE);
                    mTitleEditText.setEnabled(false);
                    mContentEditText.setEnabled(false);


                    final String noteTitle = mTitleEditText.getText().toString();
                    final String noteContent = mContentEditText.getText().toString();

                    if(APPEnv.DEBUG){
                        Log.d(TAG, "Add new note" );
                        Log.d(TAG, "noteTitle: "+noteTitle );
                        Log.d(TAG, "noteContent: "+noteContent );
                    }
                    //
                    final SecureNote note = new SecureNote(noteTitle, noteContent);

                    mDbHelper.createSecureNote(note);

                    Intent intent = new Intent(getApplicationContext(), NoteListActivity.class);
                    startActivity(intent);
                    finish();


                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
