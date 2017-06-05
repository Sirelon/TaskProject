package com.khpi.diplom.taskproject;

import android.support.annotation.ColorRes;

/**
 * Created on 05/06/2017 21:53.
 */

public class Util {

    @ColorRes
    public static int getPriorityColorForTask(Task task) {
        switch (task.getPriority().toLowerCase()) {
            case "high":
                return R.color.priorityHigh;
            case "normal":
                return R.color.priorityNormal;
            case "low":
                return R.color.priorityLow;
            default:
                return R.color.colorAccent;
        }
    }

}
