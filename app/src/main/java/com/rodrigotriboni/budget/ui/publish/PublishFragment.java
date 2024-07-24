package com.rodrigotriboni.budget.ui.publish;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.FileAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublishFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private PublishViewModel publishViewModel;
    private FileAdapter fileAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_publish, container, false);
        root.findViewById(R.id.cvUploadTransactions).setOnClickListener(v -> showBottomSheetDialog());

        publishViewModel = new ViewModelProvider(this).get(PublishViewModel.class);
        RecyclerView rvFileList = root.findViewById(R.id.rvFileList);
        rvFileList.setLayoutManager(new LinearLayoutManager(getContext()));
        fileAdapter = new FileAdapter(new ArrayList<>());
        rvFileList.setAdapter(fileAdapter);

        publishViewModel.getFileList().observe(getViewLifecycleOwner(), fileList -> {
            fileAdapter.updateFileList(fileList);
        });
        return root;
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.btnSelectFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Escolha um arquivo PDF"), PICK_FILE_REQUEST);
        });

        bottomSheetView.findViewById(R.id.btnUploadFile).setOnClickListener(v -> {
            if (fileUri != null) {
                uploadFileToFirebase(fileUri);
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            fileUri = data.getData();
            publishViewModel.addFile(fileUri);
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("uploads/" + UUID.randomUUID().toString());

        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                extractTextFromPdf(fileUri);
            }).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", "Failed to get download URL", e);
            });
        }).addOnFailureListener(e -> {
            Log.e("FirebaseStorage", "Failed to upload file", e);
        });
    }

    private void extractTextFromPdf(Uri fileUri) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                PdfReader reader = new PdfReader(inputStream);
                StringBuilder extractedText = new StringBuilder();
                int n = reader.getNumberOfPages();
                for (int i = 0; i < n; i++) {
                    extractedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n");
                }
                reader.close();
                modelCall(extractedText.toString());
                saveTextToDatabase(extractedText.toString());
            } else {
                Log.e("Rodrigo", "Failed to open InputStream");
            }
        } catch (IOException e) {
            Log.e("Rodrigo", "Error extracting text from PDF", e);
        }
    }

    private void saveTextToDatabase(String text) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("pdf_texts");
        String key = databaseRef.push().getKey();

        if (key != null) {
            databaseRef.child(key).setValue(text)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDatabase", "Text saved successfully"))
                    .addOnFailureListener(e -> Log.e("FirebaseDatabase", "Failed to save text", e));
        }
    }

    public void modelCall(String extractedText) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDLJn689D47C1mFzfVzYW8eBquDxLtPfHc");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(extractedText)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(@NonNull GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }
}
