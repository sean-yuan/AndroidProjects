package com.seanyuan.virtualhumidor;

/**
 * Created by seanyuan on 9/30/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailedViewActivity extends AppCompatActivity {
    ImageView mainImage;
    TextView title, type, location, price, length, gauge, notes;
    RatingBar ratingBar;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private ProgressDialog progressDiag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        progressDiag = new ProgressDialog(this);
        final String value;
        Bundle bundle = getIntent().getExtras();
        value = bundle.getString("ItemPosition");
        int position = Integer.parseInt(value);
        FeedItem feedItem = MyRecyclerAdapter.feedItemList.get(position);

        mainImage = (ImageView) findViewById(R.id.thumbnail);
        title = (TextView) findViewById(R.id.title);
        ratingBar = (RatingBar) findViewById(R.id.ratingBary);
        type = (TextView) findViewById(R.id.type);
        location = (TextView) findViewById(R.id.boughtfrom);
        price = (TextView) findViewById(R.id.pricey);
        length = (TextView) findViewById(R.id.length);
        gauge = (TextView) findViewById(R.id.gaugey);
        notes = (TextView) findViewById(R.id.notey);
        /*progressDiag.setMessage("Loading Info...");
        progressDiag.show();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://virtualhumidor-e2ab0.appspot.com");
        StorageReference imagesRef = storageRef.child(feedItem.getOwnerID() + feedItem.getTitle() + "/mainImage.jpg");
        imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mainImage.setImageBitmap(bitmap);
                progressDiag.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDiag.hide();
            }
        });*/


        mainImage.setImageBitmap(feedItem.thumbnail);
        title.setText(feedItem.getTitle());
        ratingBar.setRating(Float.parseFloat(feedItem.getRatingValue()));
        type.setText(feedItem.getType());
        location.setText("Bought From:                             " + feedItem.getLocation());
        price.setText("Price:                                           $" + feedItem.getPrice());
        length.setText("Length:                                        " + feedItem.getLength() + " inches");
        gauge.setText("Gauge:                                         " + feedItem.getGauge() + " ring");
        notes.setText(feedItem.getNotes());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab10);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedViewActivity.this, AddActivity.class);
                intent.putExtra("ItemPosition", value);
                startActivity(intent);
            }
        });


    }
}