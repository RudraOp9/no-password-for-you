package com.leo.nopasswordforyou;


import android.content.Intent;

import android.os.Bundle;


import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    Button generatePass,activityLogin;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatePass = findViewById(R.id.generatePass);
        activityLogin = findViewById(R.id.activityLogin);
        generatePass.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,GeneratePass.class));
        });
        activityLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,login_page.class)));

    }
    private void test(){

    }




}