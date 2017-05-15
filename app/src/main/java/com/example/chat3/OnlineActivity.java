package com.example.chat3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnlineActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference refUsers;
    FirebaseListAdapter<User> mFirebaseListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_online));

        ListView listUser = (ListView) findViewById(R.id.list_item_user_online);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        refUsers = mFirebaseDatabase.getReference().child("user");
        mFirebaseListAdapter = new FirebaseListAdapter<User>
                (this, User.class, R.layout.item_user_online,refUsers) {
            @Override
            protected void populateView(View v, User user, int position) {
                TextView userName;
                userName = (TextView) v.findViewById(R.id.user_online_item);
                userName.setText(user.getName());
            }
        };
        listUser.setAdapter(mFirebaseListAdapter);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
