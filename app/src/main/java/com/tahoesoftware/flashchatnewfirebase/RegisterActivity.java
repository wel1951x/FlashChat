package com.tahoesoftware.flashchatnewfirebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity
    {

    // Constants
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    // TODO: Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    // Firebase instance variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.register_email);
        mPasswordView = findViewById(R.id.register_password);
        mConfirmPasswordView = findViewById(R.id.register_confirm_password);
        mUsernameView = findViewById(R.id.register_username);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
            @Override
            public boolean onEditorAction(TextView textView,
                                          int id,
                                          KeyEvent keyEvent)
                {
                if (id == com.tahoesoftware.flashchatnewfirebase.R.integer.register_form_finished ||
                    id == EditorInfo.IME_NULL)
                    {
                    attemptRegistration();
                    return true;
                    }
                return false;
                }
            });
        // Get instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        }

    // Executed when Sign Up button is pressed.
    public void signUp(View v)
        {
        attemptRegistration();
        }

    private void attemptRegistration()
        {
        // Reset errors displayed in the form.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
/*
        // Check for a valid user name, if the user entered one.
        if (userName.isEmpty() || userName.length() < 6)
            {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
            }
*/
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password))
            {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
            {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            }
        else
        if (!isEmailValid(email))
            {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
            }

        if (cancel)
            {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            }
        else
            {
            // Call create FirebaseUser() here
            createFirebaseUser();
            }
        }

    private boolean isEmailValid(String email)
        {
        // You can add more checking logic here.
        return (email.contains("@") &
                email.contains(".") &
                (email.indexOf('@') < email.indexOf('.')) &
                !email.equals("a@b.com") &
                !email.equals("x@y.com"));
        }

    private boolean isPasswordValid(String password)
        {
        // check for a valid password (minimum 8 characters)
        String confirmPassword = mConfirmPasswordView.getText().toString();
        return confirmPassword.equals(password) & password.length() > 7;
        }

    // Create a Firebase user
    private void createFirebaseUser()
        {
        // Store values at the time of the create user attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,
                                             password).
                addOnCompleteListener(this,
                                      new OnCompleteListener<AuthResult>()
                                          {
                                          @Override
                                          public void onComplete(@NonNull Task<AuthResult> task)
                                              {
                                              if (task.isSuccessful())
                                                  {
                                                  Log.d("FlashChat",
                                                        "successfully created user");
                                                  showRegistrationDialog("Success",
                                                                         email + " user created");
                                                  saveDisplayName();
                                                  finish();
                                                  }
                                              else
                                                  {
                                                  Log.d("FlashChat",
                                                        "create user failed");
                                                  showRegistrationDialog("Failure",
                                                                         task.getException().getMessage());
                                                  }
                                              }
                                          });
        }

    // Save the display name to Shared Preferences
    private void saveDisplayName()
        {
        String displayName = mUsernameView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,
                                                       MODE_PRIVATE);
        prefs.edit().putString(DISPLAY_NAME_KEY,
                               displayName)
                               .apply();
        }

    // Create an alert dialog to show in case registration failed
    private void showRegistrationDialog(String type,
                                        String message)
        {
        new AlertDialog.Builder(this).
                setTitle("Registration " + type).
                setMessage(message).
                setPositiveButton(android.R.string.ok,
                                  null).
                setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }

    }
