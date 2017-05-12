package com.gemalto.mfs.contentprovidertestapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
                retrieveTitleList(userName, password);

            }
        });
    }

    private void retrieveTitleList(final String userName, final String password){

        String[] selectionArgs = new String[]{"shanshan", "123456"};
        //String[] selectionArgs = new String[]{userName, password};
        Cursor mCursor = getContentResolver().query(CONTENT_URI, null, null, selectionArgs, null, null);


        if(mCursor !=null && mCursor.getCount()>1){
            List<String> titles = new ArrayList<>();
            if (mCursor.moveToFirst()) {
                do {
                    String title = mCursor.getString(mCursor.getColumnIndex(KEY_TITLE));
                    Log.d(TAG, "TITLE: "+title);
                    titles.add(title);
                } while (mCursor.moveToNext());

            }

            titleListAdapter = new NotesAdapter(titles);
            noteListView.setAdapter(titleListAdapter);
            titleListAdapter.notifyDataSetChanged();

            mCursor.close();
        }else{
            Toast.makeText(MainActivity.this, "Empty Title List ", Toast.LENGTH_LONG).show();
        }

    }
}
