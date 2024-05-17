package com.moutamid.addplacesapp.Dailogue;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.RatingModel;

import java.util.HashMap;
import java.util.Objects;


public class RateDialogClass extends Dialog implements
        View.OnClickListener {
    String name;
    String key;
    public Activity c;
    public RateDialogClass d;
    public TextView rate;
    RatingBar simpleRatingBar;
    ImageView close_btn;

    public RateDialogClass(Activity a, String name, String key) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.name = name;
        this.key = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.rate_us);
        rate = (TextView) findViewById(R.id.startbtn);
        close_btn = (ImageView) findViewById(R.id.close_btn);
        simpleRatingBar = (RatingBar) findViewById(R.id.simpleRatingBar);
        d = new RateDialogClass(c, name, key);
        rate.setOnClickListener(this);
        close_btn.setOnClickListener(this);

        d.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.startbtn) {
            Dialog lodingbar = new Dialog(c);
            lodingbar.setContentView(R.layout.loading);
            Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
            lodingbar.setCancelable(false);
            lodingbar.show();
            float rating1 = simpleRatingBar.getRating();
            if (rating1 == 0.0) {
                lodingbar.dismiss();
                Toast.makeText(c, "Please add some rating before Submit", Toast.LENGTH_SHORT).show();

            } else {
                String name1 = Stash.getString("name");
                RatingModel ratingModel = new RatingModel();
                ratingModel.rating = String.valueOf(rating1);
                ratingModel.name = name;

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap<String, Object> ratingMap = new HashMap<>();
                ratingMap.put("rating", ratingModel.rating);
                ratingMap.put("name", name1);

                FirebaseDatabase.getInstance().getReference()
                        .child("AddPlacesApp")
                        .child("Places")
                        .child(key)
                        .child("Rating")
                        .child(currentUserUid)
                        .updateChildren(ratingMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                show_toast("Rating is submitted successfully", 1);
                                lodingbar.dismiss();
                                dismiss();
                                c.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                lodingbar.dismiss();
                                dismiss();
                                show_toast("Something went wrong. Please try again", 0);
                                c.finish();
                            }
                        });

            }
        } else if (id == R.id.close_btn) {
            dismiss();
        }
        dismiss();
    }

    public void show_toast(String message, int type) {
        LayoutInflater inflater = getLayoutInflater();

        View layout;
        if (type == 0) {
            layout = inflater.inflate(R.layout.toast_wrong,
                    (ViewGroup) findViewById(R.id.toast_layout_root));
        } else {
            layout = inflater.inflate(R.layout.toast_right,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

        }
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(c);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}