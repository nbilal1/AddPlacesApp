package com.moutamid.addplacesapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.addplacesapp.Adapter.FeedbackAdapter;
import com.moutamid.addplacesapp.Dailogue.FeedBackDialogClass;
import com.moutamid.addplacesapp.Dailogue.RateDialogClass;
import com.moutamid.addplacesapp.Helper.Config;
import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.LocationModel;
import com.moutamid.addplacesapp.model.RatingModel;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    LocationModel locationModel;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ImageView placeImage = findViewById(R.id.place_image);
        TextView placeName = findViewById(R.id.place_name);
        TextView placeCategory = findViewById(R.id.place_category);
        TextView placeDetails = findViewById(R.id.place_details);
        TextView review_text = findViewById(R.id.review_text);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationModel = (LocationModel) Stash.getObject("currentLocationModel", LocationModel.class);
        List<RatingModel> feedbackList = new ArrayList<>();

        FeedbackAdapter adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);
        // Set data to views
        Glide.with(this).load(locationModel.getImage()).into(placeImage);
        placeName.setText(locationModel.getName());
        placeCategory.setText(locationModel.getCategory());
        placeDetails.setText(locationModel.getDetails());
        // Query the database to retrieve the data
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("AddPlacesApp").child("Places")
                .child(locationModel.getKey()).child("Rating");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    String name = placeSnapshot.child("name").getValue(String.class);
                    String rating = placeSnapshot.child("rating").getValue(String.class);
                    String feedback = placeSnapshot.child("feedback").getValue(String.class);
                    Log.d("rating", name + "  " + rating + "  " + feedback);
                    if (feedback != null) {
                        feedbackList.add(new RatingModel(name, rating, feedback));
                    } else {
                        feedbackList.add(new RatingModel(name, rating, ""));
                    }
                }
                if (feedbackList.size() < 1) {
                    review_text.setVisibility(View.GONE);
                } else {
                    review_text.setVisibility(View.VISIBLE);
                }
                FeedbackAdapter adapter = new FeedbackAdapter(feedbackList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    public void rating(View view) {
        RateDialogClass cdd = new RateDialogClass(DetailsActivity.this, locationModel.getName(), locationModel.getKey());
        cdd.show();
    }

    public void feedback(View view) {
        FeedBackDialogClass cdd = new FeedBackDialogClass(DetailsActivity.this, locationModel.getName(), locationModel.getKey());
        cdd.show();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void navigate(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+ Config.current_lat+","+Config.current_lng+"&daddr=" + locationModel.getLat() + "," + locationModel.getLng()));
        startActivity(intent);
    }
}
