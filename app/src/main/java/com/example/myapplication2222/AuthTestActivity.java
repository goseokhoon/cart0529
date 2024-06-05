package com.example.myapplication2222;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;

public class AuthTestActivity extends AppCompatActivity {

    //FirebaseAuth의 인스턴스를 선언
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authtest);

//        // onCreate() 메서드에서 FirebaseAuth 인스턴스를 초기화
        auth = FirebaseAuth.getInstance();

        Button interBtn = findViewById(R.id.interBtn);
        interBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AuthTestActivity.this, "토스트테스트", Toast.LENGTH_LONG).show();
            }
        });
//
        // 회원가입 기능
        Button joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = findViewById(R.id.email);
                EditText passwordEditText = findViewById(R.id.password);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthTestActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AuthTestActivity.this, "성공", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AuthTestActivity.this, "실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 로그인 기능
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = findViewById(R.id.email);
                EditText passwordEditText = findViewById(R.id.password);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthTestActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AuthTestActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AuthTestActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 비회원 로그인 기능
        Button noLoginBtn = findViewById(R.id.noLoginBtn);
        noLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signInAnonymously()
                        .addOnCompleteListener(AuthTestActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    Log.d("MainActivity", user.getUid());
                                } else {
                                    Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 로그아웃 기능
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(AuthTestActivity.this, "로그아웃", Toast.LENGTH_LONG).show();
            }
        });
    }
}