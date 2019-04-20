/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.morningstar.intimate.R;
import com.morningstar.intimate.managers.ConstantManager;
import com.morningstar.intimate.managers.UtilityManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editText_login_email)
    EditText editTextLoginEmail;
    @BindView(R.id.editText_login_password)
    EditText editTextLoginPassword;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;
    @BindView(R.id.buttonRegister)
    Button buttonRegister;
    @BindView(R.id.loginProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.loginActivityRoot)
    LinearLayout linearLayout;
    @BindView(R.id.loginRootLayout)
    LinearLayout rootLayout;

    private String emailAddress;
    private String password;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    }

    @OnClick(R.id.buttonLogin)
    public void login() {
        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);
        emailAddress = editTextLoginEmail.getText().toString();
        password = editTextLoginPassword.getText().toString();

        firebaseAuth = FirebaseAuth.getInstance();

        if (!TextUtils.isEmpty(emailAddress) && validateEmail(emailAddress) && !TextUtils.isEmpty(password) && validatePassword(password)) {
            if (UtilityManager.isOnline()) {
                firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editor = sharedPreferences.edit();
                                    editor.putString(ConstantManager.USER_EMAIL, emailAddress);
                                    editor.putBoolean(ConstantManager.IS_AUTHENTICATED, true);
                                    editor.apply();
                                    progressBar.setVisibility(View.GONE);

                                    Intent intent = new Intent(LoginActivity.this, UserAuthenticationActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(rootLayout, "Wrong user credentials", Snackbar.LENGTH_SHORT);
                                    Toast.makeText(LoginActivity.this, "Wrong user credentials", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    buttonLogin.setEnabled(true);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buttonLogin.setEnabled(true);
                                Snackbar.make(rootLayout, "An error occurred", Snackbar.LENGTH_SHORT);
                            }
                        });
            } else {
                Toast.makeText(this, "Not Online", Toast.LENGTH_SHORT).show();
                Snackbar.make(rootLayout, "Not Online", Snackbar.LENGTH_SHORT);
            }
        } else {
            if (TextUtils.isEmpty(emailAddress))
                editTextLoginEmail.setError("Empty email");
            if (!validateEmail(emailAddress))
                editTextLoginEmail.setError("Invalid email format");
            if (TextUtils.isEmpty(password))
                editTextLoginPassword.setError("Empty password");
            if (validatePassword(password))
                editTextLoginPassword.setError("Please choose 6 digit password");

            buttonLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.buttonRegister)
    public void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);
        emailAddress = editTextLoginEmail.getText().toString();
        password = editTextLoginPassword.getText().toString();

        firebaseAuth = FirebaseAuth.getInstance();

        if (!TextUtils.isEmpty(emailAddress) && validateEmail(emailAddress) && !TextUtils.isEmpty(password) && validatePassword(password)) {
            if (UtilityManager.isOnline()) {
                firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editor = sharedPreferences.edit();
                                    editor.putString(ConstantManager.USER_EMAIL, emailAddress);
                                    editor.putBoolean(ConstantManager.IS_AUTHENTICATED, true);
                                    editor.apply();
                                    progressBar.setVisibility(View.GONE);

                                    Intent intent = new Intent(LoginActivity.this, UserAuthenticationActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User could not be created", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    buttonRegister.setEnabled(true);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buttonRegister.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(rootLayout, "An error occurred", Snackbar.LENGTH_SHORT);
                            }
                        });
            } else {
                Toast.makeText(this, "Not Online", Toast.LENGTH_SHORT).show();
                Snackbar.make(rootLayout, "Not Online", Snackbar.LENGTH_SHORT);
            }
        } else {
            if (TextUtils.isEmpty(emailAddress))
                editTextLoginEmail.setError("Empty email");
            if (!validateEmail(emailAddress))
                editTextLoginEmail.setError("Invalid email format");
            if (TextUtils.isEmpty(password))
                editTextLoginPassword.setError("Empty password");
            if (validatePassword(password))
                editTextLoginPassword.setError("Please choose 6 digit password");

            buttonRegister.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String pass) {
        if (pass.length() > 6)
            return true;
        else
            return false;
    }
}
