package com.example.qiuqiucommunity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qiuqiucommunity.application.example.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegistryActivity extends AppCompatActivity {

    private final int LOCATION_REQUEST_CODE = 100;
    EditText emailTextBox,passwordTextBox,rewriteTextBox;
    EditText usernameTextBox, phoneNumberTextBox, addressTextBox;
    Spinner genderSpinner;
    Button signUpButton;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationProviderClient;
    String fineAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RegistryActivity.this);
        mapping();
        eventTaking();
        checkPermission();
        loadGender();
    }
    private void mapping() {
        usernameTextBox = findViewById(R.id.fullNameTextBox);
        emailTextBox = findViewById(R.id.emailTextBox);
        passwordTextBox = findViewById(R.id.passwordTextBox);
        rewriteTextBox = findViewById(R.id.rewritePasswordTextBox);
        phoneNumberTextBox = findViewById(R.id. phoneNumberTxb);
        addressTextBox = findViewById(R.id.addressTxb);
        signUpButton = findViewById(R.id.signUpButton);
        genderSpinner = findViewById(R.id.genderSpn);
    }

    private void eventTaking() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rewritePassword;
                rewritePassword= rewriteTextBox.getText().toString();
                progressDialog= new ProgressDialog(RegistryActivity.this);
                progressDialog.setMessage("Xin chờ...");
                progressDialog.show();
                Registry(setRegistryInformation(), rewritePassword);
            }
        });
    }

    private User setRegistryInformation(){
        final User user = new User();
        user.setEmail(emailTextBox.getText().toString());
        user.setPassword(passwordTextBox.getText().toString());
        user.setFullName(usernameTextBox.getText().toString());
        user.setPhoneNumber(phoneNumberTextBox.getText().toString());
        user.setPresentationAddress(addressTextBox.getText().toString());
        user.setAddress(fineAddress);
        user.setGender(genderSpinner.getSelectedItem().toString());
        return user;
    }

    private void loadGender(){
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,genders());
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    private ArrayList<String> genders(){
        ArrayList<String> gendersList = new ArrayList<>();
        gendersList.add("Nữ");
        gendersList.add("Nam");
        gendersList.add("Lesbian");
        gendersList.add("Gay");
        gendersList.add("Bisexual female");
        gendersList.add("Bisexual male");
        return gendersList;
    }

    private void Registry(final User user, String rewritePassword) {
            if (user.getPassword().equals(rewritePassword)
                    && user.getEmail() != null) {
                firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user.setId(firebaseAuth.getUid());
                                    // Create HashMap Object
                                    HashMap<String, Object> userMap = new HashMap<>();
                                    //Put Value for each ID
                                    userMap.put("User ID", user.getId());
                                    userMap.put("Full name", user.getFullName());
                                    userMap.put("Phone number", user.getPhoneNumber());
                                    userMap.put("Fine Address", user.getAddress());
                                    userMap.put("Presentation Address", user.getPresentationAddress());
                                    userMap.put("Gender", user.getGender());
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User Data").child(user.getId());
                                    try {
                                        databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegistryActivity.this,
                                                            "Sign up successfully !"
                                                            , Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegistryActivity.this,
                                                            task.getException().getMessage()
                                                            , Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    catch (Exception e){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistryActivity.this);
                                        builder.setTitle("Error has been identified");
                                        builder.setMessage(e.getMessage());
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistryActivity.this,
                                            task.getException().getMessage()
                                            , Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            } else {
                Toast.makeText(RegistryActivity.this,
                        "Your password and rewriting password are not match !",
                        Toast.LENGTH_SHORT).show();
            }

    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(RegistryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
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
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Get your location from above event.
                    Location location = task.getResult();
                    if(location != null){
                        // If location isn't null or have something about location information
                        Geocoder geocoder = new Geocoder(RegistryActivity.this, Locale.getDefault());
                        try {
                            List<Address>addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            showAndSetLocationInformation(addresses.get(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Toast.makeText(this,
                    "Đã sẵn sàng định vị !"
                    ,Toast.LENGTH_LONG).show();
        }
    }

    public void showAndSetLocationInformation(Address address) {
        addressTextBox.setText(address.getAdminArea()+ ", " + address.getCountryName());
        fineAddress= address.getAddressLine(0);
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