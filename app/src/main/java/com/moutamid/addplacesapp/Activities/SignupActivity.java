package com.moutamid.addplacesapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.UserModel;
import java.util.Objects;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputConfirmPassword;
    private Button btnSignUp;
    private TextView btnSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        LinearLayout main_layout = findViewById(R.id.main_layout);
        main_layout.setAnimation(bottomAnim);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        inputName = (EditText) findViewById(R.id.name);
        inputConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        btnSignIn = (TextView) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String confirm_password = inputConfirmPassword.getText().toString().trim();
                Stash.put("name", name);
                if (TextUtils.isEmpty(name)) {
                    show_toast("Enter name", 0);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    show_toast("Enter email address!", 0);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    show_toast("Enter password!", 0);
                    return;
                }
                if (TextUtils.isEmpty(confirm_password)) {
                    show_toast("Enter confirm password!", 0);
                    return;
                }


                if (password.length() < 6) {
                    show_toast("Password too short, enter minimum 6 characters!", 0);
                    return;
                }
                if (!password.equals(confirm_password)) {
                    show_toast("Password is not matched", 0);
                    inputConfirmPassword.setText("");
                    inputPassword.setText("");
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);
                Dialog lodingbar = new Dialog(SignupActivity.this);
                lodingbar.setContentView(R.layout.loading);
                Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
                lodingbar.setCancelable(false);
                lodingbar.show();

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    show_toast("Authentication failed." + task.getException(), 0);
                                } else {
                                    UserModel userModel = new UserModel();
                                    userModel.email = email;
                                    userModel.name = name;
                                    FirebaseDatabase.getInstance().getReference().child("AddPlacesApp").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            show_toast("Account is created successfully", 1);
                                            startActivity(new Intent(SignupActivity.this, HomePage.class));
                                            lodingbar.dismiss();
                                            finishAffinity();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            lodingbar.dismiss();
                                            show_toast("Something went wrong. Please try again", 0);
                                        }
                                    });

                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void back(View view) {
        onBackPressed();
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

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
