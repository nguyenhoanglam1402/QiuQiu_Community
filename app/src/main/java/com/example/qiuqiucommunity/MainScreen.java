package com.example.qiuqiucommunity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private final int REQUEST_CAMERA_CODE = 101;
    TextView userNameTextView, phoneNumberTextView,addressTextView, genderTextView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    com.mikhaellopez.circularimageview.CircularImageView largeAvatar;
    ImageView profileBackgroundImgView;
    Button newsButton;
    ListView postList;
    ImageView avatarImageOfPost;
    TextView nameUser;
    TextView description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mapping();
        loadUserInformationFromCloud();
        eventTaking();
        loadPost();
    }

    private void eventTaking(){
        largeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionChecking();
                loadAvatar();
            }
        });
        profileBackgroundImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionChecking();

            }
        });
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionChecking();
            }
        });
    }

    private void loadAvatar(){

    }

    private void permissionChecking(){
        if(ContextCompat.checkSelfPermission(MainScreen.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainScreen.this,
                    new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_CODE);
        }
    }

    private void loadUserInformationFromCloud() {
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


    class CustomList extends BaseAdapter{
        int[] Images = {R.drawable.vuthituyetmai,
                        R.drawable.maianhduong,
                        R.drawable.chanhday};
        String[] Username = {"Vũ Thị Tuyết Mai",
                            "Mai Ánh Dương",
                            "Trúc Lam"};
        String[] Description = {"Muốn có người yêu vailon. Nhưng thui tui chịu được :))",
                                "Thân em vừa trắng lại vừa tròn :)). Ai hốt ăn liền thì alo",
                                "Công nhận thả thính mà éo có ai dính :)"};
        @Override
        public int getCount() {
            return Images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(position == 0){
                view= getLayoutInflater().inflate(R.layout.profile_item, null);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.postitem, null);
                avatarImageOfPost = view.findViewById(R.id.avatarPostItemIMG);
                nameUser = view.findViewById(R.id.usernamePostTextViewTV);
                description = view.findViewById(R.id.descriptionPostTextViewTV);
                //Receiving data
                avatarImageOfPost.setImageResource(Images[position]);
                nameUser.setText(Username[position]);
                description.setText(Description[position]);
            }
            return view;
        }
    }

    private void loadPost(){
        CustomList customList = new CustomList();
        postList.setAdapter(customList);
        Toast.makeText(this, "Count of Image is "+ customList.getCount(),Toast.LENGTH_LONG).show();
    }

    private void mapping(){
        userNameTextView = findViewById(R.id.fullNameTV);
        phoneNumberTextView = findViewById(R.id.phoneTV);
        addressTextView = findViewById(R.id.addressTV);
        genderTextView = findViewById(R.id.genderTV);
        largeAvatar = findViewById(R.id.largeAvatar);
        profileBackgroundImgView = findViewById(R.id.profileBackground);
        newsButton= findViewById(R.id.newsBtn);
        postList = findViewById(R.id.postListPL);
    }


}