package com.example.smdass4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smdass4.databinding.ActivityProjectBinding;
import com.example.smdass4.databinding.BottomSheetAddTaskBinding;
import com.example.smdass4.fragment.DiscussionFragment;
import com.example.smdass4.fragment.FilesFragment;
import com.example.smdass4.fragment.ProjectOverviewFragment;
import com.example.smdass4.fragment.TasksFragment;
import com.example.smdass4.model.Project;
import com.example.smdass4.model.Task;
import com.example.smdass4.repository.ProjectRepository;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.UUID;

public class ProjectActivity extends AppCompatActivity {

    private ActivityProjectBinding binding;
    private Project project;
    private ProjectRepository repository;

    private ProjectOverviewFragment overviewFragment;
    private TasksFragment tasksFragment;
    private FilesFragment filesFragment;
    private DiscussionFragment discussionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ProjectRepository.getInstance();

        String projectId = getIntent().getStringExtra(HomeActivity.EXTRA_PROJECT_ID);
        if (projectId != null) {
            project = repository.getProjectById(projectId);
        }

        if (project == null) {
            project = (Project) getIntent().getSerializableExtra(HomeActivity.EXTRA_PROJECT);
        }

        if (project == null) {
            Toast.makeText(this, "Project not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupHeaderUI();
        setupViewPagerAndTabs();
        setupAddTaskButton();

        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupHeaderUI() {
        binding.tvProjectHeaderTag.setText(project.getTag());
        binding.tvProjectHeaderTitle.setText(project.getName());
        applyTagStyling(project.getTag());
    }

    private void applyTagStyling(String tag) {
        if (tag == null) tag = "";
        int textColor;

        switch (tag.toLowerCase()) {
            case "backend":
                textColor = ContextCompat.getColor(this, R.color.tag_backend_text);
                break;
            case "frontend":
                textColor = ContextCompat.getColor(this, R.color.tag_frontend_text);
                break;
            case "qa & ops":
            case "qa":
                textColor = ContextCompat.getColor(this, R.color.tag_qa_text);
                break;
            case "design":
                textColor = ContextCompat.getColor(this, R.color.tag_design_text);
                break;
            case "user interface":
            default:
                textColor = ContextCompat.getColor(this, R.color.tag_ui_text);
                break;
        }
        binding.tvProjectHeaderTag.setTextColor(textColor);
    }

    private void setupViewPagerAndTabs() {
        overviewFragment = ProjectOverviewFragment.newInstance(project);
        tasksFragment = TasksFragment.newInstance(project);
        filesFragment = FilesFragment.newInstance(project);
        discussionFragment = new DiscussionFragment();

        tasksFragment.setOnTaskUpdatedListener(updatedProject -> {
            this.project = updatedProject;
            saveAndSyncData();
        });

        filesFragment.setOnAttachmentUpdatedListener(updatedProject -> {
            this.project = updatedProject;
            saveAndSyncData();
        });

        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return overviewFragment;
                    case 1:
                        return tasksFragment;
                    case 2:
                        return filesFragment;
                    case 3:
                    default:
                        return discussionFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            updateTabTitle(tab, position);
        }).attach();
    }

    private void updateTabTitle(TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText("Overview");
                break;
            case 1:
                tab.setText("Tasks (" + (project != null ? project.getTotalTaskCount() : 0) + ")");
                break;
            case 2:
                tab.setText("Files (" + (project != null ? project.getAttachmentCount() : 0) + ")");
                break;
            case 3:
                tab.setText("Discussion (" + (project != null ? project.getCommentsCount() : 0) + ")");
                break;
        }
    }

    private void saveAndSyncData() {
        if (project == null) return;
        repository.updateProject(project);

        // Notify fragments
        if (overviewFragment != null) overviewFragment.updateProject(project);
        if (tasksFragment != null) tasksFragment.updateProject(project);
        if (filesFragment != null) filesFragment.updateProject(project);

        // Update tab titles
        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            if (tab != null) {
                updateTabTitle(tab, i);
            }
        }

        // Set result intent for HomeActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra(HomeActivity.EXTRA_PROJECT_ID, project.getId());
        resultIntent.putExtra(HomeActivity.EXTRA_PROJECT, project);
        setResult(RESULT_OK, resultIntent);
    }

    private void setupAddTaskButton() {
        binding.btnAddTask.setOnClickListener(v -> showAddTaskBottomSheet());
    }

    private void showAddTaskBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetAddTaskBinding dialogBinding = BottomSheetAddTaskBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvAddTaskProjectName.setText(project.getName() + " project");

        // Status Spinner Adapter
        String[] statuses = new String[]{"To do", "In progress", "Overdue", "Done"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        dialogBinding.spinnerStatus.setAdapter(statusAdapter);

        // Priority Spinner Adapter
        String[] priorities = new String[]{"Normal", "High", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities);
        dialogBinding.spinnerPriority.setAdapter(priorityAdapter);

        dialogBinding.btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnCancelAddTask.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnSubmitAddTask.setOnClickListener(v -> {
            String desc = dialogBinding.etNewTaskDescription.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(ProjectActivity.this, "Task description is required", Toast.LENGTH_SHORT).show();
                return;
            }

            String status = (String) dialogBinding.spinnerStatus.getSelectedItem();
            String priority = (String) dialogBinding.spinnerPriority.getSelectedItem();
            String dueDate = dialogBinding.etNewTaskDueDate.getText().toString().trim();
            if (dueDate.isEmpty()) dueDate = "No due date";

            boolean completed = "Done".equalsIgnoreCase(status);

            Task newTask = new Task(
                    UUID.randomUUID().toString(),
                    desc,
                    status,
                    priority,
                    dueDate,
                    completed
            );

            if (project != null && project.getTasks() != null) {
                project.getTasks().add(newTask);
                saveAndSyncData();
                Toast.makeText(ProjectActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Switch to Tasks tab to view newly added task
                binding.viewPager.setCurrentItem(1, true);
            }
        });

        dialog.show();
    }
}