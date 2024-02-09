package com.leo.nopasswordforyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    Button generatePass,regeneratePass;
    ImageView copyPass;
    SwitchCompat customSetSwitch;
    TextView passText,textView;
    AppCompatSpinner spinnerNumbers,spinnerSpecialSym,spinnerSmallLetter,spinnerCapLetter;
    ConstraintLayout layout1,layout2;
    LinearLayout customSettings;
    // activity launch = 0; activity generate password = 1 ; activity get password = 2
    boolean anotherActivity = false ;
    byte alphaCapLength = 4;
    byte specialSymbol = 2;
    byte numberslen = 4;
    byte alphaSmallLength = 6;
    byte passLength = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        customSettings = findViewById(R.id.customSettings);

        copyPass = findViewById(R.id.copyPass);
        customSetSwitch = findViewById(R.id.customSetSwitch);

        spinnerCapLetter = findViewById(R.id.spinnerCapLetter);
        spinnerSmallLetter = findViewById(R.id.spinnerSmallLetter);
        spinnerSpecialSym = findViewById(R.id.spinnerSpecialSym);
        spinnerNumbers = findViewById(R.id.spinnerNumbers);





        generatePass = findViewById(R.id.generatePass);
        regeneratePass = findViewById(R.id.regeneratePass);
        passText = findViewById(R.id.passText);
        textView = findViewById(R.id.textView);

        generatePass.setOnClickListener(v -> {
            layout2.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
            anotherActivity =! anotherActivity;
            passText.setText(generateNewPass());

        });

        regeneratePass.setOnClickListener(v -> passText.setText(generateNewPass()));


        customSetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)customSettings.setVisibility(View.VISIBLE);
            else customSettings.setVisibility(View.GONE);
        });


        String[] values = {"10","9","8","7","6","5","4","3","2","1"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                ,values);
        spinnerCapLetter.setAdapter(adapter);
        spinnerNumbers.setAdapter(adapter);
        spinnerSmallLetter.setAdapter(adapter);
        spinnerSpecialSym.setAdapter(adapter);
        spinnerNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberslen = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 4;
                updateTotalText();
                            }
        });
        spinnerCapLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alphaCapLength = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                alphaCapLength = 4;
                updateTotalText();
            }
        });
        spinnerSmallLetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alphaSmallLength = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 6;
                updateTotalText();
            }
        });
        spinnerSpecialSym.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialSymbol = Byte.parseByte(values[position]);
                updateTotalText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numberslen = 2;
                updateTotalText();
            }
        });

        copyPass.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", passText.getText());
            clipboard.setPrimaryClip(clip);
            copyPass.setImageResource(R.drawable.icon_done_24);
            Handler handler = new Handler();
            handler.postDelayed(() -> copyPass.setImageResource(R.drawable.icon_copy_24),1500);


        });



    }

    private void updateTotalText() {
        int total = alphaCapLength+alphaSmallLength+specialSymbol+numberslen;
        String k = String.valueOf(total);
        textView.setText(k); ;
    }

    private String generateNewPass() {


        String[] capitalLetter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"} ;
        String[] smalLetter = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y"} ;
        String[] numbers = {"0","1","2","3","4","5","6","7","8","9"};
        String[] specials = {"#","$","&","*","@","~","?","=","/",":"};

        //String[] password = new String[16];
        ArrayList<String> password = new ArrayList<>(passLength);
        // adding alphabetical small to password array

        for (byte a = 0; a < alphaSmallLength; a++){
            password.add(smalLetter[(int) (Math.random() * 25)]);
        }
        for (byte d = 0; d < specialSymbol; d++){
            password.add( specials[(int) (Math.random() * 9)]);
        }
        for (byte c = 0; c < numberslen; c++){
            password.add( numbers[(int) (Math.random() * 9)]);
        }

        for (byte b = 0; b < alphaCapLength; b++){
            password.add(capitalLetter[(int) (Math.random() * 25)]);
        }


        Collections.shuffle(password);

        String pass = "";
        for (String a : password){
            pass += a;
        }
        return pass;

    }


    @Override
    public void onBackPressed() {
        if (anotherActivity){
            layout2.setVisibility(View.GONE);
            layout1.setVisibility(View.VISIBLE);
            anotherActivity =! anotherActivity;

        }else {
            super.onBackPressed();
        }
    }
}