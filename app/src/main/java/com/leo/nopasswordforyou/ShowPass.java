package com.leo.nopasswordforyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.leo.nopasswordforyou.helper.PassAdapter;

public class ShowPass extends AppCompatActivity {
    RecyclerView rvPasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pass);
        rvPasses = findViewById(R.id.rvPasses);
        PassAdapter passAdapter = new PassAdapter();
        rvPasses.setAdapter(passAdapter);
    }
}