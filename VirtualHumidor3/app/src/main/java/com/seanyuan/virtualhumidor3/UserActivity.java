package com.seanyuan.virtualhumidor3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seanyuan.virtualhumidor3.models.User;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seanyuan on 10/24/16.
 */

public class UserActivity extends BaseActivity{
    TextView profileName, profileEmail, cigarsTotal;
    ImageView profileImage;
    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;
    FirebaseStorage storage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        String value= "-1";
        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            value = bundle.getString("CigarNumber");
            System.out.println("recieved" + value);
        }

        profileName = (TextView) findViewById(R.id.profileName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        cigarsTotal = (TextView) findViewById(R.id.humidorTotal);
        profileImage = (ImageView) findViewById(R.id.profileImageView);

        System.out.println("Setting uname: " +mFirebaseUser.getDisplayName());

        profileName.setText(mFirebaseUser.getDisplayName());
        profileEmail.setText(mFirebaseUser.getEmail());
        cigarsTotal.setText("Total cigars: " + value);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user.pictureID != null) {
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
                            StorageReference islandRef = storageRef.child(user.pictureID);
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    System.out.println("got image");
                                    Bitmap bitty = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    profileImage.setImageBitmap(getCroppedBitmap(bitty));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mFirebaseUser.getUid();
                Intent intent = new Intent(UserActivity.this, RegisterActivity.class);
                intent.putExtra("EditIntent", uid);
                startActivity(intent);
            }
        });
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
        Bitmap _bmp = Bitmap.createScaledBitmap(output, 250, 250, false);
        return _bmp;
        //return output;
    }

}
