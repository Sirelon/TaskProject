package com.khpi.diplom.taskproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailActivity extends AppCompatActivity {

    private static final String ARG_TASK = ".task";

    public static Intent getStartIntent(Context context, Task item) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(ARG_TASK, item);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = getLayoutInflater().inflate(R.layout.activity_task_detail, null);
        setContentView(root);

        TaskViewBinder binder = new TaskViewBinder(root);

        Task task = getIntent().getParcelableExtra(ARG_TASK);
        binder.bind(task);

        initByCreator(task);
        initByReporter(task);
    }

    private void onCreatorLoaded(User reporterUser) {

    }

    private void onReportedUserLoad(User reporterUser) {

    }

    private void initByReporter(Task task) {
        final TextView reporterView = (TextView) findViewById(R.id.task_reportedUser);
        loadUser(task.getResponsibleUserId(), reporterView, new CallableArg<User>() {
            @Override
            public void call(User item) {
                onReportedUserLoad(item);
            }
        });
    }

    private void initByCreator(Task task) {
        final TextView creatorView = (TextView) findViewById(R.id.task_creadedUser);
        loadUser(task.getCreatorId(), creatorView, new CallableArg<User>() {
            @Override
            public void call(User item) {
                onCreatorLoaded(item);
            }
        });
    }

    private void loadUser(String userId, final TextView userView, final CallableArg<User> callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference = reference.child(Constants.REF_USER).child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    User reporterUser = dataSnapshot.getValue(User.class);
                    if (null != reporterUser) {
                        userView.setError(null);
                        userView.setText(reporterUser.getName());
                        callback.call(reporterUser);
                    } else {
                        onCancelled(DatabaseError.fromException(new NullPointerException("User is empty")));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                userView.setError("Cannot load user.");
                userView.setText("Cannot load user.");
            }
        });
    }
}
