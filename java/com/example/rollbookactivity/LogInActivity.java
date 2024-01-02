package com.example.rollbookactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    TextView register, forgotpass;
    LinearLayout login;
    FirebaseAuth mAuth;
    EditText email, password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);
        register = findViewById(R.id.sigUpText);
        forgotpass = findViewById(R.id.forgetPasswordId);
        email = findViewById(R.id.loginEmailId);
        password = findViewById(R.id.loginPasswordId);
        login = findViewById(R.id.logInBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, Forget_Password.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });

    }

    public void verifyDetails() {
        String emaill = email.getText().toString();
        String passwordl = password.getText().toString();
        if (emaill.isEmpty() || !emaill.contains("@gmail.com")) {
            email.setError("Invalid email!");
        } else if (passwordl.isEmpty()) {
            password.setError("Password can't be empty!");
        } else {
            siginiUser();
        }
    }

    public void siginiUser() {
        String emaill = email.getText().toString();
        String passwordl = password.getText().toString();
        mAuth.signInWithEmailAndPassword(emaill, passwordl).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle login failure and show an error message
                    Toast.makeText(LogInActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}