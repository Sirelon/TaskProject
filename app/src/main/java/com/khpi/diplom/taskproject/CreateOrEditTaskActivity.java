package com.khpi.diplom.taskproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateOrEditTaskActivity extends BaseActivity {

    private static final String ARG_TASK = ".task";
    private static final int DELETE_ITEM_ID = 12;

    public static Intent getIntentForCreation(Activity activity) {
        return new Intent(activity, CreateOrEditTaskActivity.class);
    }

    public static Intent getIntentForEdit(Activity activity, Task task) {
        Intent intent = getIntentForCreation(activity);
        intent.putExtra(ARG_TASK, task);
        return intent;
    }

    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private TextInputLayout taskUserInput;
    private Spinner prioritySpinner;
    private String[] priorityArray;
    private User selectedUser;

    @Nullable
    private Task editableTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = getLayoutInflater().inflate(R.layout.activity_create_task, null);
        setContentView(root);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameInput = (TextInputLayout) findViewById(R.id.task_name);
        descriptionInput = (TextInputLayout) findViewById(R.id.task_description);
        taskUserInput = (TextInputLayout) findViewById(R.id.task_reportedUser);

        prioritySpinner = (Spinner) findViewById(R.id.task_priority);

        priorityArray = getResources().getStringArray(R.array.arr_priority);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        taskUserInput.getEditText().setFocusable(false);
        taskUserInput.getEditText().setFocusableInTouchMode(false);
        taskUserInput.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogWithUsers();
            }
        });

        editableTask = getIntent().getParcelableExtra(ARG_TASK);

        bindViews(root);
    }

    private void bindViews(View root) {
        if (editableTask == null)
            return;

        TaskViewBinder viewBinder = new TaskViewBinder(root, nameInput, descriptionInput, null, prioritySpinner);
        viewBinder.bind(editableTask);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference = reference.child(Constants.REF_USER).child(editableTask.getResponsibleUserId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    User reporterUser = dataSnapshot.getValue(User.class);
                    if (null != reporterUser) {
                        bindUserOnView(reporterUser);
                    } else {
                        onCancelled(DatabaseError.fromException(new NullPointerException("User is empty")));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == DELETE_ITEM_ID) {
            doDeleteTask();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editableTask != null) {
            menu.add(1, DELETE_ITEM_ID, 1, "Delete").setIcon(R.drawable.ic_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void doDeleteTask() {
        new AlertDialog.Builder(this)
                .setTitle("Deletion task")
                .setMessage("Are you sure to want delete this task?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference taskFirebase = FirebaseDatabase.getInstance().getReference().child(Constants.REF_TASK).child(editableTask.getId());
                        taskFirebase.setValue(null);
                        dialog.dismiss();
                        Toast.makeText(CreateOrEditTaskActivity.this, "Task was deleted", Toast.LENGTH_SHORT).show();
                        MainActivity.start(CreateOrEditTaskActivity.this);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDialogWithUsers() {
        showProgress();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> users = new ArrayList<>(20);
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : children) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }

                hideProgress();
                showUsersInDialog(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CreateOrEditTaskActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
    }

    private void showUsersInDialog(final List<User> users) {
        List<String> userNames = new ArrayList<>(users.size());
        for (User user : users) {
            userNames.add(user.getName());
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreateOrEditTaskActivity.this);
        dialogBuilder.setTitle("Choose user");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bindUserOnView(users.get(which));
            }
        });
        dialogBuilder.show();
    }

    private void bindUserOnView(User user) {
        selectedUser = user;
        taskUserInput.getEditText().setText(selectedUser.getName());
        taskUserInput.setError(null);
    }

    private void onSaveClicked() {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID().toString());
        String nameStr = nameInput.getEditText().getText().toString();
        String descriptionStr = descriptionInput.getEditText().getText().toString();

        if (checkForValid(nameStr, nameInput)
                && checkForValid(descriptionStr, descriptionInput)
                && checkUserForValid()) {

            newTask.setName(nameStr);

            newTask.setDescription(descriptionStr);
            newTask.setPriority(priorityArray[prioritySpinner.getSelectedItemPosition()]);
            newTask.setCreationDate(System.currentTimeMillis());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            newTask.setCreatorId(user.getUid());
            newTask.setResponsibleUserId(selectedUser.getUid());

            saveTaskToFirebase(newTask);
        }
    }

    private boolean checkUserForValid() {
        if (selectedUser == null) {
            taskUserInput.setError("Select user for continue.");
            return false;
        } else {
            taskUserInput.setError(null);
            return true;
        }
    }

    private static boolean checkForValid(String descriptionStr, TextInputLayout inputLayout) {
        if (TextUtils.isEmpty(descriptionStr)) {
            inputLayout.setError("Cannot be blank.");
            inputLayout.requestFocus();
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }

    private void saveTaskToFirebase(Task newTask) {
        showProgress();
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference reference = instance.getReference(Constants.REF_TASK).child(newTask.getId());
        reference.setValue(newTask).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgress();
                String text = editableTask == null ? "Task was created" : "Task was edited";
                Toast.makeText(CreateOrEditTaskActivity.this, text, Toast.LENGTH_SHORT).show();
                MainActivity.start(CreateOrEditTaskActivity.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                e.printStackTrace();
                Toast.makeText(CreateOrEditTaskActivity.this, "Task creation error. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
