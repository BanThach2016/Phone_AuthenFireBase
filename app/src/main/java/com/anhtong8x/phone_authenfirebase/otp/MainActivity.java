package com.anhtong8x.phone_authenfirebase.otp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhtong8x.phone_authenfirebase.R;
import com.hbb20.CountryCodePicker;

/*
- Connect app voi firebase
    + Tool/Firebase
    + Authentication

- Enable phone
    + Vao Firebase console
    + Authentication/ Sign-in method
    + Phone/ enable

- Project yeu cau ssh. Gen ssh
    + Vao gradle/ signingReport. Click no copy ma ssh
    + Vao Firebase console. Setting/ Project setting/ Add fingerprint
    + Paste ma ssh vo
- Test tren thiet bi that. Lap sim so do vao

- Activity:
    + Lay so dien thoai gui sang activity khac
* */

public class MainActivity extends AppCompatActivity {

    CountryCodePicker mCountryCodePicker;
    EditText edtPhone;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        edtPhone = findViewById(R.id.edtPhone);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _phone = edtPhone.getText().toString().trim();
                if(_phone.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please input your phone!", Toast.LENGTH_LONG).show();

                }else {
                    Intent intent = new Intent(MainActivity.this, VerifiOtpActivity.class);
                    intent.putExtra("phone", "+" + mCountryCodePicker.getFullNumber() + edtPhone.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }
}