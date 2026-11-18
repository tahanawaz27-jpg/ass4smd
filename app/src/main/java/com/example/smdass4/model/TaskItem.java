package com.example.smdass4.model;

import java.util.Objects;

public class TaskItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;

    private final int type;
    private String headerTitle;
    private int headerTaskCount;
    private Task task;
    private String statusGroup;

    // Header constructor
    public TaskItem(String headerTitle, int headerTaskCount, String statusGroup) {
        this.type = TYPE_HEADER;
        this.headerTitle = headerTitle;
        this.headerTaskCount = headerTaskCount;
        this.statusGroup = statusGroup;
        this.task = null;
    }

    // Task constructor
    public TaskItem(Task task, String statusGroup) {
        this.type = TYPE_TASK;
        this.task = task;
        this.statusGroup = statusGroup;
        this.headerTitle = null;
        this.headerTaskCount = 0;
    }

    public int getType() { return type; }

    public String getHeaderTitle() { return headerTitle; }
    public void setHeaderTitle(String headerTitle) { this.headerTitle = headerTitle; }

    public int getHeaderTaskCount() { return headerTaskCount; }
    public void setHeaderTaskCount(int headerTaskCount) { this.headerTaskCount = headerTaskCount; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public String getStatusGroup() { return statusGroup; }
    public void setStatusGroup(String statusGroup) { this.statusGroup = statusGroup; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskItem taskItem = (TaskItem) o;
        return type == taskItem.type &&
                headerTaskCount == taskItem.headerTaskCount &&
                Objects.equals(headerTitle, taskItem.headerTitle) &&
                Objects.equals(task, taskItem.task) &&
                Objects.equals(statusGroup, taskItem.statusGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, headerTitle, headerTaskCount, task, statusGroup);
    }
}