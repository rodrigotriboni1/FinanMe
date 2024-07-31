package com.rodrigotriboni.budget.ui.publish;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rodrigotriboni.budget.models.ModelBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> fileList;
    private final MutableLiveData<List<Uri>> selectedFiles;
    private final MutableLiveData<List<ModelBank>> bankList;

    public PublishViewModel() {
        fileList = new MutableLiveData<>(new ArrayList<>());
        selectedFiles = new MutableLiveData<>(new ArrayList<>());
        bankList = new MutableLiveData<>(new ArrayList<>());
        fetchStoredFiles();
        fetchBankDataExpenses();
        fetchBankDataIncomes();
    }

    

    public LiveData<List<Uri>> getFileList() {
        return fileList;
    }

    public LiveData<List<Uri>> getSelectedFiles() {
        return selectedFiles;
    }

    public LiveData<List<ModelBank>> getBankList() {
        return bankList;
    }

    public void addFile(Uri fileUri) {
        List<Uri> currentList = fileList.getValue();
        if (currentList != null) {
            currentList.add(fileUri);
            fileList.setValue(currentList);
        }
    }

    public void addSelectedFile(Uri fileUri) {
        List<Uri> currentList = selectedFiles.getValue();
        if (currentList != null) {
            currentList.add(fileUri);
            selectedFiles.setValue(currentList);
        }
    }

    public void clearSelectedFiles() {
        selectedFiles.setValue(new ArrayList<>());
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
        });
    }

    private void fetchBankDataExpenses() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference expensesRef = database.getReference("expenses");
        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot expensesSnapshot) {
                Map<String, Integer> bankTotals = new HashMap<>();
                for (DataSnapshot bankSnapshot : expensesSnapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();
                    int total = 0;

                    // Iterate through all categories for the bank
                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        // Iterate through all dates for the category
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            // Iterate through all expense entries for the date
                            for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                                Integer amount = expenseSnapshot.child("amount").getValue(Integer.class);
                                if (amount != null && amount >= 0) { // Only add positive amounts
                                    total += amount;
                                }
                            }
                        }
                    }
                    bankTotals.put(bank, total);
                }
                List<ModelBank> banks = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : bankTotals.entrySet()) {
                    banks.add(new ModelBank(entry.getKey(), entry.getValue()));
                }
                bankList.setValue(banks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
    private void fetchBankDataIncomes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference incomesRef = database.getReference("incomes");
        incomesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot incomesSnapshot) {
                Map<String, Integer> bankTotals = new HashMap<>();

                // Iterate through all banks under "incomes"
                for (DataSnapshot bankSnapshot : incomesSnapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();
                    int total = 0;

                    // Iterate through all categories for the bank
                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        // Iterate through all dates for the category
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            // Iterate through all income entries for the date
                            for (DataSnapshot incomeSnapshot : dateSnapshot.getChildren()) {
                                Integer amount = incomeSnapshot.child("amount").getValue(Integer.class);
                                if (amount != null && amount >= 0) { // Only add positive amounts
                                    total += amount;
                                }
                            }
                        }
                    }
                    bankTotals.put(bank, total);
                }

                List<ModelBank> banks = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : bankTotals.entrySet()) {
                    banks.add(new ModelBank(entry.getKey(), entry.getValue()));
                }
                bankList.setValue(banks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
