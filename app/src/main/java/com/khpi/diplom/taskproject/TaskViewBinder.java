package com.khpi.diplom.taskproject;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created on 10/06/2017 13:18.
 */

public class TaskViewBinder {

    private final View root;
    private final TextView taskName;
    private final TextView taskDescription;
    @Nullable
    private final TextView taskCreationDate;
    private final View priorityView;
    private final DateUtil dateUtil;

    public TaskViewBinder(View root, TextInputLayout taskName, TextInputLayout taskDescription, @Nullable TextInputLayout taskCreationDate, View priorityView) {
        this.root = root;
        dateUtil = new DateUtil();
        this.taskName = taskName.getEditText();
        this.taskDescription = taskDescription.getEditText();
        if (taskCreationDate != null) {
            this.taskCreationDate = taskCreationDate.getEditText();
        } else {
            this.taskCreationDate = null;
        }
        this.priorityView = priorityView;
    }

    public TaskViewBinder(View root) {
        this.root = root;
        dateUtil = new DateUtil();
        priorityView = root.findViewById(R.id.task_priority);
        taskName = (TextView) root.findViewById(R.id.task_name);
        taskDescription = (TextView) root.findViewById(R.id.task_description);
        taskCreationDate = (TextView) root.findViewById(R.id.task_creation_date);
    }

    public void bind(Task task) {
        taskName.setText(task.getName());
        taskDescription.setText(task.getDescription());

        if (taskCreationDate != null) {
            long creationDateLong = task.getCreationDate();
            taskCreationDate.setText(dateUtil.formatDate(creationDateLong));
        }

        @ColorInt int color = ContextCompat.getColor(root.getContext(), Util.getPriorityColor(task.getPriority()));
        priorityView.setBackgroundColor(color);

        if (priorityView instanceof TextView) {
            ((TextView) priorityView).setText(task.getPriority());
        } else if (priorityView instanceof Spinner) {
            ((Spinner) priorityView).setPrompt(task.getPriority());
        }
    }
}
