package com.anhtong8x.phone_authenfirebase.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anhtong8x.phone_authenfirebase.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifiOtpActivity extends AppCompatActivity {

    String TAG = VerifiOtpActivity.class.getSimpleName();
    String _phone;

    PinView mPinView;
    TextView txtTime, btnReSendCode;
    Button btnNext;

    FirebaseAuth mFirebaseAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private boolean firstSend=true,timerOn=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verifi_otp);

        // 1. Lay ve so dien thoai
        _phone = getIntent().getStringExtra("phone");
        Log.d(TAG, _phone);

        mPinView = findViewById(R.id.pin_view);
        txtTime = findViewById(R.id.txtTime);
        btnReSendCode = findViewById(R.id.btnTime);
        btnNext = findViewById(R.id.btnNext);

        // 2. Khoi tao doi tuong FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // tat popup recapcha moi truong dev . Dang bi loi
        //mFirebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        //  3. Gui so phone len server
        sendVerificationCode(_phone);

        //  4. Dem nguoc de yc gui lai otp
        startResendTimer(60);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = mPinView.getText().toString();
                Log.d(TAG, otp);
                verificationCode(otp);
            }
        });

        btnReSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(_phone);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Gui so dien thoai len server. Timeout la 30s
    // 1 callback mCallback de nhan ket qua tra ve
    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mFirebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifiOtpActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        Log.d(TAG, "Click Verifi");

    }

    // callback nhan ket qua tra ve
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            // Nhan duoc ma otp cua server
            String otp = phoneAuthCredential.getSmsCode();

            if(otp != null){
                mPinView.setText(otp);
                verificationCode(otp);

            }
            Log.d(TAG, "onVerificationCompleted");
            Log.d(TAG, otp);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifiOtpActivity.this,"Send code error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationId = s;
            mResendToken = forceResendingToken; // token cua server

            Log.d(TAG, "onCodeSent");
            Log.d(TAG, "mVerificationId");
            Log.d(TAG, "" + mResendToken);
        }
    };

    // Dung otp va token de tao obj PhoneAuthCredential
    // Dung obj PhoneAuthCredential gui len server de chung thuc
    private void verificationCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        SignInWithPhoneAuthCredential(credential);
    }

    // Gui obj PhoneAuthCredential gui len server de chung thuc
    private void SignInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "SignInWithPhoneAuthCredential");

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifiOtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //Toast.makeText(VerifiOtpActivity.this, "Login success", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(VerifiOtpActivity.this, VerifiOtpSuccess.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(VerifiOtpActivity.this, "Login false", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    // Tao time count down de yc gui lai otp
    // chu y time phai lon hon timeout cua Firebase tranh spam
    public void startResendTimer(int seconds) {
        txtTime.setVisibility(View.VISIBLE);
        btnReSendCode.setEnabled(false);

        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String secondsString = Long.toString(millisUntilFinished/1000);
                if (millisUntilFinished<10000) {
                    secondsString = "Gui lai ma trong 00:"+ secondsString + " s";
                }
                txtTime.setText(secondsString);
            }

            public void onFinish() {
                btnReSendCode.setEnabled(true);
                txtTime.setVisibility(View.GONE);
                timerOn=false;
            }
        }.start();
    }

}// end class