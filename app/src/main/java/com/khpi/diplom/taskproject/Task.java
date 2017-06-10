package com.khpi.diplom.taskproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 25/05/2017 22:35.
 */

public class Task implements Parcelable{

    private String id;
    private String creatorId;
    private String name;
    private String description;
    private long creationDate;
    private String responsibleUserId;

    private String priority;

    public Task(){

    }

    protected Task(Parcel in) {
        id = in.readString();
        creatorId = in.readString();
        name = in.readString();
        description = in.readString();
        creationDate = in.readLong();
        responsibleUserId = in.readString();
        priority = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(creatorId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(creationDate);
        dest.writeString(responsibleUserId);
        dest.writeString(priority);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getResponsibleUserId() {
        return responsibleUserId;
    }

    public void setResponsibleUserId(String responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
