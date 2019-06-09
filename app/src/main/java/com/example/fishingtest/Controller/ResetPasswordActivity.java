package com.example.fishingtest.Controller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Common;
import com.example.fishingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Completed by Qin Xu on 8/06/2019.
 *
 * The controller for the "Password Reset" activity
 * after clicking the "Reset My Password" button on the Log-in Page..
 */

public class ResetPasswordActivity extends AppCompatActivity {


    // UI views
    Toolbar mtoolBar;
    ProgressBar mProgressBar;
    TextView mEmail;
    Button mResetPwd_btn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mtoolBar = (Toolbar) findViewById(R.id.resetpwd_top_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.resetpwd_progressBar);
        mEmail = (TextView) findViewById(R.id.resetpwd_email);
        mResetPwd_btn = (Button) findViewById(R.id.resetpwd_btn);

        mtoolBar.setTitle("Forgot password");
        firebaseAuth = FirebaseAuth.getInstance();

        // Upon clicking the password reset button
        mResetPwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(mEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity.this,
                                            "Password link sent to your email", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(ResetPasswordActivity.this,
                                            task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                mEmail.setText(Common.EMPTY);
            }
        });

    }
}
