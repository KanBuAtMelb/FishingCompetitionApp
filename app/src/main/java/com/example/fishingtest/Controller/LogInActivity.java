package com.example.fishingtest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * The controller for the "Log In" activity at the start of the App.
 */

public class LogInActivity extends AppCompatActivity {

    // Member variables here:
    private FirebaseAuth mAuth;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase authentication initialization
        mAuth = FirebaseAuth.getInstance();
        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(LogInActivity.this, RegisterUserActivity.class);
        startActivity(intent);
        finish();
    }

    // Executed when Forget My Password button pressed
    public void forgetPassword(View v){
        Intent intent = new Intent(LogInActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    // attemptLogin() used in onCreate()
    private void attemptLogin() {

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.equals("") || password.equals("")){
            Toast.makeText(this, "Please fill the email address and password", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();

        // Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FishingApp", "signInWithEmail() onComplete: " + task.isSuccessful()); // For debugging purpose

                // Check the Firebase authentication results
                if (!task.isSuccessful()) {
                    Log.d("FlashChat", "Login Failure" + task.getException().getLocalizedMessage());
                    showErrorDialog("Either the email or password might be incorrect. Please try again.");
                } else {
                    Intent intent = new Intent(LogInActivity.this, HomePageActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    // Show error on screen with an alert dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
