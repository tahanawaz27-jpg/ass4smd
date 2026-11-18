package com.example.smdass4.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smdass4.R;
import com.example.smdass4.adapter.AttachmentAdapter;
import com.example.smdass4.databinding.FragmentFilesBinding;
import com.example.smdass4.model.Attachment;
import com.example.smdass4.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FilesFragment extends Fragment {

    private static final String ARG_PROJECT = "arg_project";
    private FragmentFilesBinding binding;
    private Project project;
    private AttachmentAdapter adapter;

    public interface OnAttachmentUpdatedListener {
        void onAttachmentUpdated(Project updatedProject);
    }

    private OnAttachmentUpdatedListener listener;

    public static FilesFragment newInstance(Project project) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAttachmentUpdatedListener(OnAttachmentUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
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
        setupAddButton();
        loadAttachments();
    }

    public void updateProject(Project updatedProject) {
        this.project = updatedProject;
        if (binding != null) {
            loadAttachments();
        }
    }

    private void setupRecyclerView() {
        adapter = new AttachmentAdapter(new AttachmentAdapter.OnAttachmentActionListener() {
            @Override
            public void onPreviewAttachment(Attachment attachment) {
                showPreviewDialog(attachment);
            }

            @Override
            public void onRemoveAttachment(Attachment attachment, int position) {
                showNameConfirmationDeleteDialog(attachment, position);
            }
        });

        binding.rvAttachments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAttachments.setAdapter(adapter);
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getBindingAdapterPosition();
                if (pos >= 0 && adapter != null && pos < adapter.getCurrentList().size()) {
                    Attachment attachment = adapter.getCurrentList().get(pos);
                    showNameConfirmationDeleteDialog(attachment, pos);
                }
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(swipeCallback);
        helper.attachToRecyclerView(binding.rvAttachments);
    }

    private void showNameConfirmationDeleteDialog(Attachment attachment, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Attachment Deletion");

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(50, 30, 50, 10);

        TextView tvMsg = new TextView(requireContext());
        tvMsg.setText("Please enter the attachment name to confirm deletion:\n\"" + attachment.getFileName() + "\"");
        tvMsg.setTextColor(getResources().getColor(R.color.text_primary, null));
        tvMsg.setTextSize(14);
        container.addView(tvMsg);

        EditText etInput = new EditText(requireContext());
        etInput.setHint("Type attachment name here");
        etInput.setPadding(20, 20, 20, 20);
        etInput.setBackgroundResource(R.drawable.bg_search);
        container.addView(etInput);

        builder.setView(container);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String inputName = etInput.getText().toString().trim();
            if (inputName.equalsIgnoreCase(attachment.getFileName().trim())) {
                if (project != null && project.getAttachments() != null) {
                    project.getAttachments().remove(attachment);
                    Toast.makeText(requireContext(), "Attachment removed", Toast.LENGTH_SHORT).show();
                    notifyProjectUpdated();
                }
            } else {
                Toast.makeText(requireContext(), "Attachment name does not match! Deletion cancelled.", Toast.LENGTH_LONG).show();
                adapter.notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            adapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.setOnCancelListener(dialog -> adapter.notifyItemChanged(position));
        builder.show();
    }

    private void showPreviewDialog(Attachment attachment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Preview: " + attachment.getFileName());

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 30, 40, 30);

        TextView tvType = new TextView(requireContext());
        tvType.setText("Type: " + attachment.getFileType() + " | Size: " + attachment.getFileSize());
        tvType.setTextSize(13);
        tvType.setTextColor(getResources().getColor(R.color.text_secondary, null));
        container.addView(tvType);

        TextView tvPreview = new TextView(requireContext());
        tvPreview.setPadding(0, 20, 0, 0);
        tvPreview.setText(attachment.getPreviewText() != null ? attachment.getPreviewText() : "Preview content for " + attachment.getFileName());
        tvPreview.setTextSize(15);
        tvPreview.setTextColor(getResources().getColor(R.color.text_primary, null));
        container.addView(tvPreview);

        builder.setView(container);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setupAddButton() {
        binding.btnAddAttachment.setOnClickListener(v -> showAddAttachmentDialog());
    }

    private void showAddAttachmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Attachment");

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        EditText etName = new EditText(requireContext());
        etName.setHint("Attachment File Name (e.g. document.pdf)");
        etName.setPadding(20, 20, 20, 20);
        etName.setBackgroundResource(R.drawable.bg_search);
        container.addView(etName);

        EditText etType = new EditText(requireContext());
        etType.setHint("File Type (e.g. Document, Image)");
        etType.setPadding(20, 20, 20, 20);
        etType.setBackgroundResource(R.drawable.bg_search);
        container.addView(etType);

        builder.setView(container);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String type = etType.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "File name required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (type.isEmpty()) type = "Document";

            Attachment newAtt = new Attachment(
                    UUID.randomUUID().toString(),
                    name,
                    type,
                    "1.2 MB",
                    "Link",
                    "Preview description for " + name
            );

            if (project != null && project.getAttachments() != null) {
                project.getAttachments().add(newAtt);
                notifyProjectUpdated();
                Toast.makeText(requireContext(), "Attachment added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadAttachments() {
        if (project == null || binding == null) return;

        String query = binding.etFileSearch.getText() != null ? binding.etFileSearch.getText().toString().trim().toLowerCase() : "";
        List<Attachment> all = project.getAttachments();
        List<Attachment> filtered = new ArrayList<>();

        if (all != null) {
            for (Attachment a : all) {
                if (query.isEmpty() || (a.getFileName() != null && a.getFileName().toLowerCase().contains(query))) {
                    filtered.add(a);
                }
            }
        }

        binding.tvFileCountHeader.setText("Showing " + filtered.size() + " of " + (all != null ? all.size() : 0) + " attachments");
        adapter.submitList(filtered);
    }

    private void setupSearch() {
        binding.etFileSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadAttachments();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void notifyProjectUpdated() {
        loadAttachments();
        if (listener != null) {
            listener.onAttachmentUpdated(project);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}