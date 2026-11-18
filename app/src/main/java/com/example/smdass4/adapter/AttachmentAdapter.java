package com.example.smdass4.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smdass4.R;
import com.example.smdass4.databinding.ItemAttachmentBinding;
import com.example.smdass4.model.Attachment;

public class AttachmentAdapter extends ListAdapter<Attachment, AttachmentAdapter.AttachmentViewHolder> {

    public interface OnAttachmentActionListener {
        void onPreviewAttachment(Attachment attachment);
        void onRemoveAttachment(Attachment attachment, int position);
    }

    private final OnAttachmentActionListener listener;

    public AttachmentAdapter(OnAttachmentActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Attachment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Attachment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Attachment oldItem, @NonNull Attachment newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Attachment oldItem, @NonNull Attachment newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAttachmentBinding binding = ItemAttachmentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new AttachmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class AttachmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAttachmentBinding binding;

        public AttachmentViewHolder(@NonNull ItemAttachmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Attachment attachment) {
            binding.tvFileName.setText(attachment.getFileName());

            String type = attachment.getFileType() != null ? attachment.getFileType() : "File";
            String size = attachment.getFileSize() != null ? attachment.getFileSize() : "";
            String link = attachment.getLinkInfo() != null ? attachment.getLinkInfo() : "";
            binding.tvFileInfo.setText(type + " • " + size + " • " + link);

            // Icon styling
            if ("image".equalsIgnoreCase(type)) {
                binding.ivFileIcon.setImageResource(R.drawable.ic_document);
                binding.ivFileIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tag_ui_bg)));
                binding.ivFileIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.primary_blue)));
            } else if ("document".equalsIgnoreCase(type)) {
                binding.ivFileIcon.setImageResource(R.drawable.ic_document);
                binding.ivFileIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tag_qa_bg)));
                binding.ivFileIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tag_qa_text)));
            } else {
                binding.ivFileIcon.setImageResource(R.drawable.ic_document);
                binding.ivFileIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tag_frontend_bg)));
                binding.ivFileIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.tag_frontend_text)));
            }

            binding.btnPreview.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPreviewAttachment(attachment);
                }
            });

            binding.btnRemoveAttachment.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onRemoveAttachment(attachment, pos);
                    }
                }
            });
        }
    }
}