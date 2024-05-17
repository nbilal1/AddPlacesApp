package com.moutamid.addplacesapp.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.moutamid.addplacesapp.Helper.Config;
import com.moutamid.addplacesapp.R;
import com.moutamid.addplacesapp.model.LocationModel;
import com.squareup.picasso.Picasso;
public class AddPlacesActivity extends AppCompatActivity {
    public static final int GALARY_PICK = 1;

    private EditText name, productDescription;
    private Button add, choose;
    private ImageView img;
    private Uri imgUri;
    private StorageReference mStorageRef;
    private Spinner spinner;
    private StorageTask mUploadTask;
    String category;
    TextView name_cat, editTextLat, editTextLng, add_lat_lng;
    private String[] categories = {
            "Hotel",
            "Restaurant",
            "Park",
            "Shopping",
            "Entertainment",
            "Travel",
            "Education",
            "Healthcare",
            "Beauty",
            "Fitness",
            "Services"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        add_lat_lng = findViewById(R.id.add_lat_lng);
        editTextLat = findViewById(R.id.editTextLat);
        editTextLng = findViewById(R.id.editTextLng);
        name = findViewById(R.id.editTextName);
        name_cat = findViewById(R.id.name);
        add = (Button) findViewById(R.id.btnAddCatogry);
        choose = (Button) findViewById(R.id.btnChooseCatogryImage);
        img = (ImageView) findViewById(R.id.CatogryImage);
        productDescription = findViewById(R.id.editTextDescription);
        spinner = (Spinner) findViewById(R.id.spinner);
        if (Config.lat == 0.0 && Config.lng == 0.0) {
            editTextLng.setVisibility(View.GONE);
            editTextLat.setVisibility(View.GONE);
        } else {

            editTextLng.setVisibility(View.VISIBLE);
            editTextLat.setVisibility(View.VISIBLE);
        }
        // Setup Spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        add_lat_lng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddPlacesActivity.this, MapActivity.class));
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference().child("AddPlacesApp").child("Places");


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress())
                    show_toast("Upload is in progress", 1);
                else if (name.getText().toString().isEmpty() || productDescription.getText().toString().isEmpty() || imgUri == null) {
                    Toast.makeText(AddPlacesActivity.this, "Please fill blank fields", Toast.LENGTH_SHORT).show();

                } else {
                    uploadData();
//                    Toast.makeText(AddPlacesActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void uploadData() {
        if (Config.lat == 0.0 || name.getText().toString().isEmpty() || productDescription.getText().toString().isEmpty() || imgUri == null) {
            Toast.makeText(AddPlacesActivity.this, "Please fill blank fields and locations", Toast.LENGTH_SHORT).show();
        } else {
            uploadImage();
        }
    }

    public void uploadImage() {

        if (imgUri != null) {
            StorageReference fileReference = mStorageRef.child(name.getText().toString() + "." + getFileExtension(imgUri));
            mUploadTask = fileReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    DatabaseReference z = FirebaseDatabase.getInstance().getReference().child("AddPlacesApp")
                            .child("Places");
                    String key = z.push().getKey().toString();
                    LocationModel product = new LocationModel(name.getText().toString().trim(),
                            productDescription.getText().toString().trim(),
                            downloadUrl.toString(), category, editTextLat.getText().toString().trim(), editTextLng.getText().toString().trim(), key);
                    z.child(key).setValue(product);
                    Config.lat = 0.0;
                    Config.lng = 0.0;
                    startActivity(new Intent(AddPlacesActivity.this, HomePage.class));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Config.lat = 0.0;
                    Config.lng = 0.0;
                    Toast.makeText(AddPlacesActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void openImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, GALARY_PICK);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALARY_PICK && resultCode == Activity.RESULT_OK && data.getData() != null && data != null) {
            imgUri = data.getData();
            try {
                Picasso.get().load(imgUri).fit().centerCrop().into(img);
            } catch (Exception e) {
                Log.e(this.toString(), e.getMessage().toString());
            }
        }
    }


    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.lat == 0.0 && Config.lng == 0.0) {
            editTextLng.setVisibility(View.GONE);
            editTextLat.setVisibility(View.GONE);
        } else {

            editTextLng.setVisibility(View.VISIBLE);
            editTextLat.setVisibility(View.VISIBLE);
            editTextLng.setText(String.valueOf(Config.lng));
            editTextLat.setText(String.valueOf(Config.lat));

        }
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