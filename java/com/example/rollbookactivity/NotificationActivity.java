package com.example.rollbookactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;

    RelativeLayout noData;

    ProgressBar progressBar;
    ImageView backArrow;

    FirebaseAuth auth1 = FirebaseAuth.getInstance();
    String user = auth1.getUid();

    String area;

    MyAdapter adapter;

    private List<Notify> propertyList;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    // Assuming "UIDs" is a child node of the root node
    DatabaseReference batchesRef = databaseRef.child("batches");

    DatabaseReference notifications = FirebaseDatabase.getInstance().getReference("notifications");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



        recyclerView = findViewById(R.id.recyclerViewUser);
        progressBar = findViewById(R.id.progressNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertyList = new ArrayList<>();
        adapter = new MyAdapter(propertyList);
        recyclerView.setAdapter(adapter);
        noData = findViewById(R.id.noMessage);

        backArrow = findViewById(R.id.back_arrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });
        // Assuming you have an instance of your RecyclerView adapter named "notificationAdapter"
       //adapter.notifyDataSetChanged();

        batchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String areas = snapshot.child(user).child("hostel").getValue(String.class);

                //String key2 = String.valueOf(snapshot.getChildren());
                area = areas;

                //Toast.makeText(MainActivity.this, areas, Toast.LENGTH_SHORT).show();
                retrieveDataFromFirebase();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);





    }

    private void retrieveDataFromFirebase() {
        notifications.child(area).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                propertyList.clear();



                for (DataSnapshot propertySnapshot : dataSnapshot.getChildren()) {
                    String date = propertySnapshot.child("date").getValue(String.class);
                    String message = propertySnapshot.child("message").getValue(String.class);

                    Notify property = new Notify(date, message);
                    propertyList.add(property);
                }
                Collections.reverse(propertyList); // reverse the order of the elements
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (!dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
                else {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}