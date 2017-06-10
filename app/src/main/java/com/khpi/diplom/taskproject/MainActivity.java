package com.khpi.diplom.taskproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private TaskAdapter taskAdapter = new TaskAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
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

        taskList.setAdapter(taskAdapter);
        taskAdapter.setClickCallback(new ItemChooser<Task>() {
            @Override
            public void choose(Task item) {
                onTaskClick(item);
            }
        });
        loadList();
    }

    private void onTaskClick(Task item) {
        Intent taskDetail = TaskDetailActivity.getStartIntent(this, item);
        startActivity(taskDetail);
    }

    private void loadList() {
        showProgress();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child(Constants.REF_TASK).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Task> tasks = new ArrayList<>(20);
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : children) {
                    Task task = snapshot.getValue(Task.class);
                    tasks.add(task);
                }
                taskAdapter.addData(tasks);
                hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                Toast.makeText(MainActivity.this, "Canceled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
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
