package com.example.chat3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class NameActivity extends AppCompatActivity implements View.OnClickListener {
    private Button saveName;
    private EditText name;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(getString(R.string.title_set_name));
        mFirebaseAuth = FirebaseAuth.getInstance();

        saveName = (Button) findViewById(R.id.save_name_btn);
        saveName.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
    }

    @Override
    public void onClick(View v) {
        final String nameUser = name.getText().toString().trim();
        if (!nameUser.equals("")) {
            UserProfileChangeRequest profUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameUser).build();
            final FirebaseUser user = mFirebaseAuth.getCurrentUser();
            user.updateProfile(profUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent();
                    if (task.isSuccessful()) {
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_CANCELED, intent);
                    }
                    finish();
                }
            });
        }
    }
}

