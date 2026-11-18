package com.example.smdass4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smdass4.adapter.ProjectAdapter;
import com.example.smdass4.databinding.ActivityHomeBinding;
import com.example.smdass4.model.Project;
import com.example.smdass4.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_PROJECT_ID = "extra_project_id";
    public static final String EXTRA_PROJECT = "extra_project";

    private ActivityHomeBinding binding;
    private ProjectAdapter adapter;
    private ProjectRepository repository;
    private List<Project> currentProjectList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> projectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Project updatedProject = (Project) result.getData().getSerializableExtra(EXTRA_PROJECT);
                    if (updatedProject != null) {
                        repository.updateProject(updatedProject);
                    }
                }
                loadProjects();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ProjectRepository.getInstance();

        setupRecyclerView();
        setupSearch();
        setupNewProjectButton();
        loadProjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }

    private void setupRecyclerView() {
        adapter = new ProjectAdapter(project -> {
            Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
            intent.putExtra(EXTRA_PROJECT_ID, project.getId());
            intent.putExtra(EXTRA_PROJECT, project);
            projectActivityResultLauncher.launch(intent);
        });

        binding.rvProjects.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProjects.setAdapter(adapter);
    }

    private void loadProjects() {
        currentProjectList = repository.getProjects();
        filterProjects(binding.etSearch.getText() != null ? binding.etSearch.getText().toString() : "");
    }

    private void filterProjects(String query) {
        List<Project> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(currentProjectList);
        } else {
            String lower = query.toLowerCase().trim();
            for (Project p : currentProjectList) {
                if ((p.getName() != null && p.getName().toLowerCase().contains(lower)) ||
                    (p.getTag() != null && p.getTag().toLowerCase().contains(lower)) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(lower))) {
                    filteredList.add(p);
                }
            }
        }
        binding.tvProjectCount.setText("Showing " + filteredList.size() + " projects");
        adapter.submitList(filteredList);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProjects(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNewProjectButton() {
        binding.btnNewProject.setOnClickListener(v -> showNewProjectDialog());
    }

    private void showNewProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Project");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_project, null);
        EditText etTag = dialogView.findViewById(R.id.etProjectTag);
        EditText etName = dialogView.findViewById(R.id.etProjectName);
        EditText etDesc = dialogView.findViewById(R.id.etProjectDesc);

        builder.setView(dialogView);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String tag = etTag.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Project name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tag.isEmpty()) tag = "User Interface";

            Project newProject = new Project(
                    UUID.randomUUID().toString(),
                    tag,
                    name,
                    desc,
                    0,
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            repository.addProject(newProject);
            loadProjects();
            Toast.makeText(HomeActivity.this, "Project created successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}