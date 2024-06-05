package com.example.myapplication2222;

import android.app.Application;
import android.util.Log;

import com.example.myapplication2222.repository.FirebaseRepository;
import com.google.firebase.FirebaseApp;

import java.util.Map;

public class LoginActivity extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        //로그인 로직(필요없나?)
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.createUserWithEmailAndPassword("tkdgus6796@naver.com", "q1w2e3r4t52@");
//        Log.d("MyApplication", "Accept UUID = " + auth.getUid());

        Map<String, Object> data = FirebaseRepository.getInstance()
                .getData("product","상품1");

        Log.d("[DATA] = ", data.toString());
    }
}