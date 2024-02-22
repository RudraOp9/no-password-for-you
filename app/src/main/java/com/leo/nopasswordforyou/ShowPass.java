package com.leo.nopasswordforyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.leo.nopasswordforyou.helper.PassAdapter;
import com.leo.nopasswordforyou.helper.PassAdapterData;

import java.util.ArrayList;

public class ShowPass extends AppCompatActivity {
    RecyclerView rvPasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pass);
        rvPasses = findViewById(R.id.rvPasses);

        ArrayList<PassAdapterData> test = new ArrayList<>();
        test.add(new PassAdapterData("Youtube","my youtube password"));
        test.add(new PassAdapterData("Instagram","my Instagram password"));
        test.add(new PassAdapterData("Facebook","my facebook password"));
        test.add(new PassAdapterData("github","my github password"));
        test.add(new PassAdapterData("whatsapp","my whatsapp password"));
        test.add(new PassAdapterData("test","my test password"));
        test.add(new PassAdapterData("hard time","my  password"));
        test.add(new PassAdapterData("password","my password"));

        PassAdapter passAdapter = new PassAdapter(test);
        rvPasses.setAdapter(passAdapter);
        rvPasses.setLayoutManager(new LinearLayoutManager(this));
    }
}