package com.seanyuan.virtualhumidor3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.seanyuan.virtualhumidor3.models.Cigar;
import com.seanyuan.virtualhumidor3.models.User;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "NewCigarActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText uname, email, password, confirm;
    private TextView registerland;
    private ImageView imager;
    private Button photoButton, register;
    private static final int selected_p = 1;
    private Bitmap s_image;
    FirebaseStorage storage;
    Boolean isEdit;
    Bundle bundle;
    private Uri userphotoID;
    private FirebaseAuth mAuth;
    private static FirebaseUser mFirebaseUser;
    private ProgressDialog progressDiag;
    Boolean proceed;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(getIntent().getExtras() != null){
            isEdit = true;
            bundle = getIntent().getExtras();
        } else{
            isEdit = false;
        }
        progressDiag = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        proceed = false;

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        // [END initialize_database_ref]

        uname = (EditText) findViewById(R.id.nameEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        confirm = (EditText) findViewById(R.id.confirmPasswordEditText);
        imager = (ImageView) findViewById(R.id.thumbnaily);
        photoButton = (Button) findViewById(R.id.profileButton);
        registerland = (TextView) findViewById(R.id.RegisterLand);
        register = (Button) findViewById(R.id.registerButton);

        if(isEdit){
            mFirebaseUser = mAuth.getCurrentUser();
            Uri photoUrl = mFirebaseUser.getPhotoUrl();
            userphotoID = photoUrl;
            imager.setImageURI(null);
            imager.setImageURI(photoUrl);
            uname.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
            registerland.setVisibility(View.GONE);
            proceed = true;
            isEdit = true;
            register.setText("Add Profile Photo");
        } else{
            photoButton.setVisibility(View.GONE);
            imager.setVisibility(View.GONE);
        }

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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!proceed){
                    String unameCheck = uname.getText().toString().trim();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    Query queryRef = mDatabase.child("users").orderByChild("username").equalTo(unameCheck);
                    ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showProgressDialog();
                        if (dataSnapshot.exists() && !proceed) {
                            proceed = false;
                            int i = 0;
                            for (DataSnapshot child: dataSnapshot.getChildren()) {
                                System.out.println("uname found : " + i);
                                i++;
                            }
                            Toast.makeText(RegisterActivity.this, "Error:" + "Username is already taken",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                        else if(!proceed && !isEdit){
                            System.out.println("uname not found");
                            proceed = true;
                            Toast.makeText(RegisterActivity.this, "Username is not taken!",
                                    Toast.LENGTH_SHORT).show();
                            register.setText("Go!");
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    };
                    queryRef.addValueEventListener(listener);
                }
                if(proceed) {
                    if (isEdit) {
                        updatePhoto(userphotoID);
                    } else {
                        String uName = uname.getText().toString().trim();
                        String eMail = email.getText().toString().trim();
                        String pass = password.getText().toString().trim();
                        String conf = confirm.getText().toString().trim();
                        validateFields(eMail, uName, pass);
                        if (pass.equals(conf)) {
                            if (validatePassword(pass)) {
                                progressDiag.setMessage("Registering User...");
                                progressDiag.show();
                                mAuth.createUserWithEmailAndPassword(eMail, pass)
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    String errormessage = task.getException().toString();
                                                    String[] message = errormessage.split(":");
                                                    Toast.makeText(RegisterActivity.this, "Error:" + message[1],
                                                            Toast.LENGTH_SHORT).show();
                                                    progressDiag.hide();
                                                    return;
                                                } else {
                                                    progressDiag.hide();
                                                    String uName2 = uname.getText().toString().trim();
                                                    System.out.println("Adding username " + uName2 );
                                                    writeNewUserPhotoandUName(userphotoID, uName2);
                                                }
                                            }
                                        });
                            } else {
                                passwordAlert("invalid");
                            }
                        } else {
                            passwordAlert("match");
                        }
                    }
                }
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
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://virtualhumidor3.appspot.com");
                        Random rand = new Random(System.currentTimeMillis());
                        location = "images/profile/" + rand;
                        StorageReference riversRef = storageRef.child(location);
                        riversRef.putFile(imageUri);
                        s_image = BitmapFactory.decodeStream(imageStream);
                        imager.setImageBitmap(s_image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }


    }

    private void writeNewUserPhotoandUName(Uri imageURI, String uname1) {
        mFirebaseUser = mAuth.getCurrentUser();
        //String myUserId = getUid();
        System.out.println("Adding username " + uname1 );
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(uname1)
                    .setPhotoUri(imageURI)
                    .build();

            mFirebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                progressDiag.hide();
                                SignInActivity.onAuthSuccess(mFirebaseUser, location);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                progressDiag.hide();
                            }
                        }
                    });
    }

    private void updatePhoto(Uri imageURI) {
        mFirebaseUser = mAuth.getCurrentUser();
        //String myUserId = getUid();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageURI)
                .build();

        mFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile photo updated.");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressDiag.hide();
                        }else{
                            progressDiag.hide();
                        }
                    }
                });

        SignInActivity.onAuthSuccess(mFirebaseUser, location);
    }

    public static boolean validatePassword(String unhashedPassword) {
        boolean hasUppercase = !unhashedPassword.equals(unhashedPassword.toLowerCase());
        boolean hasNumber = unhashedPassword.matches(".*\\d+.*");
        boolean longEnough = unhashedPassword.length() >= 6;
        return hasUppercase && hasNumber && longEnough;
    }
    public void passwordAlert(String error){
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("Wait!");
        if(error.equals("invalid")){
            alertDialog.setMessage("Passwords must be at least 6 characters long and contain at least: 1-number 1-uppercase letter");
        } else{
            alertDialog.setMessage("Passwords did not match.");
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void validateFields(String email, String name,  String pass){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Please enter email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Please enter name",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(RegisterActivity.this, "Please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
