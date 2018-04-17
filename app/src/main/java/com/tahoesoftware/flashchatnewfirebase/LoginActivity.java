package com.tahoesoftware.flashchatnewfirebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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


public class LoginActivity extends AppCompatActivity
    {

    // Member variables:
    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
            @Override
            public boolean onEditorAction(TextView textView,
                                          int id,
                                          KeyEvent keyEvent)
                {
                if (id == com.tahoesoftware.flashchatnewfirebase.R.integer.login || id == EditorInfo.IME_NULL)
                    {
                    attemptLogin();
                    return true;
                    }
                return false;
                }
            });

        // Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)
        {
        // Call attemptLogin() here
        attemptLogin();
        }

    // Executed when Register button pressed
    public void registerNewUser(View v)
        {
        Intent intent = new Intent(this,
                                   RegisterActivity.class);
//        finish();
        startActivity(intent);
        }

    // attemptLogin() method
    private void attemptLogin()
        {
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.isEmpty() || password.isEmpty())
            return;

        Toast.makeText(this,
                       "Login in progress...",
                       Toast.LENGTH_LONG).show();

        // Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email,
              password).addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>()
                            {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                boolean taskSuccess = task.isSuccessful();
                                Log.d("FlashChat",
                                      "signInWithEmailAndPassword" + taskSuccess);
                                if (!taskSuccess)
                                    {
                                    String failureMsg = task.getException().getMessage();
                                    Log.d("FlashChat",
                                          "SignIn error: " + failureMsg);
                                    showLoginErrorDialog(failureMsg);
                                    }
                                else
                                    {
                                    Intent chatIntent = new Intent(LoginActivity.this,
                                                                   MainChatActivity.class);
                                    chatIntent.putExtra("userName",
                                                        email.substring(0,
                                                                        email.indexOf('@')));
                                    finish();
                                    startActivity(chatIntent);
                                    }
                                }
                            });
        }

    // Show error on screen with an alert dialog
    private void showLoginErrorDialog(String message)
        {
        new AlertDialog.Builder(this).
                setTitle("Login Failure ").
                setMessage(message).
                setPositiveButton(android.R.string.ok,
                                  null).
                setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }

    }
