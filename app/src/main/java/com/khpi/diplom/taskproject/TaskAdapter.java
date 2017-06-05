package com.khpi.diplom.taskproject;

import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 04/06/2017 21:19.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> data = new ArrayList<>();

    private final DateUtil dateUtil = new DateUtil();
    private ItemChooser<Task> taskItemChooser;

    public void addData(List<Task> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setClickCallback(ItemChooser<Task> taskItemChooser) {
        this.taskItemChooser = taskItemChooser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Task task = data.get(position);
        holder.taskName.setText(task.getName());
        holder.taskDescription.setText(task.getDescription());
        long creationDateLong = task.getCreationDate();
        holder.taskCreationDate.setText(dateUtil.formatDate(creationDateLong));

        @ColorInt int color = ContextCompat.getColor(holder.itemView.getContext(), Util.getPriorityColorForTask(task));
        holder.priorityView.setBackgroundColor(color);

        if (taskItemChooser != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskItemChooser.choose(data.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskName;
        private final TextView taskDescription;
        private final TextView taskCreationDate;
        private final View priorityView;

        public ViewHolder(View itemView) {
            super(itemView);
            priorityView = itemView.findViewById(R.id.task_priority);
            taskName = (TextView) itemView.findViewById(R.id.task_name);
            taskDescription = (TextView) itemView.findViewById(R.id.task_description);
            taskCreationDate = (TextView) itemView.findViewById(R.id.task_creation_date);
        }
    }
}
