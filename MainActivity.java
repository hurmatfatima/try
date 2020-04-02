package com.example.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    RadioButton radio_attendent,radio_admin;
 String status="";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        radio_admin=findViewById(R.id.admin);
        radio_attendent=findViewById(R.id.attendent);

        progressBar = findViewById(R.id.progess);
        progressBar.setVisibility(View.GONE); //at first progress bar is not visible,until any progress has been started
        //initializing authentication object
        //getting object of firebase
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.register).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }


    private void registerUser() {

        //we make variables final,whn we need to use them in inner clss
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(radio_attendent.isChecked())
        {
            status="attendent";
        }
        if(radio_admin.isChecked())
        {
            status="admin";
        }



        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            //requestFocus() makes a request that the given Component gets set to a focused state.
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }
 //email shoud be in proper format i.e abc@yahoo.com
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }




        progressBar.setVisibility(View.VISIBLE); //when process starts thn show progress bar

        //createUserWithEmailAndPassword:when it will get pass and email,thn it will create a user and after creating user it will also store additional info in firebase real time db
        mAuth.createUserWithEmailAndPassword(email, password)  //addOnCompleteListener:Adds a listener that is called when the Task completes.
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //OnCompleteListener,Listener called when a Task completes.

                        if (task.isSuccessful()) {

                            //creating object of helper class "User",inner param must be same as in helper class "means their sequence and their spelling"
                            User user = new User(
                                    name,
                                    email,
                                    status

                            );
                           //getInstance():Gets the default FirebaseDatabase instance.
                            //	getReference():Gets a DatabaseReference for the database root node.
                            // getUid ():string used to uniquely identify your user in your Firebase project's user
                            //getCurrentUser():returns the currently logged in user in firebase
                            //The setValue(): method overwrites data at the specified location, including any child nodes.
                            //need to set this user object on Users node in db
                            FirebaseDatabase.getInstance().getReference("Users") //creating node //after getting object of firebase db,defining path in this refrence,in db Users node will be created and everything will be stored in Users refrence
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) // inside Users node need to go to a unique user id  generated by firebase(fb) auth while creating user//get object of firebase(fb) auth system(which generates unique id for each user),and through this fb auth object we will get curent user's id,and thn values stored in user object ,will also stored in specific id
                                    .setValue(user)  //values stored in user object ,will also stored in database
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE); //when process is complete then hide progressbar
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(getApplicationContext(),login.class);
                                        startActivity(intent);


                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                registerUser();
                break;
        }
    }

}
