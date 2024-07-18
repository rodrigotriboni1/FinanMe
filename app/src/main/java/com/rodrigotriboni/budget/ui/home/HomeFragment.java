package com.rodrigotriboni.budget.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.CreditCardAdapter;
import com.rodrigotriboni.budget.databinding.FragmentHomeBinding;
import com.rodrigotriboni.budget.models.CreditCard;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Configure o ViewPager2 e o CircleIndicator
        final ViewPager2 viewPager = binding.viewPager;
        final CircleIndicator3 circleIndicator = binding.circleIndicator;

        homeViewModel.getCreditCardList().observe(getViewLifecycleOwner(), new Observer<List<CreditCard>>() {
            @Override
            public void onChanged(List<CreditCard> creditCards) {
                CreditCardAdapter adapter = new CreditCardAdapter(creditCards);
                viewPager.setAdapter(adapter);
                circleIndicator.setViewPager(viewPager);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
