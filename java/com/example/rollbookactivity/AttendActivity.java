package com.example.rollbookactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AttendActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private TextView  nearTextView;

    LinearLayout awayView, submitButtom;

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    String formattedDate = dateFormat.format(date);

    DatabaseReference batchesRef = FirebaseDatabase.getInstance().getReference("batches");


    Double latFetched, longFetched;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String uid = user.getUid();

    ProgressBar fetch, verify;

    RelativeLayout fLayout, vLayout;

    DatabaseReference attendedRef = FirebaseDatabase.getInstance().getReference("attended");

    ImageView tickFetch, tickVerify,backArrow;

    String nameUser;

    private LocationManager locationManager;
    private LocationListener locationListener;


    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    String currentTime = String.format("%02d:%02d", hour, minute);

    String area;

    private boolean isLocationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float TARGET_RADIUS = 450; // Define the radius within which the user is considered to be in the location (in meters)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);

        submitButtom = findViewById(R.id.submitBtn);
        checkBox = findViewById(R.id.checkboxId);
        awayView = findViewById(R.id.awayToLoction);
        nearTextView = findViewById(R.id.nearToLoction);
        fetch = findViewById(R.id.fetchProgress);
        verify = findViewById(R.id.verifyProgress);
        tickFetch = findViewById(R.id.fetchTick);
        tickVerify = findViewById(R.id.verifyTick);
        fLayout = findViewById(R.id.fetchLayout);
        vLayout = findViewById(R.id.verifyLayout);
        backArrow = findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });

        submitButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setError(null);

                    fetchData();
                    fLayout.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setError("Please select the checkbox");
                }
            }
        });

        DatabaseReference photo = FirebaseDatabase.getInstance().getReference("profileStudent").child(uid);
        photo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("firstName").getValue(String.class);

                nameUser = name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void fetchData(){


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference("locations");
        DatabaseReference finalRef = locationRef.child(uid);
        finalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);

                latFetched = latitude;
                longFetched = longitude;
                fetch.setVisibility(View.GONE);
                tickFetch.setVisibility(View.VISIBLE);
                vLayout.setVisibility(View.VISIBLE);
                verifyLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   private void verifyLocation() {
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           isLocationPermissionGranted = true;
           locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
           locationListener = new LocationListener() {
               @Override
               public void onLocationChanged(Location location) {
                   double userLatitude = location.getLatitude();
                   double userLongitude = location.getLongitude();

                   Location targetLocation = new Location("RollBook");
                   targetLocation.setLatitude(latFetched);   // Replace TARGET_LATITUDE with the desired latitude
                   targetLocation.setLongitude(longFetched);  // Replace TARGET_LONGITUDE with the desired longitude
                   verify.setVisibility(View.GONE);
                   tickVerify.setVisibility(View.VISIBLE);
                   float distance = location.distanceTo(targetLocation);

                   // Check if it's within the specified time slots
                   Calendar calendar = Calendar.getInstance();
                   int hour = calendar.get(Calendar.HOUR_OF_DAY);

                   if ((hour >= 7 && hour < 9) || (hour >= 19 && hour < 22)) {
                       if (distance <= TARGET_RADIUS) {
                           nearTextView.setVisibility(View.VISIBLE);
                           awayView.setVisibility(View.GONE);

                           batchesRef.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   String areas = snapshot.child(uid).child("hostel").getValue(String.class);

                                   //area = areas;
                                   attendedRef.child(areas).child(formattedDate).child(currentTime).setValue(nameUser);
                                   Toast.makeText(AttendActivity.this, "Attendance submitted!", Toast.LENGTH_SHORT).show();
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                               }
                           });

                       } else {
                           nearTextView.setVisibility(View.GONE);
                           awayView.setVisibility(View.VISIBLE);
                           Toast.makeText(AttendActivity.this, "Attendance Failed!", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       nearTextView.setVisibility(View.GONE);
                       awayView.setVisibility(View.VISIBLE);
                       Toast.makeText(AttendActivity.this, "Attendance can only be taken during specified time slots.", Toast.LENGTH_SHORT).show();
                   }

                   // Stop location updates after 5 seconds
                   locationManager.removeUpdates(this);
               }

               @Override
               public void onStatusChanged(String provider, int status, Bundle extras) {
               }

               @Override
               public void onProviderEnabled(String provider) {
               }

               @Override
               public void onProviderDisabled(String provider) {
               }
           };

           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
       } else {
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
       }
   }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                verifyLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
