package com.leo.nopasswordforyou;


import android.content.Intent;

import android.os.Bundle;


import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    Button generatePass;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatePass = findViewById(R.id.generatePass);
        generatePass.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,GeneratePass.class));
        });

    }




}