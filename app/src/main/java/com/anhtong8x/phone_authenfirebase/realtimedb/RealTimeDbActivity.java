package com.anhtong8x.phone_authenfirebase.realtimedb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhtong8x.phone_authenfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
    - CRUD Firebase database
    1. Ket noi project voi realtime firebasedatabase
        - Tools/firebase. Tim trong menu
    2. Vao Realtime trong console cua fb
        - Co the tao cac node json
        - Import file json
    3. App nhu sau
        - Them sua xoa 1 tbl_ser(key, name, image)
        - key la do firebase sinh ra, name tu nhap,
        - Image se chuyen anh sang stringbase64 day len server( chu y anh nho k se loi khi chuyen )
        - Chon anh tu gallery hoa camera. Sau do chuyen qua string base64
        - Khi click listview lay ve key de sua xoa
    4. Code
        - FirebaseDatabase quan ly db no SQL, quan ly theo node json
        - Truy van den tung node de CRUD du lieu
        - Khi them moi Firebase se tu sinh 1 key kieu unique ta luu lai lam key de truy van node do
        - FirebaseDatabase obj quan ly data cua firebase
        - FirebaseReference obj truy van den tung node trong json database
            // Truy van den node "App_Name", set gia tri cho no "Basic FirebaseDatabase"
                _FirebaseDatabase.getReference("App_Name").setValue("Basic FirebaseDatabase");
            // lay ve node tbl_user luu vao object DatabaseReference
                DatabaseReference _DatabaseReference = _FirebaseDatabase.getReference("tbl_users");
        - Lang nghe thay doi du lieu tren 1 node, nhieu node. Goi ham update khi thay doi
            + Khi ta thay doi du lieu o bat ky may nao hoac tren firebase dl se dc cap nhat den tat ca thiet bi
*/


public class RealTimeDbActivity extends AppCompatActivity {

    // ui
    TextView txtDataName;
    EditText edtName;
    ImageView imgAvata;
    Button btnSave, btnTake, btnChose;
    ListView _lvUser;

    // logic
    UserAdapter _adapUser;
    ArrayList<UserModel> _arrUser;
    boolean flag = true;   // co dang them hay sua
    Bitmap btm;
    UserModel _userModel;

    // firebase
    FirebaseDatabase _FirebaseDatabase;
    DatabaseReference _DatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_db);

        // link ui
        linkView();

        // init firebase
        _FirebaseDatabase = FirebaseDatabase.getInstance();
        // create node "App_Name" and set value
        _FirebaseDatabase.getReference("App_Name").setValue("Basic FirebaseDatabase");
        txtDataName.setText("Basic FirebaseDatabase");

        // listener node App_Name
        _FirebaseDatabase.getReference("App_Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                txtDataName.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("abc", "" + error);
            }
        });


        // create node "tbl_user"
        _DatabaseReference = _FirebaseDatabase.getReference("tbl_users");

        // init array, adapter of listview
        _arrUser = new ArrayList<>();
        _adapUser = new UserAdapter(this, _arrUser);
        _lvUser.setAdapter(_adapUser);

        // listener data change
        onListenerChangeListView(_DatabaseReference);

        // create or update data
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc", "" + flag);
                if (flag) {
                    create();
                } else {
                    if(_userModel == null) return;
                    _userModel.setName(edtName.getText().toString());

                    //
                    if(btm == null) return;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    btm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    
                    _userModel.setImage(imgeEncoded);

                    update(_userModel);
                }
            }
        });

        // take picture
        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });

        // choose picture
        btnChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        _lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _userModel = (UserModel) parent.getAdapter().getItem(position);
                //userModel = (UserModel) _adapUser.getItem(position); // lay ve item theo adapter

                if (_userModel == null) return;

                edtName.setText(_userModel.getName());
                if (_userModel.getImage() == null) return;
                // decode string64 to bitmap
                btm = imgDecodeBase64(_userModel.getImage());
                imgAvata.setImageBitmap(btm);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RealTimeDbActivity.this);
                // set title
                alertDialogBuilder.setTitle("Choose action");
                // set dialog message
                alertDialogBuilder
                        .setMessage("What do you want?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete(_userModel);
                            }
                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        _userModel.setIdUser("");
                        edtName.setText("");
                        imgAvata.setImageBitmap(null);
                        dialog.cancel();
                    }
                }).setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        flag = false;
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            btm = (Bitmap) data.getExtras().get("data");
            imgAvata.setImageBitmap(btm);
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                Bitmap b1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                btm = Bitmap.createScaledBitmap(b1, 200, 200, true);// scale anh neu k se loi ham base64

                imgAvata.setImageBitmap(btm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // chuyen string64 ve anh bitmap
    Bitmap imgDecodeBase64(String stringImgBase64) {
        byte[] decodedString = Base64.decode(stringImgBase64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    // cop anh. khong dung. ta cropt bang xml trong layout
    public void crop(Uri uri) {
        this.grantUriPermission("com.android.camera", uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //Android N need set permission to uri otherwise system camera don't has permission to access file wait crop
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        //The proportion of the crop box is 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //Crop the output image size
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //image type
        intent.putExtra("outputFormat", "PNG");
        intent.putExtra("noFaceDetection", true);
        //true - don't return uri |  false - return uri
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 201);
    }

    // choose picture
    void choosePicture() {
        Intent chopic = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(chopic, "Select Picture"), 200);

    }

    // take picture
    void capturePicture() {
        Intent capPic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(capPic, 100);
    }

    // lang nghe the doi du lieu FireBase va cap nhat vao listview
    void onListenerChangeListView(DatabaseReference _rf) {
        _rf.addValueEventListener(new ValueEventListener() {
            // Khi co su thay doi thi ham nay duoc goi
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Xoa du lieu listview truoc
                _arrUser.clear();
                _adapUser.notifyDataSetChanged();

                // Duyet snapshot dua du lieu vao mang refresh listview
                for (DataSnapshot sn : snapshot.getChildren()) {
                    UserModel us = sn.getValue(UserModel.class);
                    _arrUser.add(us);
                }
                _adapUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("abc", error.toString());
            }
        });
    }

    // link view
    void linkView() {
        txtDataName = findViewById(R.id.txtNameDataName);
        edtName = findViewById(R.id.edtName);
        imgAvata = findViewById(R.id.imgAvata);
        btnChose = findViewById(R.id.btnChose);
        btnSave = findViewById(R.id.btnSave);
        btnTake = findViewById(R.id.btnTake);
        _lvUser = findViewById(R.id.lstUser);
    }

    // create new
    // khi create firebase se tu tao 1 khoa keu unique. Ta lay ve khoa do luu no lam khoa trong bang
    // khoa nay phuc vu cho sua va xoa
    // lam moi du lieu da duoc lang nghe trong ham oncreate. Nang nghe thay doi du lieu ca node tbl_users
    void create() {
        String uId = _DatabaseReference.push().getKey(); // lay ve key do firebase tao
        if (uId.isEmpty()) return;

        String _name = edtName.getText().toString();

        //đưa bitmap về base64string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        btm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // tao doi tuong user truyen vao khoa do firebase tra ve
        UserModel userModel = new UserModel(uId, _name, imgeEncoded);

        // cap nhat du lieu cho khoa vua tao
        _DatabaseReference.child(uId).setValue(userModel);

        // xoa data, dung co nut save
        edtName.setText("");
        imgAvata.setImageBitmap(null);

        flag = false;
    }

    // edit
    void update(UserModel userModel) {
        Log.d("abc", "" + flag);
        _DatabaseReference.child(userModel.getIdUser()).child("name").setValue(userModel.getName());
        _DatabaseReference.child(userModel.getIdUser()).child("image").setValue(userModel.getImage());

        flag = true;
        btnSave.setText("Save");
        edtName.setText("");
        imgAvata.setImageBitmap(null);
    }

    // delete
    void delete(UserModel userModel) {
        _DatabaseReference.child(userModel.getIdUser()).removeValue();
        edtName.setText("");
        imgAvata.setImageBitmap(null);
    }

}// end class