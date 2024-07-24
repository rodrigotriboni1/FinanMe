package com.rodrigotriboni.budget.ui.publish;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PublishViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> fileList;

    public PublishViewModel() {
        fileList = new MutableLiveData<>(new ArrayList<>());
        fetchStoredFiles();
    }

    public LiveData<List<Uri>> getFileList() {
        return fileList;
    }

    public void addFile(Uri fileUri) {
        List<Uri> currentList = fileList.getValue();
        if (currentList != null) {
            currentList.add(fileUri);
            fileList.setValue(currentList);
        }
    }

    private void fetchStoredFiles() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("uploads");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<Uri> files = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    files.add(uri);
                    fileList.setValue(files);
                });
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }

    public void setText(String text) {
        // Add logic for text management if needed
    }
}
