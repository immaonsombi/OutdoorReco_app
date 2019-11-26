package com.example.outdoorreco;




import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //storage
    StorageReference storageReference;
    // path where  images  of user profile and cover image
    String storagepath="Users_Profile_Cover_Imgs/";

    //views from xml

    ImageView avatariv,coverIv;
    TextView nameTv,emailTv,phonetv;
    FloatingActionButton fab;

    //progress dialog
    ProgressDialog pd;

    //camera permission stuff
    private  static final int CAMERA_REQUEST_CODE=100;
    private  static final int STORAGE_REQUEST_CODE=200;
    private  static final int IMAGE_PICK_GALLERY_CODE=300;
    private  static final int IMAGE_PICK_CAMERA_CODE=400;


    //array of permission
    String cameraPermissions[];
    String storagePermissions[];


    //uri of picked image
    Uri image_uri;

    //for checking the profile
    String profileOrCoverphoto;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_account, container, false);



        //init firebase
        firebaseAuth =FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference=getInstance().getReference(); //firebase storage reference



        // init array permission
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatariv=view.findViewById(R.id.avatarIv);
        coverIv=view.findViewById(R.id.coverIv);
        nameTv=view.findViewById(R.id.nameTv);
        emailTv=view.findViewById(R.id.emailTv);
        phonetv=view.findViewById(R.id.phoneTv);
        fab=view.findViewById(R.id.fab);

        pd=new ProgressDialog(getActivity());

        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check the required data
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get the data
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String image=""+ds.child("image").getValue();
                    String cover=""+ds.child("cover").getValue();

                    //get  data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phonetv.setText(phone);
                    try {
                        //when image is loaded
                        Picasso.get().load(image).into(avatariv);
                    }catch (Exception e){
                        // if there is an exception for loading the image
                        Picasso.get().load(R.drawable.ic_face).into(avatariv);
                    }
                    try {
                        //when image is loaded
                        Picasso.get().load(cover).into(coverIv);
                    }catch (Exception e){
                        // if there is an exception for loading the image

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


        return  view;
    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermissions(){
        //request runtime storage permission
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermissions(){
        //request runtime storage permission
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {

        //options on dialog
        String options[]={"Edit Profile picture","Edit cover photo","Edit Name","Edit phone"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Action");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // handles dialog item click
                if (which==0){
                    //edit profile clicked
                    pd.setMessage("Updating Profile Picture ");
                    profileOrCoverphoto="image";

                    showImagePicDialog();
                }else  if (which==1)
                {
                    //edit cover clicked
                    pd.setMessage("Updating cover Picture ");
                    profileOrCoverphoto="cover";
                    showImagePicDialog();
                }
                else  if (which==2){
                    //edit name
                    pd.setMessage("Updating Name ");
                    showNamePhoneUpdateDialog("name");

                }else  if (which==3){
                    //edit phone
                    //calling method
                    showNamePhoneUpdateDialog("phone");
                    pd.setMessage("Updating Phone");
                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update  " + key);

        // AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //  builder.setTitle("Recover Password");


        //// setting the layout
        //        LinearLayout linearLayout=new LinearLayout(this);

        //set layout dialog
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20,20,20,20);
//views
/*
    final EditText emailEt=new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

*/

        //add edit
        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter"+key);//edit name/phone
        linearLayout.addView(editText);
        editText.setMinEms(16);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);




        //add button in dialog
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //input text from edit text
                //String value=editText.getText().toString().trim();
                String value=editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //update dismiss progress
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Update...",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else
                {
                    Toast.makeText(getActivity(),"Please enter"+key,Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();

    }

    private void showImagePicDialog() {
        //show dialog containing options

        //options on dialog
        String options[]={"Camera","Gallery"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // handles dialog item click
                if (which==0){
                    //camera select

                    if (!checkCameraPermission()){
                        requestCameraPermissions();
                    }
                    else {
                        pickFromCamera();

                    }
                }else  if (which==1)
                {
                    //gallery selected
                    if (!checkStoragePermission()){
                        requestStoragePermissions();
                    }
                    else {
                        pickFromGallery();
                    }


                }
            }
        });
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking from camera
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        pickFromCamera();

                    }
                    else{
                        //permission denied
                        Toast.makeText(getActivity(),"please enable  permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //getting now from gallery
                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted ){
                        //permission  enabled
                        pickFromGallery();

                    }
                    else{
                        //
                        Toast.makeText(getActivity(),"please enable storage permission",Toast.LENGTH_SHORT).show();
                    }
                }


            }
            break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode== RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                //image picked from gallery
                image_uri=data.getData();

                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera
                uploadProfileCoverPhoto(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri)  {
        //show progress
        pd.show();
//path and name of image to be stored
        String filePathAndName=storagepath+""+profileOrCoverphoto+"_"+user.getUid();

        StorageReference storageReference2nd=storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri=uriTask.getResult();

                        //check if image is uploaded or not and the uri is received
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            //add and update
                            HashMap<String,Object> results=new HashMap<>();


                            results.put(profileOrCoverphoto,downloadUri.toString());


                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url adding user to db is successfull in database
                                            //dismis progress bar
                                            pd.dismiss();
                                            Toast.makeText(getActivity(),"Image updated",Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error adding uri in database
                                    //dismiss progress bar
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Error updating image",Toast.LENGTH_SHORT).show();

                                }
                            });

                        }else{
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void pickFromCamera()  {
//intent of picking image
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        // put image uri
        image_uri =getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraintent,IMAGE_PICK_CAMERA_CODE  );

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryintent=new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,IMAGE_PICK_GALLERY_CODE);
    }
    public  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){

        }else {
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }
    //inflate options menu
    @Override
    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //inflate menu
        inflater.inflate(R.menu.navigation,menu);
        super.onCreateOptionsMenu(menu,inflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if (id==R.id.logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }
        return super.onOptionsItemSelected(item);
    }
}

