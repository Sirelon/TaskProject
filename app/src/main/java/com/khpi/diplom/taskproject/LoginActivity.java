package com.khpi.diplom.taskproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TaskManager";

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "user:" + user);

        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.btn_login).setOnClickListener(this);

        findViewById(R.id.btn_signup).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String email = getEmail();
        String password = getPassword();

        if (!isCorrectEmail(email)) {
            Toast.makeText(this, "Email is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isCorrectPassword(password)) {
            Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.btn_login: {
                logIn(email, password);
            }
            break;
            case R.id.btn_signup: {
                signUp(email, password);
            }
            break;
        }
    }

    private boolean isCorrectPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isCorrectEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getEmail() {
        return emailField.getText().toString();
    }

    private String getPassword() {
        return passwordField.getText().toString();
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            openMain();
                        } else {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Incorrect login or password.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            openMain();
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());

                            Toast.makeText(LoginActivity.this, "Incorrect login or password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openMain() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
