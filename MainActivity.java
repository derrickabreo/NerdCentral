package com.example.derri.nerdcentral;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity extends AppCompatActivity{
    private int flag=0;
    private String depart;
    private EditText eventname;
    private Spinner department;
    private EditText college;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference rootRef;
    private DatabaseReference myRef ;
    private Button submit;
    private Button eventLsit;
    private EditText eventDesc;
    private EditText datePick;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button uploadImage;
    static final int GALLERY_INTENT_RESULT =1;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private String imageDownloadUrl;
    private ProgressDialog uploadImagePB;

    private static final String[] DEPARTMENTS = new String[] {
          "Select Department" , "Computer Science", "Civil", "Mechanical", "Electronics and Communication"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventname = (EditText) findViewById(R.id.EventName);
        uploadImagePB = new ProgressDialog(this);
        //dropdown for department
        department = (Spinner) findViewById(R.id.Department);
            ArrayAdapter<String> departmentspinner = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,DEPARTMENTS );
            department.setAdapter(departmentspinner);
            department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0){
                        flag = 1;
                        return;
                    }
                    else {
                        flag =0;
                        depart = parent.getItemAtPosition(position).toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(MainActivity.this, "Please Select an option", Toast.LENGTH_SHORT).show();
                }
            });
        college = (EditText)findViewById(R.id.College);
        submit =(Button) findViewById(R.id.Submit);
        eventLsit =(Button)findViewById(R.id.EventList);
        eventDesc = (EditText)findViewById(R.id.EventDescription);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        datePick = (EditText) findViewById(R.id.DatePick);
        uploadImage =(Button)findViewById(R.id.uploadImage);



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
                if(flag==0)
                uploadtoFDB();
                else
                    Toast.makeText(MainActivity.this, "One or more fields are Empty", Toast.LENGTH_SHORT).show();
                return;
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

        //to upload image to firebase storage
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, GALLERY_INTENT_RESULT);
                //}

            }});


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
    //result after startactivityforresults (firebase storage)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT_RESULT && resultCode==RESULT_OK){


            uploadImagePB.show();
            uploadImagePB.setMessage("Uploading...");
            Uri fullPhotoUri = data.getData();
            StorageReference childref = storageRef.child("IMAGES").child(college.getText().toString().trim()).child(depart).child(eventname.getText().toString().trim());
            childref.putFile(fullPhotoUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageDownloadUrl = downloadUrl.toString();
                    uploadImagePB.dismiss();

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    //upload to firebase
    private void uploadtoFDB(){
        String collegestr = college.getText().toString().trim();
        if(TextUtils.isEmpty(collegestr)){ college.setError("Plese Enter The College Name"); return; }
        //String departmentstr = department.toString().trim();
        String eventnamestr = eventname.getText().toString().trim();
        if(TextUtils.isEmpty(eventnamestr)){ eventname.setError("Plese Enter The Event Name");return; }
        String eventdate = datePick.getText().toString().trim();
        String eventdesc = eventDesc.getText().toString().trim();
        if(TextUtils.isEmpty(eventdesc)){ eventDesc.setError("Plese Enter Event Name Desc"); return; }
        myRef = rootRef.child(collegestr);
        //depart is not coverted to string because it is fetched from spinner adapter and is already a string variable
        myRef = myRef.child(depart);

        HashMap<String,String> datmap = new HashMap<String, String>();
        datmap.put("Event Name", eventnamestr);
        datmap.put("Date", eventdate);
        datmap.put("Description",eventdesc);
        datmap.put("ImageURL",imageDownloadUrl);
        myRef.push().setValue(datmap);
        finish();
        startActivity(new Intent(getApplicationContext(),EventListActivity.class));

    }

}
