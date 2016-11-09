package com.seanyuan.virtualhumidor;

/**
 * Created by seanyuan on 9/30/16.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.seanyuan.virtualhumidor.MainActivity.cigarActualList;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton, cancelButton;
    private EditText nameEditText, emailEditText, passEditText, passConfEditText;
    private FirebaseAuth mFirebaseAuth;
    private static FirebaseUser mFirebaseUser;
    private static DatabaseReference mDatabase;
    private ProgressDialog progressDiag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        registerButton = (Button) findViewById(R.id.registerButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passEditText = (EditText) findViewById(R.id.passwordEditText);
        passConfEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        progressDiag = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passEditText.getText().toString().trim();
                String conf = passConfEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                validateFields(email, name, pass);
                if(pass.equals(conf)) {
                    if(validatePassword(pass)){
                        progressDiag.setMessage("Registering User...");
                        progressDiag.show();
                        mFirebaseAuth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), pass)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "failed" + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                            progressDiag.hide();
                                            return;
                                        }else{
                                            String name = nameEditText.getText().toString().trim();
                                            mFirebaseAuth = FirebaseAuth.getInstance();
                                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                            writeNewUser(mFirebaseUser.getUid(), name, mFirebaseUser.getEmail());
                                            Intent intent = new Intent(RegisterActivity.this, ActionActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                    else{
                        passwordAlert("invalid");
                    }
                }
                else{
                    passwordAlert("match");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }

    private void writeNewUser(String userId, String userName, String email) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        List<String> listy = new ArrayList<>();
        User database_user = new User(userName, email, listy);
        mDatabase.child("users").child(userId).setValue(database_user);
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


