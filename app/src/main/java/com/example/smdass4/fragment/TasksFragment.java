package com.example.smdass4.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smdass4.adapter.TaskAdapter;
import com.example.smdass4.databinding.FragmentTasksBinding;
import com.example.smdass4.model.Project;
import com.example.smdass4.model.Task;
import com.example.smdass4.model.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private static final String ARG_PROJECT = "arg_project";
    private FragmentTasksBinding binding;
    private Project project;
    protected TaskAdapter adapter;

    public interface OnTaskUpdatedListener {
        void onTaskUpdated(Project updatedProject);
    }

    private OnTaskUpdatedListener listener;

    public static TasksFragment newInstance(Project project) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable(ARG_PROJECT);
        }
        setupRecyclerView();
        setupSearch();
        setupItemTouchHelper();
        loadGroupedTasks();
    }

    public void updateProject(Project updatedProject) {
        this.project = updatedProject;
        if (binding != null) {
            loadGroupedTasks();
        }
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(new TaskAdapter.OnTaskStatusChangeListener() {
            @Override
            public void onTaskCompletionChanged(Task task, boolean isCompleted) {
                if (task != null) {
                    task.setCompleted(isCompleted);
                    task.setStatus(isCompleted ? "Done" : "To do");
                    notifyProjectUpdated();
                }
            }

            @Override
            public void onTaskClick(Task task) {}
        });

        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(adapter);
    }

    protected void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int pos = viewHolder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && adapter != null) {
                    TaskItem item = adapter.getCurrentList().get(pos);
                    if (item != null && item.getType() == TaskItem.TYPE_HEADER) {
                        return 0; // Headers cannot be swiped!
                    }
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION || adapter == null) return;

                TaskItem item = adapter.getCurrentList().get(pos);
                if (item == null || item.getTask() == null) return;

                Task task = item.getTask();

                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe Left: Mark as Done
                    if (task.isCompleted() || "Done".equalsIgnoreCase(task.getStatus())) {
                        Toast.makeText(requireContext(), "Task already completed", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(pos);
                    } else {
                        task.setCompleted(true);
                        task.setStatus("Done");
                        Toast.makeText(requireContext(), "Task marked as Done", Toast.LENGTH_SHORT).show();
                        notifyProjectUpdated();
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe Right: Delete with confirmation dialog
                    showDeleteConfirmationDialog(task, pos);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvTasks);
    }

    private void showDeleteConfirmationDialog(Task task, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?\n\n\"" + task.getDescription() + "\"")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (project != null && project.getTasks() != null) {
                        project.getTasks().remove(task);
                        Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                        notifyProjectUpdated();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                })
                .setOnCancelListener(dialog -> adapter.notifyItemChanged(position))
                .show();
    }

    public void loadGroupedTasks() {
        if (project == null || binding == null) return;

        String query = binding.etTaskSearch.getText() != null ? binding.etTaskSearch.getText().toString().trim().toLowerCase() : "";
        List<Task> allTasks = project.getTasks();

        List<Task> filtered = new ArrayList<>();
        if (allTasks != null) {
            for (Task t : allTasks) {
                if (query.isEmpty() || (t.getDescription() != null && t.getDescription().toLowerCase().contains(query))) {
                    filtered.add(t);
                }
            }
        }

        binding.tvTaskCountHeader.setText("Showing " + filtered.size() + " of " + (allTasks != null ? allTasks.size() : 0) + " tasks");

        List<Task> overdueList = new ArrayList<>();
        List<Task> inProgressList = new ArrayList<>();
        List<Task> todoList = new ArrayList<>();
        List<Task> doneList = new ArrayList<>();

        for (Task t : filtered) {
            String status = t.getStatus() != null ? t.getStatus().toLowerCase() : "";
            if (t.isCompleted() || "done".equals(status)) {
                doneList.add(t);
            } else if ("overdue".equals(status)) {
                overdueList.add(t);
            } else if ("in progress".equals(status)) {
                inProgressList.add(t);
            } else {
                todoList.add(t);
            }
        }

        List<TaskItem> items = new ArrayList<>();

        // Group 1: Overdue
        if (!overdueList.isEmpty()) {
            items.add(new TaskItem("Overdue", overdueList.size(), "Overdue"));
            for (Task t : overdueList) {
                items.add(new TaskItem(t, "Overdue"));
            }
        }

        // Group 2: In progress
        if (!inProgressList.isEmpty()) {
            items.add(new TaskItem("In Progress", inProgressList.size(), "In Progress"));
            for (Task t : inProgressList) {
                items.add(new TaskItem(t, "In Progress"));
            }
        }

        // Group 3: To do
        if (!todoList.isEmpty()) {
            items.add(new TaskItem("To Do", todoList.size(), "To Do"));
            for (Task t : todoList) {
                items.add(new TaskItem(t, "To Do"));
            }
        }

        // Group 4: Done
        if (!doneList.isEmpty()) {
            items.add(new TaskItem("Done", doneList.size(), "Done"));
            for (Task t : doneList) {
                items.add(new TaskItem(t, "Done"));
            }
        }

        adapter.submitList(items);
    }

    private void setupSearch() {
        binding.etTaskSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadGroupedTasks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    protected void notifyProjectUpdated() {
        loadGroupedTasks();
        if (listener != null) {
            listener.onTaskUpdated(project);
        }
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}