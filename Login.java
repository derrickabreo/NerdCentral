package com.example.derri.nerdcentral;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private TextView register;
    private Button signin;
    private FirebaseAuth.AuthStateListener mAuthLitsener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.EmailID);
        password =(EditText)findViewById(R.id.Password);
        register = (TextView)findViewById(R.id.Register) ;
        signin= (Button) findViewById(R.id.SignIn);
        //to check whether the user is already logged In
        mAuthLitsener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };

        //SignIN button action
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        //For new user registration
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeractivity();
            }
        });
    }


    //user defined fuction for sigin and Registration
    private void startSignIn(){
        String email1= email.getText().toString();
        String password1 = password.getText().toString();
        if(TextUtils.isEmpty(email1)||TextUtils.isEmpty(password1)){
            Toast.makeText(this, "One or More fields are empty", Toast.LENGTH_SHORT).show();
        }
        else {


            mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Login.this, "SigIn Failed", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
        }

    }

    private void registeractivity(){
        finish();
        startActivity(new Intent(getApplicationContext(),Register.class));
    }
}
