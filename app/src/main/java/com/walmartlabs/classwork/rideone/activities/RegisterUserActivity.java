package com.walmartlabs.classwork.rideone.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.fragments.ProfilePhotoOptionsDialog;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.ParseUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import static com.walmartlabs.classwork.rideone.models.User.COLUMN_LOGIN_USER_ID;
import static com.walmartlabs.classwork.rideone.util.Utils.isPasswordValid;

public class RegisterUserActivity extends AppCompatActivity implements ProfilePhotoOptionsDialog.ProfilePhotoUploadListener {
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

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    public String photoFileName = "profilephoto.png";
    public final String APP_TAG = "RideOne";
    private boolean imageUploaded = false;

    public ImageView ivProfile;
    ProfilePhotoOptionsDialog dialog;

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
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        imageUploaded = false;

        update = getIntent().getBooleanExtra("update", false);
        Button btn = (Button) findViewById(R.id.btnRegister);
        if (update) {
            btn.setText(R.string.register_update);
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

                    if (currentUser.getProfileImage() != null) {
                        ParseFile profileImage = currentUser.getProfileImage();

                        byte[] bitmapData = new byte[0];
                        try {
                            bitmapData = profileImage.getData();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                        ivProfile.setImageBitmap(bitmap);
                    }
                }
            });
        } else {
            btn.setText(R.string.register_save);
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
            if(imageUploaded) {
                Bitmap bitmap = ((BitmapDrawable)ivProfile.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] image = stream.toByteArray();

                ParseFile profileImage = new ParseFile(userName + ".png", image);
                user.setProfileImage(profileImage);
            }

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

    public void showUploadOptions(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ProfilePhotoOptionsDialog dialog = ProfilePhotoOptionsDialog.newInstance();
        dialog.show(fm, "fragment_upload_options");
    }

    @Override
    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            imageUploaded = true;
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // Load the taken image into a preview
                ivProfile.setImageBitmap(takenImage);
            }
            else if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri selectedPhotoUri = data.getData();
                Bitmap selectedImage = null;
                try {
                    selectedImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedPhotoUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ivProfile.setImageBitmap(selectedImage);
            }
        } else {
            imageUploaded = false;
            //Toast.makeText(this, "Error uploading picture", Toast.LENGTH_SHORT).show();
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d("failure", "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

}
