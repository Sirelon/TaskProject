package com.khpi.diplom.taskproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText description;
    private EditText name;
    private EditText taskUser;
    private Spinner prioritySpinner;
    private String[] priorityArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        name = (EditText) findViewById(R.id.field_taskName);
        description = (EditText) findViewById(R.id.field_taskDescription);
        taskUser = (EditText) findViewById(R.id.field_taskUser);

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
    }

    private void onSaveClicked() {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID().toString());
        newTask.setName(name.getText().toString());
        newTask.setDescription(description.getText().toString());
        newTask.setPriority(priorityArray[prioritySpinner.getSelectedItemPosition()]);
        newTask.setCreationDate(System.currentTimeMillis());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        newTask.setCreatorId(user.getUid());
        newTask.setResponsibleUserId(user.getUid());

        saveTaskToFirebase(newTask);
    }

    private void saveTaskToFirebase(Task newTask) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference reference = instance.getReference("task").child(newTask.getId());
        reference.setValue(newTask).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateTaskActivity.this, "Task was created", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(CreateTaskActivity.this, "Task creation error. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
