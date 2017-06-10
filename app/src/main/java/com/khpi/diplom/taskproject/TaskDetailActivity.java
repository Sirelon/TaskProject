package com.khpi.diplom.taskproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailActivity extends BaseActivity {

    private static final String ARG_TASK = ".task";
    private FirebaseUser currentUser;
    private FloatingActionButton actionButton;

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

        actionButton = (FloatingActionButton) findViewById(R.id.btn_action);

        actionButton.hide();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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

    private void initByCreator(Task task) {
        final TextView creatorView = (TextView) findViewById(R.id.task_creadedUser);
        String creatorId = task.getCreatorId();
        if (currentUser.getUid().equals(creatorId)) {
            creatorView.setText("Me");
            highlightText(creatorView);
            creatorIsMe(task);
        } else {
            loadUser(creatorId, creatorView, new CallableArg<User>() {
                @Override
                public void call(User item) {
                    onCreatorLoaded(item);
                }
            });
        }
    }

    private void initByReporter(Task task) {
        final TextView reporterView = (TextView) findViewById(R.id.task_reportedUser);
        String responsibleUserId = task.getResponsibleUserId();
        if (currentUser.getUid().equals(responsibleUserId)) {
            reporterView.setText("Me");
            highlightText(reporterView);
            reporterIsMe(task);
        } else {
            loadUser(responsibleUserId, reporterView, new CallableArg<User>() {
                @Override
                public void call(User item) {
                    onReportedUserLoad(item);
                }
            });
        }
    }

    private void creatorIsMe(Task task) {
        actionButton.setImageResource(R.drawable.ic_mode_edit);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskDetailActivity.this, "On Edit should open", Toast.LENGTH_SHORT).show();
            }
        });
        actionButton.show();
    }

    private void reporterIsMe(Task task) {
        actionButton.setImageResource(R.drawable.ic_done);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskDetailActivity.this, "TASK IS DONE", Toast.LENGTH_SHORT).show();
            }
        });
        actionButton.show();
    }

    private void highlightText(TextView creatorView) {
        creatorView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
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
