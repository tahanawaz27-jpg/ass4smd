package com.example.smdass4.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smdass4.R;
import com.example.smdass4.databinding.ItemProjectBinding;
import com.example.smdass4.model.Project;

public class ProjectAdapter extends ListAdapter<Project, ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private final OnProjectClickListener listener;

    public ProjectAdapter(OnProjectClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Project> DIFF_CALLBACK = new DiffUtil.ItemCallback<Project>() {
        @Override
        public boolean areItemsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProjectBinding binding = ItemProjectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        private final ItemProjectBinding binding;

        public ProjectViewHolder(@NonNull ItemProjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Project project) {
            binding.tvTag.setText(project.getTag());
            applyTagStyling(project.getTag());

            binding.tvProjectName.setText(project.getName());
            binding.tvDescription.setText(project.getDescription());

            int completed = project.getCompletedTaskCount();
            int total = project.getTotalTaskCount();
            int percentage = project.getProgressPercentage();

            binding.tvTaskCount.setText(completed + "/" + total + " tasks");
            binding.tvPercentage.setText(percentage + "%");
            binding.progressBar.setProgress(percentage);

            binding.tvAttachmentsCount.setText(project.getAttachmentCount() + " Attachments");
            binding.tvCommentsCount.setText(project.getCommentsCount() + " Comments");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProjectClick(project);
                }
            });
        }

        private void applyTagStyling(String tag) {
            int bgColor;
            int textColor;

            if (tag == null) tag = "";
            switch (tag.toLowerCase()) {
                case "backend":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_backend_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_backend_text);
                    break;
                case "frontend":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_frontend_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_frontend_text);
                    break;
                case "qa & ops":
                case "qa":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_qa_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_qa_text);
                    break;
                case "design":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_design_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_design_text);
                    break;
                case "user interface":
                default:
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_ui_bg);
                    textColor = ContextCompat.getColor(itemView.getContext(), R.color.tag_ui_text);
                    break;
            }

            binding.tvTag.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            binding.tvTag.setTextColor(textColor);
        }
    }
}