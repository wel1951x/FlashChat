package com.tahoesoftware.flashchatnewfirebase;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity
    {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // Set up the display name and get the Firebase reference
        setDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = findViewById(R.id.messageInput);
        mSendButton = findViewById(R.id.sendButton);
        mChatListView = findViewById(R.id.chat_list_view);

        // Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
            @Override
            public boolean onEditorAction(TextView textView,
                                          int i,
                                          KeyEvent keyEvent)
                {
                sendMessage();
                return false;
                }
            });

        // Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View view)
                {
                sendMessage();
                }
            });

        }

    // Retrieve the display name from the Shared Preferences
    private void setDisplayName()
        {
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,
                                                       MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY,
                                       null);
        if (mDisplayName == null)
            mDisplayName = getIntent().getStringExtra("userName");
        }

    private void sendMessage()
        {
        Log.d("FlashChat",
              "I sent a msg");
        // Grab the text the user typed in and push the message to Firebase
        String msg = mInputText.getText().toString();
//        showMessageDialog(msg);
        if(!msg.isEmpty())
            {
            InstantMessage chat = new InstantMessage(msg,
                                                     mDisplayName);
            mDatabaseReference.child("messages")
                              .push()
                              .setValue(chat);
            mInputText.setText("");
            }

        }

    // Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart()
        {
        super.onStart();

        mAdapter = new ChatListAdapter(this,
                                       mDatabaseReference,
                                       mDisplayName);
        mChatListView.setAdapter(mAdapter);
        }



    @Override
    public void onStop()
        {
        super.onStop();

        // Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();
        }

    // Show error on screen with an alert dialog
    private void showMessageDialog(String message)
        {
        new AlertDialog.Builder(this).
                setTitle("Message Sent was ").
                setMessage(message).
                setPositiveButton(android.R.string.ok,
                                  null).
                setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }

    }
