package com.rodrigotriboni.budget.helpers;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.rodrigotriboni.budget.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SpinnerUtil {

    public static void setupMonthSpinner(Spinner monthSpinner, Context context, ViewModelStoreOwner owner) {
        Calendar calendar = Calendar.getInstance();
        SharedViewModel sharedViewModel = new ViewModelProvider(owner).get(SharedViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar.set(Calendar.MONTH, position);
                updateCurrentMonthText(view, calendar);

                sharedViewModel.setSelectedMonth(position);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private static void updateCurrentMonthText(View view, Calendar calendar) {
        if (view instanceof TextView) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
            String currentMonth = sdf.format(calendar.getTime());
            ((TextView) view).setText(currentMonth);
            ((TextView) view).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((TextView) view).setText(currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1));
        }
    }
}
