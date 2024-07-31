package com.rodrigotriboni.budget.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;

import java.util.List;

public class UploadFileAdapter extends RecyclerView.Adapter<UploadFileAdapter.FileViewHolder> {

    private List<Uri> fileList;
    public UploadFileAdapter(List<Uri> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pdf_upload, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Uri fileUri = fileList.get(position);
        String fileName = fileUri.getLastPathSegment();
        holder.tvFileName.setText(fileName);}
    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void updateFileList(List<Uri> newFileList) {
        this.fileList = newFileList;
        notifyDataSetChanged();
    }


    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
        }
    }
}
