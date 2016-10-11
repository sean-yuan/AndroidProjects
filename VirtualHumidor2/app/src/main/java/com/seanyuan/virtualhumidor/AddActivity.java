package com.seanyuan.virtualhumidor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private RatingBar ratingBar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String txtRatingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Cigar");
        setContentView(R.layout.activity_create);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) findViewById(R.id.cigarName);
                type = (EditText) findViewById(R.id.cigarType);
                length = (EditText) findViewById(R.id.length);
                gauge = (EditText) findViewById(R.id.gauge);
                amount = (EditText) findViewById(R.id.amount);
                price = (EditText) findViewById(R.id.price);
                location = (EditText) findViewById(R.id.location);
                notes = (EditText) findViewById(R.id.notes);
                ratingBar = (RatingBar) findViewById(R.id.ratingBarSetter);
                //if rating value is changed,
                //display the current rating value in the result (textview) automatically
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {

                        txtRatingValue = (String.valueOf(rating));

                    }
                });



                mDatabase = FirebaseDatabase.getInstance().getReference();
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                FeedItem item = new FeedItem();
                item.setTitle(name.getText().toString());
                item.setThumbnail(R.drawable.common_google_signin_btn_icon_dark);
                item.setType(type.getText().toString());
                item.setPrice(price.getText().toString());
                item.setQuantity(amount.getText().toString());
                item.setRatingValue(txtRatingValue);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("users/" + mFirebaseUser.getUid() + "/humidor/", item);
                Intent intent = new Intent(AddActivity.this, ActionActivity.class);
                mDatabase.updateChildren(childUpdates);
                startActivity(intent);
            }
        });

    }
}
