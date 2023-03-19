package com.example.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deliveryapp.Models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Profile2 extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    EditText Username,phone;
    TextView email;
    String name,mail,profilepic,mobile;
    Button button,button2;
    ImageView imageView;
    private Uri resultUri;
    StorageReference storageReference=FirebaseStorage.getInstance().getReference();//reference to place where pfp are saved
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Username = findViewById(R.id.Username1);
            email = findViewById(R.id.emailadress);
            phone=findViewById(R.id.phone);
            button = findViewById(R.id.editinfo);
            imageView = findViewById(R.id.imageview);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }//opens page to edit info
            });
            button2 = findViewById(R.id.Changepassword);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Profile2.this, Changepassword.class);
                    startActivity(intent);
                }
            });
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID = user.getUid();
            reference.child("Customer").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users usersnapshot = snapshot.getValue(Users.class);
                    if (usersnapshot != null) {
                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        String username = usersnapshot.getUsername();
                        String Email = usersnapshot.getMail();
                        String Phone=usersnapshot.getphone();
                        Username.setText(username);
                        name = username;
                        mail = Email;
                        mobile=Phone;
                        email.setText(Email);
                        phone.setText(Phone);
                        if (map.get("profileImageUri") != null) {
                            profilepic = map.get("profileImageUri").toString();
                            Glide.with(getApplication()).load(profilepic).into(imageView);
                        }
                        button.setEnabled(!username.isEmpty() && !Email.isEmpty());
                    }

                }//writes user info in the texts


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    else {
        Intent intent=new Intent(Profile2.this,Login_Page.class);
        startActivity(intent);
        }
    }

    public void update(View view){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (isNamechanged()||isPhonechanged()|| resultUri != null) {
                if (resultUri != null) {
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(userID);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("profile images/").child(userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloaduri = uri.toString();
                                    Map newimage = new HashMap();
                                    newimage.put("profileImageUri", downloaduri);
                                    reference.child("Customer").child(userID).updateChildren(newimage);

                                }

                            });
                        }
                    });


                }
                else {
                    finish();
                }
                Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "There was no change in information", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Intent intent=new Intent(Profile2.this,Login_Page.class);
            startActivity(intent);
        }


    }//checks if there is change in info

    public void onCancelled(@NonNull DatabaseError databaseError) { throw databaseError.toException(); }



    private boolean isNamechanged() {
        if (name!=null && !name.equals(Username.getText().toString())) {
            reference.child("Customer").child(userID).child("username").setValue(Username.getText().toString());
            name=Username.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }
    private boolean isPhonechanged(){
        if (mobile!=null && !mobile.equals(phone.getText().toString())){
            reference.child("Customer").child(userID).child("phone").setValue(phone.getText().toString());
            mobile=phone.getText().toString();
            return true;
        }
        else{
            return false;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== Activity.RESULT_OK){
            final Uri imguri=data.getData();
            resultUri=imguri;
            imageView.setImageURI(resultUri);
        }
    }



}

