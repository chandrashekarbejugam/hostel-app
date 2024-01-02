package com.example.rollbookactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password extends AppCompatActivity {

    private EditText resetEmailEditText;
    private Button resetPasswordButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        
        mAuth = FirebaseAuth.getInstance();
        resetEmailEditText = findViewById(R.id.resetEmailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetEmailEditText.getText().toString().trim();
                
                if(TextUtils.isEmpty(email)) {
                    resetEmailEditText.setError("Enter your email");
                }else{
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Forget_Password.this, "Reset email sent successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(Forget_Password.this, "Failed to send reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}