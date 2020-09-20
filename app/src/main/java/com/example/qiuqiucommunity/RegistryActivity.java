package com.example.qiuqiucommunity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistryActivity extends AppCompatActivity {

    private final int LOCATION_REQUEST_CODE = 100;
    EditText emailTextBox,passwordTextBox,rewriteTextBox, usernameTextBox, phoneNumberTextBox;
    Button signUpButton;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        firebaseAuth = FirebaseAuth.getInstance();
        Mapping();
        EventTaking();
        checkPermission();
    }

    private void EventTaking()
    {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, rewritePassword, userName, phoneNumber;
                email= emailTextBox.getText().toString();
                password= passwordTextBox.getText().toString();
                rewritePassword= rewriteTextBox.getText().toString();
                userName = usernameTextBox.getText().toString();
                phoneNumber = phoneNumberTextBox.getText().toString();
                progressDialog= new ProgressDialog(RegistryActivity.this);
                progressDialog.setMessage("Xin chờ...");
                progressDialog.show();
                Registry(email,password,rewritePassword,userName,phoneNumber);
            }
        });
    }
    private void Registry(String email, String password,
                          String rewritePassword,
                          final String userName, final String phoneNumber)
    {
        if(password.equals(rewritePassword) && !userName.equals(null)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String userID = firebaseAuth.getUid();
                                HashMap<String, Object> userMap = new HashMap<String, Object>();
                                userMap.put("User ID",userID);
                                userMap.put("Full name",userName);
                                userMap.put("Phone number",phoneNumber);
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("User Data").child(userID);
                                databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(RegistryActivity.this,
                                                    "Sign up successfully !"
                                                    , Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegistryActivity.this,
                                                    task.getException().getMessage()
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(RegistryActivity.this,
                                        task.getException().getMessage()
                                        ,Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
        else {
            if(userName.equals(null)) {
                Toast.makeText(RegistryActivity.this,
                        "Your name cannot be null",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RegistryActivity.this,
                        "Your password and rewriting password are not match !",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Mapping()
    {
        usernameTextBox = findViewById(R.id.fullNameTextBox);
        emailTextBox = findViewById(R.id.emailTextBox);
        passwordTextBox = findViewById(R.id.passwordTextBox);
        rewriteTextBox = findViewById(R.id.rewritePasswordTextBox);
        phoneNumberTextBox = findViewById(R.id. phoneNumberTxb);
        signUpButton = findViewById(R.id.signUpButton);
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(RegistryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog. Builder builder = new AlertDialog.Builder(RegistryActivity.this);
                builder.setTitle("Qiu Qiu Community cần");
                builder.setMessage("Quyền truy cập vị trí chính xác để định vị vị trí hiện tại của bạn");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RegistryActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Không đồng ý", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
            else {
                Toast.makeText(this,
                        "Quyền truy cập định vị bị từ chối ! Tự động định vị vị trí chính xác thất bại !",
                        Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this,
                    "Đã sẵn sàng cấp quyền !"
                    ,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,
                        "Cấp quyền thành công !",
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,
                        "Từ chối cấp quyền ! Tự động tìm kiếm vị trí thất bại !",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
}