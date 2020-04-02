package com.example.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    Button login,register;
    EditText emaill,pass;
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
// DatabaseReference:A Firebase reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
private  String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        emaill=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        progressBar=findViewById(R.id.progess);
        //getting object of firebase
        firebaseAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //will get all authenticated users id's
        userID = user.getUid();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //trim wil cancel extra spaces
                String password = pass.getText().toString().trim();
                String email=emaill.getText().toString().trim();

                //textutils will validate fields of txtboxes
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(login.this, "plz enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(login.this, "plz enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(login.this, "password lenght short", Toast.LENGTH_SHORT).show();
                }


                progressBar.setVisibility(View.VISIBLE);

                //"signInWithEmailAndPassword" :if entered email and pass are available in firebase then it will authenticate user
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    Toast.makeText(login.this, "logged in   successfully", Toast.LENGTH_SHORT).show();


                                   //we used to read data from firebase by using addvalueeventlistener
                                    //A ValueEventListener listens for data changes to a specific location in your database - i.e a node.
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override


                                       //  onDataChange(DataSnapshot snapshot): This method will be called with a snapshot of the data at this location.
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the activity is start
                                            //and whenever data at db is updated.
                                            //  triggered when the data at a child location has changed.

                                            //method:by pasing datasnapshot,this snapshot will take snapshot of data in db ,and then we further extract data from it
                                            //this method will read the data
                                            status(dataSnapshot);
                                        }

                                       //onCancelled(DatabaseError error):This method will be triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }




                                 else {
                                    Toast.makeText(login.this, "email and password not available", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }


        });

    }

//A DataSnapshot instance contains data from a Firebase Database location. Any time you read Database data, you receive the data as a DataSnapshot.
    private void status(DataSnapshot dataSnapshot) {
        //loop to go through all users,who are authenticated,it will iterate through all snapshots

        //getChildren():Gives access to all of the immediate children of this snapshot.
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            //creating instance of helper class

            UserInformation uInfo = new UserInformation();

            //now setting the status frm database
            //will use snapshot,go to the child of specific user id,get all parameter underneath that child,and get specificcally status
            uInfo.setStatus(ds.child(userID).getValue(UserInformation.class).getStatus()); //set the status
            String statuss=uInfo.getStatus();
            switch (statuss) {
                case "admin":
                    startActivity(new Intent(login.this, admin.class));
                    break;
                case "attendent":
                    startActivity(new Intent(login.this, attendent.class));
                    break;
            }

            /*
            //display all the information
            Log.d(TAG, "showData: name: " + uInfo.getName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

            ArrayList<String> array  = new ArrayList<>();
            array.add(uInfo.getName());
            array.add(uInfo.getEmail());
            array.add(uInfo.getPhone_num());
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
            mListView.setAdapter(adapter);

             */
        }
    }
}
