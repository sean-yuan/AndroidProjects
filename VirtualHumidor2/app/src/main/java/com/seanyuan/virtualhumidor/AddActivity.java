package com.seanyuan.virtualhumidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.key;

/**
 * Created by seanyuan on 10/7/16.
 */

public class AddActivity extends AppCompatActivity {
    private EditText name, type, length, gauge, amount, price, location, notes;
    private ImageView imager;
    private Button save, photo;
    private RatingBar ratingBar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String txtRatingValue;
    private static final int selected_p = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference spot_image;
    private Bitmap s_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Cigar");
        setContentView(R.layout.activity_create);
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

        final String value;

        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            value = bundle.getString("ItemPosition");
            int position = Integer.parseInt(value);
            FeedItem feedItem = MyRecyclerAdapter.feedItemList.get(position);
            name.setText(feedItem.getTitle());
            amount.setText(feedItem.getQuantity());
            ratingBar.setRating(Float.parseFloat(feedItem.getRatingValue()));
            type.setText(feedItem.getType());
            location.setText(feedItem.getLocation());
            price.setText(feedItem.getPrice());
            length.setText(feedItem.getPrice());
            gauge.setText(feedItem.getGauge());
            notes.setText(feedItem.getNotes());
            //imager.setImageBitmap(feedItem.getThumbnail());

        }

        photo = (Button) findViewById(R.id.addPhotoButton);
        photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), selected_p);

            }
        });
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                txtRatingValue = (String.valueOf(rating));
            }
        });


        save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl("gs://virtualhumidor-e2ab0.appspot.com");

                FeedItem item = new FeedItem();
                item.setTitle(name.getText().toString());
                item.setType(type.getText().toString());
                item.setLength(length.getText().toString());
                item.setGauge(gauge.getText().toString());
                item.setNotes(notes.getText().toString());
                item.setLocation(location.getText().toString());
                item.setPrice(price.getText().toString());
                item.setQuantity(amount.getText().toString());
                item.setRatingValue(txtRatingValue);
                item.setOwnerID(mFirebaseUser.getUid());
                item.setCigarID(mFirebaseUser.getUid() + item.getTitle());

                StorageReference imagesRef = storageRef.child(mFirebaseUser.getUid() + item.getTitle());
                spot_image = imagesRef.child("mainImage.jpg");
                upload(s_image , spot_image);
                item.thumbnail = s_image ;

                mDatabase.child("users").child(mFirebaseUser.getUid()).child("humidor").setValue(item.getTitle());
                mDatabase.child("cigars-all").child(mFirebaseUser.getUid() + item.getTitle()).setValue(item);
                Intent intent = new Intent(AddActivity.this, ActionActivity.class);
                startActivity(intent);
            }
        });

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
                        s_image = BitmapFactory.decodeStream(imageStream);
                        imager.setImageBitmap(s_image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }


    }


    public void upload(Bitmap image, StorageReference spotImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = spotImage.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Upload failed" + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                System.out.println("upload success");
            }
        });
    }
}
