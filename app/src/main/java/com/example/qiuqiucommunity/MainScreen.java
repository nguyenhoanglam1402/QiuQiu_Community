package com.example.qiuqiucommunity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qiuqiucommunity.application.example.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainScreen extends AppCompatActivity {

    TextView userNameTextView, phoneNumberTextView,addressTextView, genderTextView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Mapping();
        LoadUserInformationFromCloud();

    }
    private void LoadUserInformationFromCloud()
    {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.getUid() != null){
            databaseReference.child("User Data").child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userDatabase = new User();
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    userDatabase.setFullName((String)map.get("Full name"));
                    userDatabase.setPhoneNumber((String)map.get("Phone number"));
                    userDatabase.setPresentationAddress((String)map.get("Presentation Address"));
                    userDatabase.setGender((String)map.get("Gender"));
                    displayUserProfile(userDatabase);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Intent intent = new Intent(MainScreen.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainScreen.this,
                            error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            // Force user to go back login screen if cannot find the UserID
            Intent intent = new Intent(MainScreen.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(MainScreen.this,
                    "Lỗi không thể tìm được vị trí cơ sở dữ liệu của bạn trong hệ thống",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void displayUserProfile(User userDatabase){
        userNameTextView.setText(userDatabase.getFullName());
        phoneNumberTextView.setText("Số điện thoại: " + userDatabase.getPhoneNumber());
        addressTextView.setText("Địa chỉ: " + userDatabase.getPresentationAddress());
        genderTextView.setText("Giới tính: " + userDatabase.getGender());
    }
    private void Mapping()
    {
        userNameTextView = findViewById(R.id.fullNameTV);
        phoneNumberTextView = findViewById(R.id.phoneTV);
        addressTextView = findViewById(R.id.addressTV);
        genderTextView = findViewById(R.id.genderTV);
    }
}