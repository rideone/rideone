package com.walmartlabs.classwork.rideone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.User;

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
            currentUser = (User) ParseUser.getCurrentUser();
            edUserName.setText(currentUser.getUsername());
            edEmail.setText(currentUser.getEmail());
            edFirstName.setText(currentUser.getFirstName());
            edLastName.setText(currentUser.getLastName());
            edPassword.setText(PASSWORD_TEXT);
            edPasswordConfirm.setText(PASSWORD_TEXT);
            edUserName.setEnabled(false);
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
            if (currentUser == null)
                user = new User();
            else
                user = currentUser;
            // Set core properties
            user.setUsername(userName);
            if (!password.equals(PASSWORD_TEXT))
                user.setPassword(password);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setStatus(User.Status.NO_RIDE);
            if (update) {
                try {
                    user.save();
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                    edUserName.setError(e.getLocalizedMessage());
                    edUserName.requestFocus();
                }
            } else {
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            finish();
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
