package com.seanyuan.virtualhumidor3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seanyuan.virtualhumidor3.models.Cigar;
import com.seanyuan.virtualhumidor3.models.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCigarActivity extends BaseActivity {

    private static final String TAG = "NewCigarActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText name, type, length, gauge, amount, price, location, notes;
    private ImageView imager;
    private Button photoButton;
    private RatingBar ratingBar;
    private String txtRatingValue;
    private static final int selected_p = 1;
    private Bitmap s_image;
    private FloatingActionButton mSubmitButton, deleteButton;
    FirebaseStorage storage;
    String imageCode;
    Boolean isEdit;
    Bundle bundle;
    private String cigarID = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cigar);
        if(getIntent().getExtras() != null){
            isEdit = true;
            bundle = getIntent().getExtras();
        } else{
            isEdit = false;
        }

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        // [END initialize_database_ref]

        name = (EditText) findViewById(R.id.cigarName);
        type = (EditText) findViewById(R.id.cigarType);
        length = (EditText) findViewById(R.id.length);
        gauge = (EditText) findViewById(R.id.gauge);
        amount = (EditText) findViewById(R.id.amount);
        price = (EditText) findViewById(R.id.price);
        location = (EditText) findViewById(R.id.location);
        notes = (EditText) findViewById(R.id.notes);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarSetter);
        imager = (ImageView) findViewById(R.id.thumbnail);
        deleteButton = (FloatingActionButton) findViewById(R.id.fab_delete_post);
        deleteButton.setVisibility(View.GONE);

        if(isEdit){
            String value = bundle.getString("CigarFields");
            String [] cigarValue = value.split(">");
            name.setText(cigarValue[1]);
            type .setText(cigarValue[3]);
            length .setText(cigarValue[6]);
            gauge .setText(cigarValue[7]);
            amount .setText(cigarValue[8]);
            price .setText(cigarValue[5]);
            location .setText(cigarValue[4]);
            ratingBar .setRating(Float.valueOf(cigarValue[2]));
            txtRatingValue = cigarValue[2];
            deleteButton.setVisibility(View.VISIBLE);

            String noteValue = bundle.getString("CigarNotes");
            notes .setText(noteValue);

            cigarID = bundle.getString("CigarID");
            final String ownerID = bundle.getString("OwnerID");


            imageCode = cigarValue[0];
            StorageReference storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
            StorageReference islandRef = storageRef.child("images/" + cigarValue[0]);

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("got image from saved cigar");
                    Bitmap bitty = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imager.setImageBitmap(bitty);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("cigars").child(cigarID).setValue(null);
                    mDatabase.child("user-cigars").child(ownerID).child(cigarID).setValue(null);
                    mDatabase.child("cigar-comments").child(cigarID).setValue(null);


                    Intent intent = new Intent(NewCigarActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }



        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        photoButton = (Button) findViewById(R.id.addPhotoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), selected_p);

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                txtRatingValue = (String.valueOf(rating));
            }
        });
    }

    private void submitPost() {

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewCigarActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String cigar_name = name.getText().toString();
                            String cigar_type = type.getText().toString();
                            String cigar_length = length.getText().toString();
                            String cigar_gauge = gauge.getText().toString() ;
                            String cigar_notes = notes.getText().toString();
                            String cigar_location = location.getText().toString();
                            String cigar_price = price.getText().toString();
                            String cigar_amount =  amount.getText().toString();
                            String RatingValuer = txtRatingValue;
                            String ownerID = getUid();
                            String ownerPhoto = user.pictureID;

                            // Title is required
                            if (TextUtils.isEmpty(cigar_name)) {
                                name.setError(REQUIRED);
                                return;
                            }
                            if (TextUtils.isEmpty(cigar_type)) {
                                cigar_type = "N/A";
                            }
                            if (TextUtils.isEmpty(txtRatingValue)) {
                                RatingValuer = Integer.toString(0);
                            }
                            if (TextUtils.isEmpty(cigar_length)) {
                                cigar_length = "N/A";
                            }
                            if (TextUtils.isEmpty(cigar_gauge)) {
                                cigar_gauge = "N/A";
                            }
                            if (TextUtils.isEmpty(cigar_notes)) {
                                cigar_notes = "N/A";
                            }
                            if (TextUtils.isEmpty(cigar_location)) {
                                cigar_location = "N/A";
                            }
                            if (TextUtils.isEmpty(cigar_price)) {
                                cigar_price = "N/A";
                            }
                            if (TextUtils.isEmpty(cigar_amount)) {
                                cigar_amount = "N/A";
                            }
                            if(s_image == null){
                                System.out.println(" adding a null image");
                            }
                            if(imageCode == null){
                                imageCode = "none-";
                            }
                            if(isEdit){
                                //Update post
                                Cigar cigar_old = new Cigar(ownerPhoto, ownerID, cigar_name, user.username, cigar_type, cigar_length, cigar_gauge,
                                        cigar_notes, cigar_location, cigar_price, cigar_amount, RatingValuer, cigarID, s_image, imageCode);
                                updateCigar(userId, cigar_old);
                            } else{
                                // Write new post
                                Cigar cigar_new = new Cigar(ownerPhoto, ownerID, cigar_name, user.username, cigar_type, cigar_length, cigar_gauge,
                                        cigar_notes, cigar_location, cigar_price, cigar_amount, RatingValuer, cigarID, s_image, imageCode);
                                writeNewCigar(userId, cigar_new);
                            }
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case selected_p:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
                        StorageReference riversRef = storageRef.child("images/"+getUid()+hashCode());
                        imageCode = getUid()+hashCode();
                        riversRef.putFile(imageUri);
                        s_image = BitmapFactory.decodeStream(imageStream);
                        imager.setImageBitmap(s_image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }


    }

    private void setEditingEnabled(boolean enabled) {
        name.setEnabled(enabled);
        type.setEnabled(enabled);
        length.setEnabled(enabled);
        gauge .setEnabled(enabled);
        amount.setEnabled(enabled);
        price .setEnabled(enabled);
        location .setEnabled(enabled);
        notes .setEnabled(enabled);
        ratingBar .setEnabled(enabled);
        imager.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewCigar(String userId, Cigar cigar_new) {
        // Create new cigar at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("cigars").push().getKey();
        cigar_new.cigarID = key;
        System.out.println("uploading cigar with key: " + key);
        Map<String, Object> cigarValues = cigar_new.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/cigars/" + key, cigarValues);
        childUpdates.put("/user-cigars/" + userId + "/" + key, cigarValues);

        mDatabase.updateChildren(childUpdates);
        Intent intent = new Intent(NewCigarActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    // [END write_fan_out]

    private void updateCigar(String userId, Cigar old){
        System.out.println("updating cigar with key: " + old.cigarID);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_amount").setValue(old.cigar_amount);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_gauge").setValue(old.cigar_gauge);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_length").setValue(old.cigar_length);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_location").setValue(old.cigar_location);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_name").setValue(old.cigar_name);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_notes").setValue(old.cigar_notes);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_price").setValue(old.cigar_price);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("cigar_type").setValue(old.cigar_type);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("imageCode").setValue(old.imageCode);
        mDatabase.child("user-cigars").child(userId).child(old.cigarID).child("RatingValuer").setValue(old.RatingValuer);

        mDatabase.child("cigars").child(old.cigarID).child("cigar_amount").setValue(old.cigar_amount);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_gauge").setValue(old.cigar_gauge);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_length").setValue(old.cigar_length);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_location").setValue(old.cigar_location);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_name").setValue(old.cigar_name);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_notes").setValue(old.cigar_notes);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_price").setValue(old.cigar_price);
        mDatabase.child("cigars").child(old.cigarID).child("cigar_type").setValue(old.cigar_type);
        mDatabase.child("cigars").child(old.cigarID).child("imageCode").setValue(old.imageCode);
        mDatabase.child("cigars").child(old.cigarID).child("RatingValuer").setValue(old.RatingValuer);

        Intent intent = new Intent(NewCigarActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
