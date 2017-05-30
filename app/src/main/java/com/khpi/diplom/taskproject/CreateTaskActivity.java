package com.khpi.diplom.taskproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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

public class CreateTaskActivity extends BaseActivity {

    private TextInputLayout nameInput;
    private TextInputLayout descriptionInput;
    private TextInputLayout taskUserInput;
    private Spinner prioritySpinner;
    private String[] priorityArray;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameInput = (TextInputLayout) findViewById(R.id.field_taskName);
        descriptionInput = (TextInputLayout) findViewById(R.id.field_taskDescription);
        taskUserInput = (TextInputLayout) findViewById(R.id.field_taskUser);

        prioritySpinner = (Spinner) findViewById(R.id.list_priority);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
                Toast.makeText(CreateTaskActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
    }

    private void showUsersInDialog(final List<User> users) {
        List<String> userNames = new ArrayList<>(users.size());
        for (User user : users) {
            userNames.add(user.getName());
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreateTaskActivity.this);
        dialogBuilder.setTitle("Choose user");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedUser = users.get(which);
                taskUserInput.getEditText().setText(selectedUser.getName());
                taskUserInput.setError(null);
            }
        });
        dialogBuilder.show();
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
        DatabaseReference reference = instance.getReference("task").child(newTask.getId());
        reference.setValue(newTask).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgress();
                Toast.makeText(CreateTaskActivity.this, "Task was created", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                e.printStackTrace();
                Toast.makeText(CreateTaskActivity.this, "Task creation error. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
