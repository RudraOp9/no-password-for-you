package com.leo.nopasswordforyou;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login_page extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    androidx.appcompat.app.AlertDialog alertDialog;
    TextView doSignUp, doSignIn;
    LinearLayout loginLayout, signUpLayout;
    //  private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        mAuth.addAuthStateListener(firebaseAuth -> {
            mAuth.getCurrentUser();
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(this, ShowPass.class));
                mAuth.removeAuthStateListener((FirebaseAuth.AuthStateListener) this);
                finish();
            }

        });

        doSignUp = findViewById(R.id.doSignUp);
        doSignIn = findViewById(R.id.doSignIn);
        loginLayout = findViewById(R.id.loginLayout);
        signUpLayout = findViewById(R.id.signUpLayout);
        doSignUp.setOnClickListener(v -> {
            loginLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.VISIBLE);
        });
        doSignIn.setOnClickListener(v -> {
            loginLayout.setVisibility(View.VISIBLE);
            signUpLayout.setVisibility(View.GONE);
        });

        // log in
        EditText emailTextLogIn = findViewById(R.id.emailTextLogIn);
        EditText passTextLogIn = findViewById(R.id.passTextLogIn);
        Button logIn = findViewById(R.id.logIn);


        alertDialog = new MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        logIn.setOnClickListener(v -> {
            alertDialog.show();
            if (emailTextLogIn.getText().toString().trim().isEmpty() || passTextLogIn.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
                //  makeToast(this,"Fields are empty");
                alertDialog.dismiss();
            } else {
                if (mAuth.getCurrentUser() != null) mAuth.signOut();

                loginAccount(emailTextLogIn.getText().toString().trim(), passTextLogIn.getText().toString().trim());
            }
        });

        //sign up
        EditText emailTextSignUp = findViewById(R.id.emailTextSignUp);
        EditText passTextSignUp = findViewById(R.id.passTextSignUp);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            alertDialog.show();
            if (emailTextSignUp.getText().toString().trim().isEmpty()
                    || passTextSignUp.getText().toString().trim().isEmpty()) {

                Toast.makeText(this, "fields are empty !", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            } else {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                }
                createAccount(emailTextSignUp.getText().toString(), passTextSignUp.getText().toString());


            }
        });
    }




    private void loginAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Objects.requireNonNull(mAuth.getCurrentUser()).reload();
                        // Sign in success, update UI with the signed-in user's information
                        alertDialog.dismiss();
                        //   loggedIn();

                        Log.d(TAG, "signInWithEmail:success");
                    }
                }).addOnFailureListener(e -> {
                    String error = e.getMessage();
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    alertDialog.dismiss();
                    if (task.isSuccessful()) {

                        // loggedIn();
                        Log.d(TAG, "createUserWithEmail:success");
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        //makeToast(this, );
                    }
                }).addOnFailureListener(e -> {
                    alertDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}