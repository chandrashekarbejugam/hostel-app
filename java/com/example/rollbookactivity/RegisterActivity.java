package com.example.rollbookactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText nameEditText, emailEditText, passwordEditText, conformPassword;
    LinearLayout submitButton;
    TextView loginTextView;
    Spinner spinner;
    double latitude = 0.0, longitude = 0.0;
    String area = "yourArea";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.userName);
        emailEditText = findViewById(R.id.userEmail);
        passwordEditText = findViewById(R.id.userPassword);
        conformPassword = findViewById(R.id.userConformPassword);
        submitButton = findViewById(R.id.submitBtn);
        loginTextView = findViewById(R.id.Login_text);
        spinner = (Spinner) findViewById(R.id.locationSpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LocationList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                switch (selectedItem) {
                    case "Medipally":
                        latitude = 17.4047500;
                        longitude = 78.5981259;
                        area = "Medipally";
                        break;
                    case "LB nagar":
                        latitude = 17.3550559;
                        longitude =  78.5539201;
                        area = "LB nagar";
                        break;
                    case "Gatkesar":
                        latitude = 17.4521779;
                        longitude = 78.6812503;
                        area = "Gatkesar";
                        break;
                    case "Agraharam":
                        latitude = 18.4312413;
                        longitude = 78.8320679;
                        area = "Agraharam";
                        break;
                    case "Vemulawada":
                        latitude = 18.4575927;
                        longitude = 78.8632643;
                        area = "Vemulawada";
                        break;
                    case "Alugunur":
                        latitude = 18.4059170;
                        longitude = 79.1446760;
                        area = "Alugunur";
                        break;
                    case "Nadergul":
                        latitude = 17.281524498107917;
                        longitude = 78.53669512297319;
                        area = "Nadergul";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDetails();

            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
                finish();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();
        userRef = rootRef.child("userDetails");

    }

    public void validateDetails() {
        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String conform = conformPassword.getText().toString();
        String selectedLocation = spinner.getSelectedItem().toString(); // Get the selected spinner item

        if (name.isEmpty()) {
            nameEditText.setError("Enter name");
        } else if (email.isEmpty()) {
            emailEditText.setError("Enter Email id");
        } else if (!email.contains("@gmail.com")) {
            emailEditText.setError("Enter a valid Email");
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must contain at least 6 characters!");
        } else if (!conform.equals(password)) {
            conformPassword.setError("Password doesn't match");
        }else {
            registerUser(email, password);
        }
    }

    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    uploadDetails();
                    startActivity(new Intent(RegisterActivity.this,DetailsActivity.class));
                    //finish();
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadDetails() {
        String namel = nameEditText.getText().toString();
        String emaill = emailEditText.getText().toString();
        String passwordl = passwordEditText.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();
        String userUid = user.getUid();
        RegisterDetails registerDetails = new RegisterDetails(namel, emaill, passwordl, userUid, latitude, longitude);
        DatabaseReference userDetails = database.getReference("userDetails");
        userDetails.child(area).child(userUid).setValue(registerDetails);

        DatabaseReference locationRef = database.getReference("locations");
        locationRef.child(userUid).child("latitude").setValue(latitude);
        locationRef.child(userUid).child("longitude").setValue(longitude);

        DatabaseReference batchRef = database.getReference("batches");
        batchRef.child(String.valueOf(mAuth.getUid())).child("hostel").setValue(area);

    }
}
