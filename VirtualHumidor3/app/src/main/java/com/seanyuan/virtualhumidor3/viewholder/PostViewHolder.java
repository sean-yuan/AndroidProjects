package com.seanyuan.virtualhumidor3.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seanyuan.virtualhumidor3.R;
import com.seanyuan.virtualhumidor3.UserActivity;
import com.seanyuan.virtualhumidor3.models.Cigar;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;

    protected ImageView thumbnail, author_photo;
    protected TextView title;
    protected TextView type;
    protected TextView price;
    protected RatingBar rating;
    protected TextView quantity;
    FirebaseStorage storage;


    public PostViewHolder(View itemView) {
        super(itemView);
        authorView = (TextView) itemView.findViewById(R.id.cigar_author);
        author_photo = (ImageView) itemView.findViewById(R.id.cigar_author_photo);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        this.title = (TextView) itemView.findViewById(R.id.title);
        this.type = (TextView) itemView.findViewById(R.id.type);
        this.price = (TextView) itemView.findViewById(R.id.price);
        this.rating = (RatingBar) itemView.findViewById(R.id.ratingBar);
        this.quantity = (TextView) itemView.findViewById(R.id.quantity);
    }

    public void bindToPost(Cigar cigar, View.OnClickListener starClickListener) {
        authorView.setText(cigar.author);
        numStarsView.setText(String.valueOf(cigar.starCount));
        starView.setOnClickListener(starClickListener);

        //image
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
        StorageReference islandRef = storageRef.child("images/" + cigar.imageCode);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                System.out.println("got cigar image");
                Bitmap bitty = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                thumbnail.setImageBitmap(bitty);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        //image

        //image
        if(cigar.ownerPhoto != null) {
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
            islandRef = storageRef.child(cigar.ownerPhoto);
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("got image");
                    Bitmap bitty = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    author_photo.setImageBitmap(getCroppedBitmap(bitty));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }else{
            System.out.println("cigar owner photo null");
        }

        //image

        title.setText(cigar.cigar_name);
        type.setText(cigar.cigar_type);
        price.setText("$" + cigar.cigar_price);
        rating.setRating(Float.parseFloat(cigar.RatingValuer));
        quantity.setText("Qty: " + cigar.cigar_amount);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap _bmp = Bitmap.createScaledBitmap(output, 80, 80, false);
        return _bmp;
        //return output;
    }
}
