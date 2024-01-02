package com.example.rollbookactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    CardView attendenceCard, notificationCard, scholarshipDetailsCard, bcHostelApplicationCard;
    ImageView profileImg;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    String currentUserId = user.getUid();

    private LocationManager locationManager;
    private Location targetLocation;
    TextView nameUser;
    private static final int RADIUS = 500; // 500 meters


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileImg = findViewById(R.id.profile);
        attendenceCard = findViewById(R.id.attendenceCard);
        notificationCard = findViewById(R.id.notification_card);
        nameUser = findViewById(R.id.userNameMain);
        scholarshipDetailsCard = findViewById(R.id.scholarshipDetails);
        bcHostelApplicationCard = findViewById(R.id.bcHostelApplication);



        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));

            }
        });

        attendenceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AttendActivity.class));
            }
        });

        bcHostelApplicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BcHostelApplicationActivity.class));

            }
        });


        DatabaseReference photo = FirebaseDatabase.getInstance().getReference("profileStudent").child(currentUserId);
        photo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String image = snapshot.child("image").getValue(String.class);

                String name = snapshot.child("firstName").getValue(String.class);

                nameUser.setText(name);

               // Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, currentUserId, Toast.LENGTH_SHORT).show();

                Glide.with(MainActivity.this)
                        .load(image)  // Assuming you have a getImageUrl() method in your Property class that returns the image URL
                        .into(profileImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NotificationActivity.class));


            }
        });

        scholarshipDetailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ScholarshipDetailsActivity.class));
            }
        });











        // Ensure you handle exceptions properly
      /*  try {
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());

            String advertisingId = adInfo.getId(); // This is the Advertising ID
            boolean isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled(); // Indicates if user has opted out of ad tracking

            Toast.makeText(this, advertisingId, Toast.LENGTH_SHORT).show();
            // You can use the advertisingId for analytics or user identification purposes
        } catch (Exception e) {
            e.printStackTrace();

        }


        int sdk = Build.VERSION.SDK_INT;
        Toast.makeText(this, String.valueOf(sdk), Toast.LENGTH_SHORT).show();


        String devicemodel = Build.MODEL;
        Toast.makeText(MainActivity.this, devicemodel, Toast.LENGTH_SHORT).show();*/

    }
}