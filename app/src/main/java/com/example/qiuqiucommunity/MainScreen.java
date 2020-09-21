package com.example.qiuqiucommunity;

import android.os.Bundle;
import android.widget.TextView;

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

    TextView userNameTextView, phoneNumberTextView,addressTextView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Mapping();
        LoadUserInformation();

    }
    private void LoadUserInformation()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.getUid() != null){
            databaseReference.child("User Data").child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    String fullName =(String)map.get("Full name");
                    String phoneNumber =(String)map.get("Phone number");
                    String address =(String)map.get("Address");
                    userNameTextView.setText(fullName);
                    phoneNumberTextView.setText("Số điện thoại: " + phoneNumber);
                    addressTextView.setText("Địa chỉ: " + address);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {

        }
    }

    private void showUserInformation(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
            User user = new User();
            user.setFullName(snapshot.child("User Data").getValue(User.class).getFullName());
            userNameTextView.setText(user.getFullName());
        }
    }
    private void Mapping()
    {
        userNameTextView = findViewById(R.id.fullNameTV);
        phoneNumberTextView = findViewById(R.id.phoneTV);
        addressTextView = findViewById(R.id.addressTV);
    }
}