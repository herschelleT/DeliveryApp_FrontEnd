package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class Profile extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    EditText Username,phone;
    TextView email;
    String name,mail,profilepic,mobile,rating;
    Button button, button2,back;
    ImageView imageView;
    private Uri resultUri;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//Delivery profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Username = findViewById(R.id.Username1);
            email = findViewById(R.id.emailadress);
            button = findViewById(R.id.editinfo);
            back=findViewById(R.id.Back);
            button2 = findViewById(R.id.Changepassword);
            imageView = findViewById(R.id.imageview);
            phone=findViewById(R.id.phone);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);//goes to gallery to choose picture
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Profile.this, Changepassword.class);//goes to change password page
                    startActivity(intent);
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Profile.this,DriversMapsActivity.class);
                }
            });//goes back to the map
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID = user.getUid();
            reference.child("Delivery").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {//writes the useinfo in the text box
                    Users usersnapshot = snapshot.getValue(Users.class);
                    if (usersnapshot != null) {
                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        String username = usersnapshot.getUsername();
                        String Email = usersnapshot.getMail();
                        String Phone=usersnapshot.getphone();
                        mobile=Phone;
                        Username.setText(username);
                        phone.setText(Phone);
                        name = username;
                        mail = Email;
                        email.setText(Email);
                        if (map.get("profileImageUri") != null) {
                            profilepic = map.get("profileImageUri").toString();
                            Glide.with(getApplication()).load(profilepic).into(imageView);//adds the image to the imageview
                        }
                        button.setEnabled(!username.isEmpty() && !Email.isEmpty());//enables button only when the name isnt empty

                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Profile.this,DriversMapsActivity.class);
                    startActivity(intent);
                }
            });

        }//checks incase a user tht isnt signed in enters the page
        else{
            Intent intent=new Intent(Profile.this,Login_Page.class);
            startActivity(intent);
        }

    }

    public void update(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
        if (isNamechanged() || isPhonechanged()|| resultUri != null) {
            if (resultUri != null) {
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(userID);//checks it there is an image
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
                                reference.child("Delivery").child(userID).updateChildren(newimage);

                            }

                        });
                    }
                });


            } else {
                finish();
            }
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();

        } //checks if there was a change in information
        else {
            Toast.makeText(this, "There was no change in information", Toast.LENGTH_LONG).show();
        }


    }
        else{
            Intent intent=new Intent(Profile.this,Login_Page.class);
            startActivity(intent);
        }

}

    public void onCancelled(@NonNull DatabaseError databaseError) { throw databaseError.toException(); }

    private boolean isNamechanged() {
       if (name!=null && !name.equals(Username.getText().toString())) {
            reference.child("Delivery").child(userID).child("username").setValue(Username.getText().toString());
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

