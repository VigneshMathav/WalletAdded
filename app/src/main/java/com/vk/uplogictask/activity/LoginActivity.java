package com.vk.uplogictask.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.uplogictask.R;
import com.vk.uplogictask.databinding.ActivityLoginBinding;
import com.vk.uplogictask.helper.ProgressDisplay;
import com.vk.uplogictask.helper.Session;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    Session session;
    ProgressDisplay progressDisplay;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDisplay = new ProgressDisplay(this);
        session = new Session(this);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        binding.tvsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp()
    {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private void userLogin()
    {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (email.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.req_email))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (password.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.req_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            progressDisplay.showProgress();
            firebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(task ->
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Login successful!!",
                                            Toast.LENGTH_LONG).show();
                            String id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference("user").child(id);

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists())
                                    {
                                        progressDisplay.hideProgress();

                                        String email = snapshot.child("email").getValue(String.class);
                                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("isUserLogin", true);
                                        editor.commit();

                                        session.setData(Session.USER_ID,id);
                                        session.setData(Session.USER_EMAIL,email);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error)
                                {
                                    progressDisplay.hideProgress();
                                    Toast.makeText(LoginActivity.this, "Invalid Username & Password", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                        else
                        {
                            progressDisplay.hideProgress();
                            Toast.makeText(this, "please check email and password", Toast.LENGTH_SHORT).show();
                        }

                    });

        }
    }
}