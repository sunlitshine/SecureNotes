package com.shansong.securenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shansong.securenotes.data.SecureNote;
import com.shansong.securenotes.database.DatabaseHelper;
import com.shansong.securenotes.utils.APPEnv;

public class ViewNote extends AppCompatActivity {
    private static final String TAG = ViewNote.class.getName();

    private TextView mTitleView;
    private TextView mContentView;
    private TextView mSavedAtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        DatabaseHelper mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        final int noteId = intent.getIntExtra("Note", -1);
        if(APPEnv.DEBUG){
            Log.d(TAG, "NoteID:  "+noteId);
        }

        if(noteId != -1){
            SecureNote note = mDbHelper.getSecureNote(noteId);

            if(APPEnv.DEBUG){
                if(note == null){
                    Log.d(TAG, "Secure note is null");
                }else{
                    Log.d(TAG, "note.id: "+note.getId());
                    Log.d(TAG, "note.getTitle: "+note.getTitle());
                    Log.d(TAG, "note.getContent: "+note.getContent());
                    Log.d(TAG, "note.getDateTime: "+note.getDateTime());
                }
            }

            //
            mTitleView = (TextView) findViewById(R.id.title_textview) ;
            mContentView = (TextView) findViewById(R.id.content_textview);
            mSavedAtView = (TextView) findViewById(R.id.saved_at_textview);
            //
            FloatingActionButton eidtBtn = (FloatingActionButton) findViewById(R.id.fab_edit);
            eidtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),EditNote.class);
                    intent.putExtra("Note", noteId);
                    startActivity(intent);
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //
            mTitleView.setText(note.getTitle());
            mContentView.setText(note.getContent());
            mSavedAtView.setText(note.getDateTime());
        }
    }
}
