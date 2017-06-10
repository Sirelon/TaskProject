package com.khpi.diplom.taskproject;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 04/06/2017 21:19.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private static final int NORMAL_TASK = 1;
    private static final int CLOSED_TASK = 2;

    private final List<Task> data = new ArrayList<>();

    private CallableArg<Task> taskItemChooser;

    public void addData(List<Task> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void setClickCallback(CallableArg<Task> taskItemChooser) {
        this.taskItemChooser = taskItemChooser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder.getItemViewType() == CLOSED_TASK) {
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.grey_300);
            holder.rootClosed.setVisibility(View.VISIBLE);
            holder.rootTask.setBackgroundColor(color);
        } else {
            holder.rootClosed.setVisibility(View.GONE);
        }

        Task task = data.get(position);

        holder.binder.bind(task);

        if (taskItemChooser != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskItemChooser.call(data.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        Task task = data.get(position);
        return task.isClose() ? CLOSED_TASK : NORMAL_TASK;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TaskViewBinder binder;
        private final View rootClosed;
        private final View rootTask;

        public ViewHolder(View itemView) {
            super(itemView);
            binder = new TaskViewBinder(itemView);
            rootClosed = itemView.findViewById(R.id.root_closed);
            rootTask = itemView.findViewById(R.id.root_task);
        }
    }
}
