package com.khpi.diplom.taskproject;

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

    private final List<Task> data = new ArrayList<>();

    private CallableArg<Task> taskItemChooser;

    public void addData(List<Task> data) {
        this.data.addAll(data);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Task task = data.get(position);

        holder.binder.bind(task);

        if (taskItemChooser != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskItemChooser.call(data.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        final TaskViewBinder binder;

        public ViewHolder(View itemView) {
            super(itemView);
            binder = new TaskViewBinder(itemView);
        }
    }
}
