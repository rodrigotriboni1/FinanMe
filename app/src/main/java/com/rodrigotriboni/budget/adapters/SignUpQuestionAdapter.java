package com.rodrigotriboni.budget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.models.ModelSignUpQuestion;
import com.rodrigotriboni.budget.ui.signup.SignUpViewModel;

import java.util.List;

public class SignUpQuestionAdapter extends RecyclerView.Adapter<SignUpQuestionAdapter.ViewHolder> {

    private final List<ModelSignUpQuestion> questions;
    private final SignUpViewModel viewModel;
    private final String key;

    public SignUpQuestionAdapter(List<ModelSignUpQuestion> questions, SignUpViewModel viewModel, String key) {
        this.questions = questions;
        this.viewModel = viewModel;
        this.key = key;

        viewModel.getSelectedPosition(key).observeForever(position -> notifyDataSetChanged());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_question_sign_up, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelSignUpQuestion question = questions.get(position);
        holder.questionTextView.setText(question.getQuestionText());
        holder.iconImageView.setImageDrawable(question.getIcon());

        Integer selectedPosition = viewModel.getSelectedPosition(key).getValue();

        if (selectedPosition != null && selectedPosition == position) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.light_purple_dark));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.grey_light));
        }

        holder.itemView.setOnClickListener(v -> {
            viewModel.setSelectedPosition(key, position);

            viewModel.addResponse(questions.get(position).getQuestionText());
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView questionTextView;
        public final ImageView iconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text);
            iconImageView = itemView.findViewById(R.id.question_icon);
        }
    }
}