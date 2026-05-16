package com.vocedelposto.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vocedelposto.app.R;
import com.vocedelposto.app.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<Tag> allTags;
    private Set<Long> selectedTagIds = new HashSet<>();

    public TagsAdapter(List<Tag> allTags, List<Tag> userTags) {
        this.allTags = allTags;
        for (Tag tag : userTags) {
            selectedTagIds.add(tag.getId());
        }
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = allTags.get(position);
        holder.tvName.setText(tag.getName());
        holder.tvCategory.setText(tag.getCategory() != null ? tag.getCategory() : "");
        holder.checkbox.setChecked(selectedTagIds.contains(tag.getId()));

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTagIds.add(tag.getId());
            } else {
                selectedTagIds.remove(tag.getId());
            }
        });

        holder.itemView.setOnClickListener(v ->
                holder.checkbox.setChecked(!holder.checkbox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return allTags.size();
    }

    public List<Tag> getSelectedTags() {
        List<Tag> selected = new ArrayList<>();
        for (Tag tag : allTags) {
            if (selectedTagIds.contains(tag.getId())) {
                selected.add(tag);
            }
        }
        return selected;
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView tvName, tvCategory;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkboxTag);
            tvName = itemView.findViewById(R.id.tvTagName);
            tvCategory = itemView.findViewById(R.id.tvTagCategory);
        }
    }
}