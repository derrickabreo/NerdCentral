package com.example.derri.nerdcentral;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import java.util.HashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {


    private EditText eventname;
    private EditText department;
    private EditText college;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference rootRef;
    private DatabaseReference myRef ;
    private Button submit;
    private Button eventLsit;
    private EditText datePick;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventname = (EditText)findViewById(R.id.EventName);
        department = (EditText)findViewById(R.id.Department);
        college = (EditText)findViewById(R.id.College);
        submit =(Button) findViewById(R.id.Submit);
        eventLsit =(Button)findViewById(R.id.EventList);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        datePick = (EditText) findViewById(R.id.DatePick);



        //to check whether the user is logged in?
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            }
        };


        //Upload button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadtoFDB();
            }
        });

        //temp button to go to next activity to logout
        eventLsit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),EventListActivity.class));
            }
        });



        //date picker for edittext
        datePick.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_DeviceDefault,
                        dateSetListener,year,month,day);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = dayOfMonth + "/" + month + "/" + year;
                datePick.setText(date);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    //upload to firebase
    private void uploadtoFDB(){
        String collegestr = college.getText().toString().trim();
        String departmentstr = department.getText().toString().trim();
        String eventnamestr = eventname.getText().toString().trim();
        String eventdate = datePick.getText().toString().trim();

        myRef = rootRef.child(collegestr);
        myRef = myRef.child(departmentstr);

        HashMap<String,String> datmap = new HashMap<String, String>();
        datmap.put("Event Name", eventnamestr);
        datmap.put("Date", eventdate);
        myRef.push().setValue(datmap);
        finish();
        startActivity(new Intent(getApplicationContext(),EventListActivity.class));

    }
}
