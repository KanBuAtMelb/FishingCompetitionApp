package com.example.fishingtest;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Member variables here:
    private FirebaseAuth mAuth;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    // attemptLogin() used in onCreate()
    private void attemptLogin() {

//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        if (email.isEmpty())
//            if (email.equals("") || password.equals("")) return;
//        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
//
//        // Use FirebaseAuth to sign in with email & password
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                Log.d("FishingApp", "signInWithEmail() onComplete: " + task.isSuccessful());
//
//                if (!task.isSuccessful()) {
//                    Log.d("FlashChat", "Problem signing in: " + task.getException());
//                    showErrorDialog("There was a problem signing in");
//                } else {
//                    Intent intent = new Intent(LogInActivity.this, HomePageActivity.class);
//                    finish();
//                    startActivity(intent);
//                }
//
//            }
//        });
//

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
