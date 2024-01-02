package com.example.rollbookactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ImageView backBtnProfile, profilePic;

    TextView nameProfile, joiningCode;

    CardView logout;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();


    DatabaseReference batchesRef = FirebaseDatabase.getInstance().getReference("batches");

    String currentUserId = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backBtnProfile = findViewById(R.id.backBtnProfileID);

        profilePic = findViewById(R.id.userProfilePic);

        nameProfile = findViewById(R.id.userNameProfile);

        joiningCode = findViewById(R.id.joining_Code);

        logout = findViewById(R.id.logOut);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(ProfileActivity.this, LogInActivity.class));
                finish();
            }
        });

        batchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String areas = snapshot.child(currentUserId).child("hostel").getValue(String.class);

                joiningCode.setText(areas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference photo = FirebaseDatabase.getInstance().getReference("profileStudent").child(currentUserId);
        photo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue(String.class);
                String name = snapshot.child("firstName").getValue(String.class);

                nameProfile.setText(name);

                Glide.with(ProfileActivity.this)
                        .load(image)  // Assuming you have a getImageUrl() method in your Property class that returns the image URL
                        .into(profilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}