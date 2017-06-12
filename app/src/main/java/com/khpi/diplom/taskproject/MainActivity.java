package com.khpi.diplom.taskproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private TaskAdapter taskAdapter = new TaskAdapter();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });

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
        taskAdapter.setClickCallback(new CallableArg<Task>() {
            @Override
            public void call(Task item) {
                onTaskClick(item);
            }
        });
        loadList();
    }

    public static void start(BaseActivity baseActivity) {
        baseActivity.hideProgress();
        Intent mainActivity = new Intent(baseActivity, MainActivity.class);
        baseActivity.startActivity(mainActivity);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        baseActivity.finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadList();
    }

    @Override
    protected void showProgress() {
//        super.showProgress();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void hideProgress() {
//        super.hideProgress();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void onTaskClick(Task item) {
        Intent taskDetail = TaskDetailActivity.getStartIntent(this, item);
        startActivity(taskDetail);
    }

    private void loadList() {
        taskAdapter.deleteAll();
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

                // Sort by creation date
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        return Long.compare(o2.getCreationDate(), o1.getCreationDate());
                    }
                });

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
        Intent createIntent = CreateOrEditTaskActivity.getIntentForCreation(this);
        startActivity(createIntent);
    }

    private void openLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
