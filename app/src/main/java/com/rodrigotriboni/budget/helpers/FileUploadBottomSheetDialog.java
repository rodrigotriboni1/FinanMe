package com.rodrigotriboni.budget.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.FileAdapter;
import com.rodrigotriboni.budget.pojos.ResponseCallback;
import com.rodrigotriboni.budget.ui.publish.PublishFragment;
import com.rodrigotriboni.budget.analyzer.GeminiFlash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUploadBottomSheetDialog extends BottomSheetDialogFragment {

    private final PublishFragment publishFragment;

    private static final int PICK_FILE_REQUEST = 1;
    private GeminiFlash geminiFlash;
    private Context context;

    public FileUploadBottomSheetDialog(PublishFragment fragment) {
        this.publishFragment = fragment;
        this.geminiFlash = new GeminiFlash();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FileAdapter selectedFileAdapter;
        View view = inflater.inflate(R.layout.bottom_sheet_upload, container, false);

        RecyclerView rvSelectedFiles = view.findViewById(R.id.rvFileListSelected);
        rvSelectedFiles.setLayoutManager(new LinearLayoutManager(context));
        selectedFileAdapter = new FileAdapter(new ArrayList<>());
        rvSelectedFiles.setAdapter(selectedFileAdapter);

        publishFragment.getSelectedFiles().observe(getViewLifecycleOwner(), selectedFiles -> {
            selectedFileAdapter.updateFileList(selectedFiles);
            Log.d("FileUploadBottomSheet", "Selected files updated: " + selectedFiles.size() + " files.");
        });

        view.findViewById(R.id.btnSelectFile).setOnClickListener(v -> {
            Log.d("FileUploadBottomSheet", "Select file button clicked.");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Escolha um arquivo PDF"), PICK_FILE_REQUEST);
        });

        view.findViewById(R.id.btnUploadFile).setOnClickListener(v -> {
            Log.d("FileUploadBottomSheet", "Upload file button clicked.");
            List<Uri> selectedFiles = publishFragment.getSelectedFiles().getValue();
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                for (Uri uri : selectedFiles) {
                    Log.d("FileUploadBottomSheet", "Uploading file: " + uri.toString());
                    uploadFileToFirebase(uri);
                }
                publishFragment.clearSelectedFiles();
            } else {
                Log.d("FileUploadBottomSheet", "No files selected for upload.");
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                Log.d("FileUploadBottomSheet", "File selected: " + fileUri.toString());
                publishFragment.addSelectedFile(fileUri);
            } else {
                Log.d("FileUploadBottomSheet", "File selection failed.");
            }
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("uploads/" + UUID.randomUUID().toString());
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("FirebaseStorage", "File uploaded successfully.");
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUrl -> {
                                Log.d("FirebaseStorage", "Download URL: " + downloadUrl.toString());
                                extractTextFromPdf(fileUri, fileRef);
                                publishFragment.addFile(fileUri);
                            })
                            .addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to get download URL", e));
                })
                .addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to upload file", e));
    }

    private void extractTextFromPdf(Uri fileUri, StorageReference fileRef) {
        Log.d("Rodrigo", "Starting text extraction from PDF.");
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                PdfReader reader = new PdfReader(inputStream);
                StringBuilder extractedText = new StringBuilder();
                int n = reader.getNumberOfPages();
                for (int i = 0; i < n; i++) {
                    extractedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n");
                }
                reader.close();
                Log.d("Rodrigo", "Text extraction completed.");
                geminiFlash.getResponse(extractedText.toString(), new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GeminiFlash", "Response: " + response);
                        deleteUploadedFile(fileRef);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("GeminiFlash", "Error: ", throwable);
                        deleteUploadedFile(fileRef);
                    }
                });
            } else {
                Log.e("Rodrigo", "Failed to open InputStream");
            }
        } catch (IOException e) {
            Log.e("Rodrigo", "Error extracting text from PDF", e);
        }
    }

    private void deleteUploadedFile(StorageReference fileRef) {
        fileRef.delete()
                .addOnSuccessListener(aVoid -> Log.d("FirebaseStorage", "File deleted successfully."))
                .addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to delete file", e));
    }
}
