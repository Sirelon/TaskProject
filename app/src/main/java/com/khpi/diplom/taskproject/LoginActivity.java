package com.khpi.diplom.taskproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TaskManager";

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private TextInputLayout userNameInput;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "user:" + user);

        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);
        userNameInput = (TextInputLayout) findViewById(R.id.field_username);
        userNameInput.setVisibility(View.GONE);

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
                onSignUpClick(email, password);
            }
            break;
        }
    }

    private void showProgress(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideProgress(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.hide();
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
        showProgress();
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

    private void onSignUpClick(String email, String password) {
        String username = userNameInput.getEditText().getText().toString();
        if (TextUtils.isEmpty(username)) {
            // Show user name field
            userNameInput.setVisibility(View.VISIBLE);
            userNameInput.requestFocus();
            Toast.makeText(this, "Please type your name", Toast.LENGTH_SHORT).show();
        } else {
            signUp(email, password, username);
        }
    }

    private void signUp(String email, String password, final String username) {
        showProgress();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            saveUser(firebaseUser, username);
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());

                            errorDuringSignUp();
                        }
                    }
                });
    }

    private void saveUser(FirebaseUser firebaseUser, String username) {
        User user = new User(firebaseUser.getUid(), username, firebaseUser.getEmail());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("users").child(user.getUid());
        reference.setValue(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            openMain();
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());

                            errorDuringSignUp();
                        }
                    }
                });
    }

    private void errorDuringSignUp() {
        Toast.makeText(LoginActivity.this, "Incorrect login or password.",
                Toast.LENGTH_SHORT).show();
    }

    private void openMain() {
        hideProgress();
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
