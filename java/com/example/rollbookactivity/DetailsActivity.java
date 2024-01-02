package com.example.rollbookactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 1;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    String currentUserId = user.getUid();

    DatabaseReference batchesRef = FirebaseDatabase.getInstance().getReference("batches");

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private EditText first;
    private EditText last;
    private EditText dob;

    private EditText mobile;
    private EditText addressEditText;
    private EditText classStudent;
    private EditText castStudent;
    private EditText roomNo;

    private ImageView imageView;
    private Uri imageUri;
    private DatabaseReference propertiesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        first = findViewById(R.id.firstNameEdit);
        last = findViewById(R.id.lastNameEdit);
        dob = findViewById(R.id.dobStudent);
        mobile = findViewById(R.id.studentNumber);
        addressEditText = findViewById(R.id.studentAddress);
        classStudent = findViewById(R.id.studentYear);
        castStudent = findViewById(R.id.studentCastEdit);
        roomNo = findViewById(R.id.allotedRoomEdit);
        imageView = findViewById(R.id.imageName);



        //propertiesRef = FirebaseDatabase.getInstance().getReference("properties");


        Button addButton = findViewById(R.id.uploadButton);
        addButton.setOnClickListener(v -> {
            // Validate details before uploading
            validateDetails();

        });

        Button selectImageButton = findViewById(R.id.chooseBtn);
        selectImageButton.setOnClickListener(v -> {
            // Open the gallery picker to select an image
            openGallery();
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    // Handle the selected image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
           // Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);

            ImageView imageView = findViewById(R.id.imageName); // Update the ID to match your layout file
            imageView.setImageURI(imageUri);
            // Handle the image URI, upload it to Firebase
        }
    }

    // Method to upload image to Firebase Storage
    // Method to upload image to Firebase Storage and student details to Realtime Database
    private void uploadImageToFirebase() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String filename = generateFilename();// Generate a unique filename for the image
        String currentDate = dateFormat.format(date);

        // Get the image URI from the ImageView or any other source you are using to display the selected image
        ImageView imageView = findViewById(R.id.imageName);
        Uri imageUri = getImageUriFromImageView(imageView);

        if (imageUri != null) {
        StorageReference imageRef = storageRef.child("images/" + filename);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, retrieve the download URL
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    // Now you have the image URL, along with other details
                                    // Create a student object and upload all the details to Firebase Realtime Database or Firestore
                                    Student student = new Student();
                                    student.setFirstName(first.getText().toString());
                                    student.setLastName(last.getText().toString());
                                    student.setDateOfBirth(dob.getText().toString());
                                    student.setNumber(mobile.getText().toString());
                                    student.setAddress(addressEditText.getText().toString());
                                    student.setYear(classStudent.getText().toString());
                                    student.setCast(castStudent.getText().toString());
                                    student.setRoomNumber(roomNo.getText().toString());
                                    student.setPicture(imageUrl);
                                    student.setUid(currentUserId);


                                    batchesRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String areas = snapshot.child(currentUserId).child("hostel").getValue(String.class);

                                            DatabaseReference studentDetails = FirebaseDatabase.getInstance().getReference("hostelStudents").child(areas).push();
                                            studentDetails.setValue(student);

                                            // for fetch the name and profile pic.
                                            DatabaseReference photo = FirebaseDatabase.getInstance().getReference("profileStudent").child(currentUserId);
                                            photo.child("image").setValue(imageUrl);
                                            photo.child("firstName").setValue(first.getText().toString());
                                            photo.child("lastName").setValue(last.getText().toString());
                                            photo.child("mobile").setValue(mobile.getText().toString());

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    Toast.makeText(DetailsActivity.this, "student added successfully", Toast.LENGTH_SHORT).show();
                                    // Update the listed value in the user's profile data

                                    startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                                    finish(); // Close the activity
                                })
                                .addOnFailureListener(e -> {
                                    // Handle student upload failure
                                    Toast.makeText(DetailsActivity.this, "Failed to add student", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve download URL
                        Log.e("FirebaseStorage", "Failed to upload image", e);
                        Toast.makeText(DetailsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });


    } else {
        // Handle case when no image is selected
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
    }

}


    // Method to get the URI of the image from an ImageView
    private Uri getImageUriFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Image.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri imageUri = FileProvider.getUriForFile(this, "com.example.rollbookactivity.fileprovider", imageFile);
        return imageUri;
    }


    // Method to generate a unique filename for the image
    private String generateFilename() {
        return "image_" + System.currentTimeMillis() + ".jpg";
    }

    public void validateDetails() {
        String firstName = first.getText().toString().trim();
        String lastName = last.getText().toString().trim();
        String dobValue = dob.getText().toString().trim();
        String phoneNumber = mobile.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String Year = classStudent.getText().toString().trim();
        String Cast = castStudent.getText().toString().trim();
        String RoomNumber = roomNo.getText().toString().trim();
        // String Picture(imageUrl);
        // String Uid(currentUserId);

        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else if (firstName.isEmpty()) {
            first.setError("Enter first name");
        } else if (lastName.isEmpty()) {
            last.setError("Enter last name");
        } else if (dobValue.isEmpty()) {
            dob.setError("Enter date of birth");
        } else if (phoneNumber.isEmpty()) {
            mobile.setError("Enter phone number");
        } else if (address.isEmpty()) {
            addressEditText.setError("Enter address");
        } else if (Year.isEmpty()) {
            classStudent.setError("Enter present studing year");
        }else if (Cast.isEmpty()) {
            castStudent.setError("Enter Cast");
        }else if (RoomNumber.isEmpty()) {
            roomNo.setError("Enter your room number");
        }else {
            // All fields are filled, proceed with uploading the data to Firebase
            // Upload data to Firebase
            uploadImageToFirebase();

        }
    }

}