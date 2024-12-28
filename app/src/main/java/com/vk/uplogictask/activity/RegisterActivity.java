package com.vk.uplogictask.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vk.uplogictask.R;
import com.vk.uplogictask.databinding.ActivityRegisterBinding;
import com.vk.uplogictask.helper.ProgressDisplay;
import com.vk.uplogictask.helper.Session;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth;
    Session session;
    ProgressDisplay progressDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDisplay = new ProgressDisplay(this);
        session = new Session(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");


        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });



    }

    private void userRegister() {

        String email = binding.edtEmail.getText().toString();
        String pass = binding.edtPassword.getText().toString();
        String name = binding.edtName.getText().toString();

        if (email.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(getString(R.string.req_email))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (pass.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(getString(R.string.req_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (name.isEmpty())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(getString(R.string.req_name))
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
            firebaseAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(task ->
                    {
                        if (task.isSuccessful())
                        {
                            String id = Objects.requireNonNull(firebaseAuth.getCurrentUser().getUid());
                            HashMap<String,Object> users = new HashMap<>();
                            users.put("name",name);
                            users.put("email",email);
                            users.put("password",pass);

                            databaseReference.child(id)
                                    .child(name)
                                    .setValue(users)
                                    .addOnSuccessListener(unused -> {
                                        progressDisplay.hideProgress();
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDisplay.hideProgress();
                                        Log.e("RegisterActivity", "Error: " + e.getMessage(), e);
                                        Toast.makeText(this, "user already registered", Toast.LENGTH_SHORT).show();

                                    });
                        }
                        else
                        {
                            progressDisplay.hideProgress();
                            Toast.makeText(this, "user not registered", Toast.LENGTH_SHORT).show();

                        }

                    });
        }
    }
}