package com.leo.nopasswordforyou;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.leo.nopasswordforyou.helper.Security;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class GeneratePass extends AppCompatActivity {

    AppCompatSpinner spinnerNumbers, spinnerSpecialSym, spinnerSmallLetter, spinnerCapLetter;
    TextView passText, textView,test;
    Button  regeneratePass;
    ImageView copyPass;
    SwitchCompat customSetSwitch;
    LinearLayout customSettings;
    ConstraintLayout  layout2;

    byte alphaCapLength = 4;
    byte specialSymbol = 2;
    byte numberslen = 4;
    byte alphaSmallLength = 6;
    byte passLength = 16;
    Security security = null;
    String[] capitalLetter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    String[] smallLetter = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y"};
    String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    String[] specials = {"#", "$", "&", "*", "@", "~", "?", "=", "/", ":"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pass);


        layout2 = findViewById(R.id.layout2);
        customSettings = findViewById(R.id.customSettings);

        copyPass = findViewById(R.id.copyPass);
        customSetSwitch = findViewById(R.id.customSetSwitch);

        spinnerCapLetter = findViewById(R.id.spinnerCapLetter);
        spinnerSmallLetter = findViewById(R.id.spinnerSmallLetter);
        spinnerSpecialSym = findViewById(R.id.spinnerSpecialSym);
        spinnerNumbers = findViewById(R.id.spinnerNumbers);


        regeneratePass = findViewById(R.id.regeneratePass);

        passText = findViewById(R.id.passText);
        passText.setText(generateNewPass());
        test = findViewById(R.id.test);
        textView = findViewById(R.id.textView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                security = new Security(this);
            } catch (NoSuchPaddingException |
                     NoSuchAlgorithmException |
                     KeyStoreException |
                     CertificateException |
                     IOException e) {
                Toast.makeText(this, "Something Went Wrong "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        regeneratePass.setOnClickListener(v ->{
            passText.setText(generateNewPass());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    test.setText(security.decryptData(test.getText().toString()));
                } catch (InvalidKeyException | InvalidAlgorithmParameterException |
                         IllegalBlockSizeException | BadPaddingException |
                         NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException |
                         UnrecoverableEntryException | InvalidKeySpecException e) {
                    Snackbar.make(v, Objects.requireNonNull(e.getMessage()),Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });






        String[] values = {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                , values);


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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)     {
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
        customSetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                customSettings.setVisibility(View.VISIBLE);
                numberslen = Byte.parseByte((String) spinnerNumbers.getSelectedItem());
                alphaCapLength = Byte.parseByte((String) spinnerCapLetter.getSelectedItem());
                alphaSmallLength = Byte.parseByte((String) spinnerSmallLetter.getSelectedItem());
                specialSymbol = Byte.parseByte((String) spinnerSpecialSym.getSelectedItem());
            } else {
                customSettings.setVisibility(View.GONE);
                alphaCapLength = 4;
                specialSymbol = 2;
                numberslen = 4;
                alphaSmallLength = 6;
                passLength = 16;
            }
        });

        copyPass.setOnClickListener(v -> {


            try {
                test.setText(security.encryptData(passText.getText().toString()));
            } catch (InvalidAlgorithmParameterException | KeyStoreException |
                     NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException |InvalidKeySpecException| UnrecoverableEntryException e) {
                Snackbar.make(v, Objects.requireNonNull(e.getMessage()),Snackbar.LENGTH_SHORT).show();
            }


            ClipboardManager clipboard = (ClipboardManager) GeneratePass.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", passText.getText());
            clipboard.setPrimaryClip(clip);
            copyPass.setImageResource(R.drawable.icon_done_24);
            Handler handler = new Handler();
            handler.postDelayed(() -> copyPass.setImageResource(R.drawable.icon_copy_24), 1500);


        });
    }

    private void updateTotalText() {
        int total = alphaCapLength + alphaSmallLength + specialSymbol + numberslen;
        String k = String.valueOf(total);
        textView.setText(k);
    }

    private String generateNewPass() {





        ArrayList<String> password = new ArrayList<>(passLength);


        for (byte a = 0; a < alphaSmallLength; a++) {
            password.add(smallLetter[(int) (Math.random() * 25)]);
        }
        for (byte d = 0; d < specialSymbol; d++) {
            password.add(specials[(int) (Math.random() * 9)]);
        }
        for (byte c = 0; c < numberslen; c++) {
            password.add(numbers[(int) (Math.random() * 9)]);
        }

        for (byte b = 0; b < alphaCapLength; b++) {
            password.add(capitalLetter[(int) (Math.random() * 25)]);
        }


        Collections.shuffle(password);

        String pass = "";
        for (String a : password) pass += a;
        return pass;

    }
}