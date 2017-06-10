package com.khpi.diplom.taskproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TaskDetailActivity extends AppCompatActivity {

    private static final String ARG_TASK = ".task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    public static Intent getStartIntent(Context context, Task item) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(ARG_TASK, item);
        return intent;
    }
}
