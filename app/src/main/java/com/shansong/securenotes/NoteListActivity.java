package com.shansong.securenotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.shansong.securenotes.adapters.NotesAdapter;
import com.shansong.securenotes.database.DatabaseHelper;
import com.shansong.securenotes.helpers.DividerItemDecoration;
import com.shansong.securenotes.models.SecureNote;
import com.shansong.securenotes.utils.APPEnv;

import java.util.ArrayList;
import java.util.List;


public class NoteListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = NoteListActivity.class.getName();

    private List<SecureNote> notesList  = new ArrayList<SecureNote>();
    private RecyclerView noteListView;
    private NotesAdapter mAdapter;

    private DatabaseHelper mDbHelper;
    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notelist);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get current user
        currentUser = mDbHelper.getCurrentUserName();

        //get the secure notes belonging to this user
        notesList = mDbHelper.getSecureNotesList();
        if(APPEnv.DEBUG){

            Log.d(TAG, "Total number of notes: "+notesList.size());

            for(SecureNote note: notesList){
                Log.d(TAG, "------------------------------");
                printNoteDetails(note);
            }

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        noteListView = (RecyclerView) findViewById(R.id.lst_notes);

        mAdapter = new NotesAdapter(notesList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        noteListView.setLayoutManager(layoutManager);
        noteListView.setItemAnimator(new DefaultItemAnimator());
        noteListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        noteListView.setAdapter(mAdapter);

        noteListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), noteListView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                viewNoteDetails(position);
            }

            @Override
            public void onLongClick(View view, final int position) {
                deleteNote(position);

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mDbHelper.logOutUser(currentUser);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.fab_add:
                Intent intent = new Intent(getApplicationContext(), AddNotes.class);
                startActivity(intent);
                break;

            default:
                //do nothing
                break;
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private NoteListActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final NoteListActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void viewNoteDetails(final int position){

        if(APPEnv.DEBUG){
            Log.d(TAG, "View note at position: "+ position);
        }
        SecureNote note = notesList.get(position);

        if(APPEnv.DEBUG){
            printNoteDetails(note);
        }

        Intent intent = new Intent(getApplicationContext(), ViewNote.class);

        intent.putExtra("Note", note.getId());
        startActivity(intent);
    }


    private void deleteNote(final int position){

        if(APPEnv.DEBUG){
            Log.d(TAG, "Delete note at position: "+ position);
        }

        final int noteId = notesList.get(position).getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteListActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete ? ");
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDbHelper.deleteSecureNote(noteId);
                notesList.remove(position);

                mAdapter.notifyDataSetChanged();
            }});
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void printNoteDetails(final SecureNote note){
        if(note == null){
            Log.d(TAG, "Secure note is null");
        }else{
            Log.d(TAG, "note.id: "+note.getId());
            Log.d(TAG, "note.getTitle: "+note.getTitle());
            Log.d(TAG, "note.getContent: "+note.getContent());
            Log.d(TAG, "note.getDateTime: "+note.getDateTime());
            Log.d(TAG, "note.getUserName: "+note.getUserName());

        }
    }
}
