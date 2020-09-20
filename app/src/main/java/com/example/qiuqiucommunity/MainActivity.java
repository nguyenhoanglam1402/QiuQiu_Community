package com.example.qiuqiucommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qiuqiucommunity.application.example.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText userNameTxb, passwordTxb;
    TextView  signUpButton;
    Button loginButton;
    User user = new User();
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        Reference();
        ButtonEventListener();
    }
    private void ButtonEventListener()
    {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useName = userNameTxb.getText().toString();
                String password= passwordTxb.getText().toString();
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Đang đăng nhập xin chờ !");
                dialog.show();
                if(!useName.equals("") && !password.equals("")) {
                    firebaseAuth.signInWithEmailAndPassword(useName, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Đăng nhập thành công !", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, MainScreen.class));
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    if(useName.equals(""))
                    {
                        userNameTxb.setError("Tên đăng nhập không thể bỏ trống !");
                    }
                    else
                    {
                        passwordTxb.setError("Mật khẩu cần được nhập để xác minh !");
                    }
                    dialog.dismiss();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegistryActivity.class));
            }
        });
    }


    private void Reference()
    {
        userNameTxb= findViewById(R.id.usernameTxb);
        passwordTxb= findViewById(R.id.passwordTxb);
        loginButton= findViewById(R.id.loginButton);
        signUpButton= findViewById(R.id.signUpButton);
    }
}