package com.example.chat3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    private Button createNewAccountBtn, enterToAccount, singOutBtn, verifyBtn;
    private EditText emailText, pass;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(getString(R.string.title_general));

        createNewAccountBtn = (Button) findViewById(R.id.createBtn);
        enterToAccount = (Button) findViewById(R.id.enterBtn);
        singOutBtn = (Button) findViewById(R.id.sign_out_button);
        verifyBtn = (Button) findViewById(R.id.verify_email_button);

        createNewAccountBtn.setOnClickListener(this);
        enterToAccount.setOnClickListener(this);
        singOutBtn.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);

        emailText = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        updateUI(currentUser);
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn:
                if (!validateForm()) {
                    break;
                }
                createNewAccount(emailText.getText().toString(), pass.getText().toString());
                break;
            case R.id.enterBtn:
                if (!validateForm()) {
                    break;
                }
                signInAccount(emailText.getText().toString(), pass.getText().toString());
                break;
            case R.id.verify_email_button:
                sendEmailVerification();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            default:
                break;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String emailField = emailText.getText().toString();
        if (TextUtils.isEmpty(emailField)) {
            emailText.setError(getString(R.string.vallid_need_email));
            valid = false;
        } else {
            emailText.setError(null);
        }

        String password = pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass.setError(getString(R.string.vallid_need_pass));
            valid = false;
        } else {
            pass.setError(null);
        }
        return valid;
    }

    private void signInAccount(String emailText, String passText) {
        mFirebaseAuth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            startActivity(intent);
                        } else {
                            updateUI(null);
                            Toast.makeText(getApplicationContext(), R.string.not_allowed_data, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createNewAccount(final String emailText, final String passText) {
        mFirebaseAuth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            signInAccount(emailText, passText);
                            updateUI(user);
                        } else {
                            updateUI(null);
                            Toast.makeText(getApplicationContext(), R.string.auth_failed, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    private void sendEmailVerification() {
        findViewById(R.id.verify_email_button).setEnabled(false);
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        findViewById(R.id.verify_email_button).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.verify_send) + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    R.string.verify_not_sand,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signOut() {
        mFirebaseAuth.signOut();
        updateUI(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_to_chat:
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    startActivity(intent);
                    break;
                }
                Toast.makeText(getApplicationContext(), R.string.access_to_chat_deny, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
