package com.example.smdass4.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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