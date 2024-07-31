package com.rodrigotriboni.budget.ui.dashboard;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> fileList;

    public DashboardViewModel() {
        fileList = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<Uri>> getFileList() {
        return fileList;
    }
}
