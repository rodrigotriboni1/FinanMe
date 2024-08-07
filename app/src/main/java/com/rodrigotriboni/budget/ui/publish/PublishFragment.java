package com.rodrigotriboni.budget.ui.publish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.BankAdapter;
import com.rodrigotriboni.budget.adapters.FileAdapter;
import com.rodrigotriboni.budget.helpers.FileUploadBottomSheetDialog;
import com.rodrigotriboni.budget.models.ModelBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private BankAdapter bankAdapter;

    private final MutableLiveData<List<Uri>> fileList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Uri>> selectedFiles = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ModelBank>> bankList = new MutableLiveData<>(new ArrayList<>());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_publish, container, false);
        root.findViewById(R.id.cvUploadTransactions).setOnClickListener(v -> showBottomSheetDialog());

        RecyclerView rvBankList = root.findViewById(R.id.rvBankList);
        rvBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        bankAdapter = new BankAdapter(getContext(), new ArrayList<>());
        rvBankList.setAdapter(bankAdapter);
        bankList.observe(getViewLifecycleOwner(), bankList -> bankAdapter.updateBankList(bankList));
        fetchBankData();

        return root;
    }

    private void showBottomSheetDialog() {
        FileUploadBottomSheetDialog bottomSheetDialog = new FileUploadBottomSheetDialog(this);
        bottomSheetDialog.show(getChildFragmentManager(), "FileUploadBottomSheetDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            addSelectedFile(fileUri);
        }
    }

    public LiveData<List<Uri>> getSelectedFiles() {
        return selectedFiles;
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

    private void fetchBankData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference expensesRef = database.getReference("expenses");
        DatabaseReference incomesRef = database.getReference("incomes");

        Map<String, ModelBank> bankMap = new HashMap<>();

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot expensesSnapshot) {
                for (DataSnapshot bankSnapshot : expensesSnapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();
                    int totalExpenses = 0;

                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                                Integer amount = expenseSnapshot.child("amount").getValue(Integer.class);
                                if (amount != null && amount >= 0) {
                                    totalExpenses += amount;
                                }
                            }
                        }
                    }

                    ModelBank modelBank = bankMap.getOrDefault(bank, new ModelBank(bank, 0, 0));
                    modelBank.setTotalAmountExpenses(totalExpenses);
                    bankMap.put(bank, modelBank);
                }

                bankList.setValue(new ArrayList<>(bankMap.values()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        incomesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot incomesSnapshot) {
                for (DataSnapshot bankSnapshot : incomesSnapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();
                    int totalIncome = 0;

                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            for (DataSnapshot incomeSnapshot : dateSnapshot.getChildren()) {
                                Integer amount = incomeSnapshot.child("amount").getValue(Integer.class);
                                if (amount != null && amount >= 0) {
                                    totalIncome += amount;
                                }
                            }
                        }
                    }

                    ModelBank modelBank = bankMap.getOrDefault(bank, new ModelBank(bank, 0, 0));
                    modelBank.setTotalAmountIncome(totalIncome);
                    bankMap.put(bank, modelBank);
                }

                bankList.setValue(new ArrayList<>(bankMap.values()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}
