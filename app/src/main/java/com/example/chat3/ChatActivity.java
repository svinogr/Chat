package com.example.chat3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GET_NAME_CODE = 1;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    Query refMessages;
    private Button sendText;
    private EditText textForSend;
    private ListView listChatView;
    private FirebaseListAdapter<Message> mFirebaseListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_chat));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        refMessages = mFirebaseDatabase.getReference().child("message").limitToLast(20);

        sendText = (Button) findViewById(R.id.btn_send_message);
        sendText.setOnClickListener(this);

        textForSend = (EditText) findViewById(R.id.edit_text_chat);
        listChatView = (ListView) findViewById(R.id.list_item_chat);

        mFirebaseListAdapter = new FirebaseListAdapter<Message>
                (this, Message.class, R.layout.item_chat, refMessages) {
            @Override
            protected void populateView(View v, Message message, int position) {
                TextView messageUser = (TextView) v.findViewById(R.id.user_message);
                TextView userName = (TextView) v.findViewById(R.id.user_nick_name);
                messageUser.setText(message.getBodyMessage());
                userName.setText(message.getName());
            }
        };
        listChatView.setAdapter(mFirebaseListAdapter);
        if (mFirebaseAuth.getCurrentUser().getDisplayName() == null) {
            setName();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createUserOnlineDB();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteUserOnlineFromDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                startActivity(NavUtils.getParentActivityIntent(this));
                break;
            case R.id.btn_send_message:
                String bodyMessage = textForSend.getText().toString().trim();
                if (!bodyMessage.equals("")) {
                    Message message = new Message();
                    message.setBodyMessage(bodyMessage);
                    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        if (currentUser.getDisplayName() == null) {
                            setName();
                            return;
                        }
                        message.setName(mFirebaseAuth.getCurrentUser().getDisplayName());
                    }
                    mFirebaseDatabase.getReference().child("message").push().setValue(message);
                    textForSend.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_online:
                Intent intent = new Intent(this, OnlineActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == -1) {
                createUserOnlineDB();
            }
        }
    }

    void setName() {
        Intent intent = new Intent(this, NameActivity.class);
        startActivityForResult(intent, GET_NAME_CODE);
    }

    void createUserOnlineDB() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user.getDisplayName() != null) {
            mFirebaseDatabase.getReference().child("user/" + user.getUid()).setValue(new User(user.getDisplayName()));
        }
    }

    void deleteUserOnlineFromDB() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user.getDisplayName() != null) {
            mFirebaseDatabase.getReference().child("user/" + mFirebaseAuth.getCurrentUser().getUid()).removeValue();
        }
    }
}
