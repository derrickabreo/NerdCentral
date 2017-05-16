package com.example.derri.nerdcentral;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class Register extends AppCompatActivity {
    private Button registerbutton;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthLitsener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.RegisterEmail);
        password = (EditText) findViewById(R.id.RegisterPassword);
        registerbutton =(Button)findViewById(R.id.Register);
        mAuthLitsener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()!= null){
                  finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }
        };
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registernewUser();
            }
        });

    }
    private void registernewUser(){
        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        if(TextUtils.isEmpty(email1)||TextUtils.isEmpty(password1)){
            Toast.makeText(this, "One or More fields are empty", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                                             @Override
                                                                                             public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                                 if(task.isSuccessful()){
                                                                                                     Toast.makeText(Register.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                                                                                                     finish();
                                                                                                     startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                                                                 }
                                                                                                 else{
                                                                                                     Toast.makeText(Register.this, "Registration Failed Please try again", Toast.LENGTH_SHORT).show();
                                                                                                 }
                                                                                             }
                                                                                         }
            );
        }

    }
}
