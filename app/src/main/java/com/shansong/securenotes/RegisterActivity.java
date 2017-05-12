package com.shansong.securenotes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.shansong.securenotes.models.UserInfo;
import com.shansong.securenotes.database.DatabaseHelper;
import com.shansong.securenotes.utils.APPEnv;

/**
 * A login screen that offers registration.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserNameView;
    private EditText mPWView;
    private EditText mConfirmPWView;
    private View mProgressView;
    private View mRegisterFormView;

    private DatabaseHelper mDbHelper;

    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());

        setupActionBar();
        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.username);

        mPWView = (EditText) findViewById(R.id.password);

        mConfirmPWView = (EditText) findViewById(R.id.confirmPassword);

        Button mRegisterBtn = (Button) findViewById(R.id.register_button);
        mRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors the errors are presented and no actual register attempt is made.
     */
    private void register() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPWView.setError(null);
        mConfirmPWView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPWView.getText().toString();
        String confirmPW = mConfirmPWView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPWView.setError(getString(R.string.error_invalid_password));
            focusView = mPWView;
            cancel = true;
        }else if (!isPasswordValid(confirmPW)) {
            mConfirmPWView.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPWView;
            cancel = true;
        }else if(!password.equals(confirmPW)){
            mPWView.setError(getString(R.string.error_mismatch_password));
            focusView = mPWView;
            cancel = true;
        }

        // Check for a valid username .
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.

            showProgress(true);
            mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        if(!TextUtils.isEmpty(password) && password.length() >= 6){
            return true;
        }else{
            return false;
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;

        UserLoginTask(String userName, String password) {
            this.mUserName = userName;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            user = new UserInfo(mUserName, mPassword,false);

            if(APPEnv.DEBUG){
                Log.e(TAG, "Register user:"+ mUserName + " password: "+mPassword);
            }
            final long registerResult = mDbHelper.createUser(user);
            if(registerResult == -1){// error returned if the db returns -1
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(getApplicationContext(), NoteListActivity.class);
                startActivity(intent);
                finish();
            }else{
                if(APPEnv.DEBUG){
                    Log.e(TAG, "Error registering user!");
                }

                mUserNameView.setError(getString(R.string.error_field_required));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

