package com.khpi.diplom.taskproject;

import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created on 10/06/2017 13:18.
 */

public class TaskViewBinder {

    private final View root;
    private final TextView taskName;
    private final TextView taskDescription;
    private final TextView taskCreationDate;
    private final View priorityView;
    private final DateUtil dateUtil;

    public TaskViewBinder(View root) {
        this.root = root;
        dateUtil = new DateUtil();
        priorityView = root.findViewById(R.id.task_priority);
        taskName = (TextView) root.findViewById(R.id.task_name);
        taskDescription = (TextView) root.findViewById(R.id.task_description);
        taskCreationDate = (TextView) root.findViewById(R.id.task_creation_date);
    }

    public void bind(Task task){
        taskName.setText(task.getName());
        taskDescription.setText(task.getDescription());
        long creationDateLong = task.getCreationDate();
        taskCreationDate.setText(dateUtil.formatDate(creationDateLong));

        @ColorInt int color = ContextCompat.getColor(root.getContext(), Util.getPriorityColorForTask(task));
        priorityView.setBackgroundColor(color);
    }
}
