package com.gemalto.mfs.contentprovidertestapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getName();
    private static final String AUTHORITY = "com.shansong.securenotes.provider.TitleListProvider";
    private static final String BASE_PATH = "/titlelist";
    static final String URL = "content://" + AUTHORITY + BASE_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    private static final String KEY_TITLE = "title";

    private RecyclerView noteListView;
    private NotesAdapter titleListAdapter;
    private EditText usernameEditView;
    private EditText passwordEditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditView = (EditText)findViewById(R.id.username_editText);
        passwordEditView = (EditText)findViewById(R.id.password_editText);

        noteListView = (RecyclerView) findViewById(R.id.lst_notes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        noteListView.setLayoutManager(layoutManager);
        noteListView.setItemAnimator(new DefaultItemAnimator());
        noteListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Button refreshBtn = (Button) findViewById(R.id.retrieve_button);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = usernameEditView.getText().toString();
                final String password = passwordEditView.getText().toString();


                if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && password.length()>=6){
                    retrieveTitleList(userName, password);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Invalid length for the username or password");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            usernameEditView.requestFocus();

                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }


            }
        });
    }

    private void retrieveTitleList(final String userName, final String password){

        String[] selectionArgs = new String[]{userName, password};
        Cursor mCursor = getContentResolver().query(CONTENT_URI, null, null, selectionArgs, null, null);

        List<String> titles = new ArrayList<>();
        if(mCursor !=null && mCursor.getCount()> 0){

            if (mCursor.moveToFirst()) {
                do {
                    String title = mCursor.getString(mCursor.getColumnIndex(KEY_TITLE));
                    Log.d(TAG, "TITLE: "+title);
                    titles.add(title);
                } while (mCursor.moveToNext());

            }
            mCursor.close();

        }else{
            Toast.makeText(MainActivity.this, "Empty Title List ", Toast.LENGTH_LONG).show();
        }

        titleListAdapter = new NotesAdapter(titles);
        noteListView.setAdapter(titleListAdapter);
        titleListAdapter.notifyDataSetChanged();

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
