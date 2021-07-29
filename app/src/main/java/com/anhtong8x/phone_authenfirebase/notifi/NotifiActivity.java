package com.anhtong8x.phone_authenfirebase.notifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anhtong8x.phone_authenfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

/*
    - Cloud messaging:
        + Cai app lay tokenDevice.
        + Gui tokenDevice len server private. Sau nay can gui message thi se gui theo tokenDevice nay
        + Khi remove app cai moi co tokenDevice moi
        + Test gui tin lay tokenDevice vao CloudMessaging tren firebase paste token vao de de gui
        checkout
* */
public class NotifiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifi);

        /*
        // - Lay ma token device
        // class MyFirebaseInstanceIDService khong cong duoc dung nua

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", token);
                        Toast.makeText(NotifiActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
        */
    }
}