package com.example.smdass4.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smdass4.databinding.FragmentProjectOverviewBinding;
import com.example.smdass4.model.Attachment;
import com.example.smdass4.model.Project;
import com.example.smdass4.model.Task;

import java.util.List;

public class ProjectOverviewFragment extends Fragment {

    private static final String ARG_PROJECT = "arg_project";
    private FragmentProjectOverviewBinding binding;
    private Project project;

    public static ProjectOverviewFragment newInstance(Project project) {
        ProjectOverviewFragment fragment = new ProjectOverviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectOverviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            project = (Project) getArguments().getSerializable(ARG_PROJECT);
        }
        updateUI();
    }

    public void updateProject(Project updatedProject) {
        this.project = updatedProject;
        if (binding != null) {
            updateUI();
        }
    }

    private void updateUI() {
        if (project == null || binding == null) return;

        binding.tvOverviewBrief.setText(project.getDescription());

        int total = project.getTotalTaskCount();
        int completed = project.getCompletedTaskCount();
        int percentage = project.getProgressPercentage();

        binding.tvOverviewPercentage.setText(percentage + "%");
        binding.tvOverviewTaskProgressCount.setText(completed + " of " + total + " tasks completed");
        binding.overviewProgressBar.setProgress(percentage);

        // Calculate breakdown by status
        int todo = 0, inProgress = 0, done = 0, overdue = 0;
        List<Task> tasks = project.getTasks();
        if (tasks != null) {
            for (Task t : tasks) {
                String st = t.getStatus() != null ? t.getStatus() : "";
                switch (st.toLowerCase()) {
                    case "overdue":
                        overdue++;
                        break;
                    case "in progress":
                        inProgress++;
                        break;
                    case "done":
                        done++;
                        break;
                    case "to do":
                    default:
                        todo++;
                        break;
                }
            }
        }

        binding.tvOverviewTodoCount.setText(String.valueOf(todo));
        binding.tvOverviewInProgressCount.setText(String.valueOf(inProgress));
        binding.tvOverviewDoneCount.setText(String.valueOf(done));
        binding.tvOverviewOverdueCount.setText(String.valueOf(overdue));

        List<Attachment> attachments = project.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(attachments.size()).append(" files • ");
            for (int i = 0; i < Math.min(attachments.size(), 2); i++) {
                if (i > 0) sb.append(", ");
                sb.append(attachments.get(i).getFileName());
            }
            binding.tvOverviewAttachmentsPreview.setText(sb.toString());
        } else {
            binding.tvOverviewAttachmentsPreview.setText("No attachments");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}