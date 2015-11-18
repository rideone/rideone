package com.walmartlabs.classwork.rideone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.ParseUtil;

import java.util.Arrays;

import static com.walmartlabs.classwork.rideone.models.User.COLUMN_LOGIN_USER_ID;
import static com.walmartlabs.classwork.rideone.util.Utils.isPasswordValid;

public class RegisterUserActivity extends AppCompatActivity {
    private static final String PASSWORD_TEXT = "PASSWORD_TEXT";
    private EditText edUserName;
    private EditText edPassword;
    private EditText edPasswordConfirm;
    private EditText edEmail;
    private EditText edFirstName;
    private EditText edLastName;

    private boolean update = false;
    private User currentUser = null;
    private ParseUser currentLoginUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        edUserName = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edPasswordConfirm = (EditText) findViewById(R.id.edPasswordConfirm);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edFirstName = (EditText) findViewById(R.id.edFirstName);
        edLastName = (EditText) findViewById(R.id.edLastName);

        update = getIntent().getBooleanExtra("update", false);
        Button btn = (Button) findViewById(R.id.btnRegister);
        if (update) {
            btn.setText("Update");
            ParseUser loginUser = ParseUser.getCurrentUser();
            edUserName.setText(loginUser.getUsername());
            edEmail.setText(loginUser.getEmail());
            currentLoginUser = loginUser;

            ParseQuery<User> query = ParseQuery.getQuery(User.class);
            final String loginUserId = loginUser.getObjectId();
            query.whereEqualTo(COLUMN_LOGIN_USER_ID, loginUserId);
            query.getFirstInBackground(new GetCallback<User>() {
                @Override
                public void done(User user, ParseException e) {
                    currentUser = user;
                    edFirstName.setText(currentUser.getFirstName());
                    edLastName.setText(currentUser.getLastName());
                    edPassword.setText(PASSWORD_TEXT);
                    edPasswordConfirm.setText(PASSWORD_TEXT);
                    edUserName.setEnabled(false);
                }
            });
        } else {
            btn.setText(R.string.register);
            edUserName.setEnabled(true);
        }
    }

    public void registerUser(View view) {
        String userName = edUserName.getText().toString();
        String password = edPassword.getText().toString();
        String confirmPwd = edPasswordConfirm.getText().toString();
        String email = edEmail.getText().toString();
        String firstName = edFirstName.getText().toString();
        String lastName = edLastName.getText().toString();

        edUserName.setError(null);
        edPassword.setError(null);
        edPasswordConfirm.setError(null);
        edEmail.setError(null);
        boolean cancel = false;
        View focusView = null;

        if (userName == null || userName.length() < 5) {
            edUserName.setError("User name is required and has to be 5 letters or more");
            cancel = true;
            focusView = edUserName;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            edPassword.setError(getString(R.string.error_invalid_password));
            focusView = edPassword;
            cancel = true;
        }

        if (!password.equals(confirmPwd)) {
            edPasswordConfirm.setError("Password does not match");
            focusView = edPasswordConfirm;
            cancel = true;
        }

        if (!cancel) {
            // Create the ParseUser
            User user = null;
            ParseUser loginUser = null;
            if (currentUser == null) {
                user = new User();
                loginUser = new ParseUser();
            } else {
                user = currentUser;
                loginUser = currentLoginUser;
            }

            // Set core properties
            loginUser.setUsername(userName);
            if (!password.equals(PASSWORD_TEXT))
                loginUser.setPassword(password);
            loginUser.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setStatus(User.Status.NO_RIDE);
            if (update) {
                ParseUtil.saveInBatch(Arrays.asList(user, loginUser), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e(RegisterUserActivity.class.getSimpleName(), "Failed to update user", e);
                            Toast.makeText(RegisterUserActivity.this, "Network error", Toast.LENGTH_LONG).show();
                            return;
                        }

                        finish();
                    }
                });
            } else {
                // Invoke signUpInBackground
                final User userForSave = user;
                final ParseUser loginUserForSave = loginUser;
                loginUserForSave.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            userForSave.setLoginUserId(loginUserForSave.getObjectId());
                            userForSave.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e != null) {
                                        Log.e(RegisterUserActivity.class.getSimpleName(), "Failed to save user", e);
                                        Toast.makeText(RegisterUserActivity.this, "Network error", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    finish();
                                }
                            });

                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            e.printStackTrace();
                            edUserName.setError(e.getLocalizedMessage());
                            edUserName.requestFocus();
                        }
                    }
                });
            }
        } else {
            focusView.requestFocus();
        }
    }
}
