package com.moutamid.addplacesapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.RatingModel;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<RatingModel> feedbackList;

    public FeedbackAdapter(List<RatingModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new FeedbackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        RatingModel feedback = feedbackList.get(position);
        holder.nameTextView.setText(feedback.name);
      holder.ratingBar.setRating(Float.parseFloat(feedback.rating));
        holder.feedbackTextView.setText(feedback.feedback);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ScaleRatingBar ratingBar;
        public TextView feedbackTextView;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            feedbackTextView = itemView.findViewById(R.id.feedbackTextView);
        }
    }
}
