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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.BankAdapter;
import com.rodrigotriboni.budget.adapters.FileAdapter;
import com.rodrigotriboni.budget.helpers.FileUploadBottomSheetDialog;
import com.rodrigotriboni.budget.helpers.SharedViewModel;
import com.rodrigotriboni.budget.models.ModelBank;

import java.util.ArrayList;


public class PublishFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private PublishViewModel publishViewModel;
    private FileAdapter fileAdapter;
    private BankAdapter bankAdapter;
    private SharedViewModel sharedViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_publish, container, false);
        root.findViewById(R.id.cvUploadTransactions).setOnClickListener(v -> showBottomSheetDialog());

        publishViewModel = new ViewModelProvider(this).get(PublishViewModel.class);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Setup FileAdapter
        RecyclerView rvFileList = root.findViewById(R.id.rvFileList);
        rvFileList.setLayoutManager(new LinearLayoutManager(getContext()));
        fileAdapter = new FileAdapter(new ArrayList<>());
        rvFileList.setAdapter(fileAdapter);

        // Setup BankAdapter
        RecyclerView rvBankList = root.findViewById(R.id.rvBankList);
        rvBankList.setLayoutManager(new LinearLayoutManager(getContext()));
        bankAdapter = new BankAdapter(new ArrayList<>());
        rvBankList.setAdapter(bankAdapter);
        publishViewModel.getFileList().observe(getViewLifecycleOwner(), fileList -> {
            fileAdapter.updateFileList(fileList);
        });

        publishViewModel.getBankList().observe(getViewLifecycleOwner(), bankList -> {
            bankAdapter.updateBankList(bankList);
        });

        return root;
    }



    private void showBottomSheetDialog() {
        FileUploadBottomSheetDialog bottomSheetDialog = new FileUploadBottomSheetDialog(publishViewModel);
        bottomSheetDialog.show(getChildFragmentManager(), "FileUploadBottomSheetDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            publishViewModel.addSelectedFile(fileUri);
        }
    }
}