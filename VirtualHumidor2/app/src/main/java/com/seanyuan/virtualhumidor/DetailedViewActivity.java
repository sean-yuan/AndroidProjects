package com.seanyuan.virtualhumidor;

/**
 * Created by seanyuan on 9/30/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetailedViewActivity extends AppCompatActivity {
    ImageView mainImage;
    TextView title, type, location, price, length, gauge, notes;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
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

        mainImage.setImageBitmap(feedItem.getThumbnail());
        title.setText(feedItem.getTitle());
        ratingBar.setRating(Float.parseFloat(feedItem.getRatingValue()));
        type.setText(feedItem.getType());
        location.setText(feedItem.getLocation());
        price.setText(feedItem.getPrice());
        length.setText(feedItem.getPrice());
        gauge.setText(feedItem.getGauge());
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