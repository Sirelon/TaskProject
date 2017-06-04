package com.khpi.diplom.taskproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){
            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_newTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreationCard();
            }
        });

        RecyclerView taskList = (RecyclerView) findViewById(R.id.taskList);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setItemAnimator(new DefaultItemAnimator());
        taskList.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));

//        loadList();
    }

    private void openCreationCard() {
        Intent createIntent = new Intent(this, CreateTaskActivity.class);
        startActivity(createIntent);
    }

    private void openLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
