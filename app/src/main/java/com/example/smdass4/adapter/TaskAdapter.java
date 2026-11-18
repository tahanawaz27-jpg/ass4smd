package com.example.smdass4.adapter;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smdass4.R;
import com.example.smdass4.databinding.ItemTaskBinding;
import com.example.smdass4.databinding.ItemTaskHeaderBinding;
import com.example.smdass4.model.Task;
import com.example.smdass4.model.TaskItem;

public class TaskAdapter extends ListAdapter<TaskItem, RecyclerView.ViewHolder> {

    public interface OnTaskStatusChangeListener {
        void onTaskCompletionChanged(Task task, boolean isCompleted);
        void onTaskClick(Task task);
    }

    private final OnTaskStatusChangeListener listener;

    public TaskAdapter(OnTaskStatusChangeListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<TaskItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<TaskItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaskItem oldItem, @NonNull TaskItem newItem) {
            if (oldItem.getType() != newItem.getType()) return false;
            if (oldItem.getType() == TaskItem.TYPE_HEADER) {
                return oldItem.getStatusGroup().equals(newItem.getStatusGroup());
            } else {
                return oldItem.getTask().getId().equals(newItem.getTask().getId());
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskItem oldItem, @NonNull TaskItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TaskItem.TYPE_HEADER) {
            ItemTaskHeaderBinding binding = ItemTaskHeaderBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new HeaderViewHolder(binding);
        } else {
            ItemTaskBinding binding = ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new TaskViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskItem item = getItem(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bind(item);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskHeaderBinding binding;

        public HeaderViewHolder(@NonNull ItemTaskHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TaskItem item) {
            binding.tvHeaderTitle.setText(item.getHeaderTitle());
            binding.tvHeaderCount.setText(String.valueOf(item.getHeaderTaskCount()));

            String group = item.getStatusGroup() != null ? item.getStatusGroup().toLowerCase() : "";
            int bgColor;
            int textColor;

            switch (group) {
                case "overdue":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_overdue_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.status_overdue_text);
                    break;
                case "in progress":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_in_progress_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.status_in_progress_text);
                    break;
                case "done":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_done_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.status_done_text);
                    break;
                case "to do":
                default:
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.status_todo_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.status_todo_text);
                    break;
            }

            binding.tvHeaderCount.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            binding.tvHeaderCount.setTextColor(textColor);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TaskItem item) {
            Task task = item.getTask();
            if (task == null) return;

            binding.tvTaskDescription.setText(task.getDescription());
            binding.cbTaskCompleted.setOnCheckedChangeListener(null);
            binding.cbTaskCompleted.setChecked(task.isCompleted() || "Done".equalsIgnoreCase(task.getStatus()));

            if (task.isCompleted() || "Done".equalsIgnoreCase(task.getStatus())) {
                binding.tvTaskDescription.setPaintFlags(binding.tvTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                binding.tvTaskDescription.setPaintFlags(binding.tvTaskDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            binding.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCompletionChanged(task, isChecked);
                }
            });

            binding.tvTaskStatus.setText(task.getStatus());
            applyStatusStyle(task.getStatus());

            binding.tvTaskPriority.setText(task.getPriority());
            applyPriorityStyle(task.getPriority());

            String dueDateStr = task.getDueDate() != null ? task.getDueDate() : "No due date";
            binding.tvTaskDueDate.setText("Overdue".equalsIgnoreCase(task.getStatus()) ? dueDateStr + " (Overdue)" : dueDateStr);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }

        private void applyStatusStyle(String status) {
            if (status == null) status = "";
            int bg;
            int txt;

            switch (status.toLowerCase()) {
                case "overdue":
                    bg = ContextCompat.getColor(itemView.getContext(), R.color.status_overdue_bg);
                    txt = ContextCompat.getColor(itemView.getContext(), R.color.status_overdue_text);
                    break;
                case "in progress":
                    bg = ContextCompat.getColor(itemView.getContext(), R.color.status_in_progress_bg);
                    txt = ContextCompat.getColor(itemView.getContext(), R.color.status_in_progress_text);
                    break;
                case "done":
                    bg = ContextCompat.getColor(itemView.getContext(), R.color.status_done_bg);
                    txt = ContextCompat.getColor(itemView.getContext(), R.color.status_done_text);
                    break;
                case "to do":
                default:
                    bg = ContextCompat.getColor(itemView.getContext(), R.color.status_todo_bg);
                    txt = ContextCompat.getColor(itemView.getContext(), R.color.status_todo_text);
                    break;
            }
            binding.tvTaskStatus.setBackgroundTintList(ColorStateList.valueOf(bg));
            binding.tvTaskStatus.setTextColor(txt);
        }

        private void applyPriorityStyle(String priority) {
            if (priority == null) priority = "";
            int color;

            switch (priority.toLowerCase()) {
                case "high":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.priority_high);
                    break;
                case "low":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.priority_low);
                    break;
                case "normal":
                default:
                    color = ContextCompat.getColor(itemView.getContext(), R.color.priority_normal);
                    break;
            }
            binding.tvTaskPriority.setTextColor(color);
        }
    }
}