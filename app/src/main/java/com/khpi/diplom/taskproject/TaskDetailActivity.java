package com.khpi.diplom.taskproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TaskDetailActivity extends AppCompatActivity {

    private static final String ARG_TASK = ".task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = getLayoutInflater().inflate(R.layout.activity_task_detail, null);
        setContentView(root);

        TaskViewBinder binder = new TaskViewBinder(root);

        Task task = getIntent().getParcelableExtra(ARG_TASK);
        binder.bind(task);
    }

    public static Intent getStartIntent(Context context, Task item) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(ARG_TASK, item);
        return intent;
    }
}
