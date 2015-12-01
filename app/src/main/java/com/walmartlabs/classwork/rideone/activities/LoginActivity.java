package com.walmartlabs.classwork.rideone.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.ParseUtil;
import com.walmartlabs.classwork.rideone.util.Utils;

import static com.walmartlabs.classwork.rideone.models.User.COLUMN_LOGIN_USER_ID;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText edUserName;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the login form.
        edUserName = (EditText) findViewById(R.id.edUserName);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(textView);
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void loginSuccess(ParseUser loginUser) {
        clearErrors();
        LoginActivity.login(loginUser, this, null);
    }

    public static void login(ParseUser loginUser, final Context context, final GetCallback<User> callback) {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        final String loginUserId = loginUser.getObjectId();
        query.whereEqualTo(COLUMN_LOGIN_USER_ID, loginUserId);
        query.getFirstInBackground(new GetCallback<User>() {
            @Override
            public void done(User user, ParseException e) {
                if (e != null) {
                    Log.e(LoginActivity.class.getSimpleName(), "Failed to get user for loginUserId " + loginUserId, e);
                    Toast.makeText(context, "User credentials error", Toast.LENGTH_SHORT).show();
                    if(callback != null) {
                        callback.done(user, e);
                    }
                    return;
                }

                Intent i = new Intent(context, HomeActivity.class);
                user.flush();
                i.putExtra("user", user);

                if(callback != null) {
                    callback.done(user, e);
                }

                context.startActivity(i);
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        if (mAuthTask != null) {
            return;
        }
        clearErrors();


        // Store values at the time of the login attempt.
        String userName = edUserName.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName address.
        if (TextUtils.isEmpty(userName)) {
            edUserName.setError(getString(R.string.error_field_required));
            focusView = edUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void clearErrors() {
        // Reset errors.
        edUserName.setError(null);
        mPasswordView.setError(null);
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
            int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(duration).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(duration).alpha(
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, ParseUser> {

        private final String userName;
        private final String password;

        UserLoginTask(String email, String password) {
            userName = email;
            this.password = password;
        }

        @Override
        protected ParseUser doInBackground(Void... params) {
            ParseUser user = null;
            try {
                user = ParseUser.logIn(userName, password);
            } catch (ParseException e) {
                e.printStackTrace();
                user = null;
            }
            return user;
        }

        @Override
        protected void onPostExecute(final ParseUser successUser) {
            mAuthTask = null;

            if (successUser != null) {
//                Toast.makeText(LoginActivity.this, "Login succesful : " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
                loginSuccess(successUser);
            } else {
                showProgress(false);
                edUserName.setError(getString(R.string.error_incorrect_password));
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                edUserName.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
//            Intent upIntent = NavUtils.getParentActivityIntent(this);
//            upIntent.putExtra("user", driver.flush());
//            NavUtils.navigateUpTo(this, upIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}

