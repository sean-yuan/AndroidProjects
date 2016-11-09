package com.seanyuan.virtualhumidor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static android.R.attr.bitmap;


public class MyRecyclerAdapter extends RecyclerView.Adapter<FeedListRowHolder> {


    public static List<FeedItem> feedItemList;

    private Context mContext;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
        FeedListRowHolder mh = new FeedListRowHolder(v);

        return mh;
    }


    @Override
    public void onBindViewHolder(final FeedListRowHolder feedListRowHolder, int i) {
        final FeedItem feedItem = feedItemList.get(i);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://virtualhumidor-e2ab0.appspot.com");
        StorageReference imagesRef = storageRef.child(feedItem.getOwnerID() + feedItem.getTitle() + "/mainImage.jpg");
        imagesRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                feedListRowHolder.thumbnail.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        feedListRowHolder.title.setText(feedItem.getTitle());
        feedListRowHolder.type.setText(feedItem.getType());
        feedListRowHolder.price.setText("$" + feedItem.getPrice());
        feedListRowHolder.rating.setRating(Float.parseFloat(feedItem.getRatingValue()));
        feedListRowHolder.quantity.setText("Qty:" + feedItem.getQuantity());
        feedListRowHolder.mRootView.setOnClickListener(new ItemOnClickListener(feedListRowHolder.mRootView, i));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    private class ItemOnClickListener implements View.OnClickListener{
        private View current_view;
        private String position;
        public ItemOnClickListener(View v, int i) {
            current_view = v;
            position = Integer.toString(i);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(current_view.getContext(), DetailedViewActivity.class);
            intent.putExtra("ItemPosition", position);
            current_view.getContext().startActivity(intent);
        }
    }
}
