package com.example.outdoorreco;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import api.APIService;
import api.APIUrl;
import models.Result;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private   MaterialButton btnRegister;
    TextView login_text;
    private TextInputEditText txt_name,txt_contact,txt_email,txt_pin,txt_confirm;
    String name,contact,email,pin,confirm;
    final
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txt_name=findViewById(R.id.name);
        txt_contact=findViewById(R.id.contact);
        txt_email=findViewById(R.id.email);
        txt_pin=findViewById(R.id.pin);
        login_text=findViewById(R.id.txt_login);
        txt_confirm=findViewById(R.id.confirm);
        btnRegister=findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==btnRegister){
            Validate();

        }
    }

    private void Validate() {
        name=txt_name.getText().toString().trim();
        contact=txt_contact.getText().toString().trim();
        email=txt_email.getText().toString().trim();
        pin=txt_pin.getText().toString().trim();
        confirm=txt_confirm.getText().toString().trim();
        if (name.isEmpty()){
            txt_name.setError("Name is required");
        }
        else if(email.isEmpty()){
            txt_email.setError("Email is required");
        }
        else if(contact.isEmpty()){
            txt_contact.setError("Contact is required");
        }
        else  if(pin.isEmpty()){
            txt_pin.setError("Pin is required");
        }
//        else  if(!pin.equals(confirm)){
//            txt_pin.setError("Passwords do not match");
//            txt_confirm.setError("Passwords do not match");
//        }
        else {
            userSignUp();
        }
    }

    public void userSignUp(){

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        String name=txt_name.getText().toString().trim();
        String email=txt_email.getText().toString().trim();
        String contact=txt_contact.getText().toString().trim();
        String password=txt_pin.getText().toString().trim();
        String confirm=txt_confirm.getText().toString().trim();

        //build retrofit object
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //define retrofit api class

        APIService apiService=retrofit.create(APIService.class);

        //define user object

        User user=new User(name,email, contact,password);

        //define the call

        Call<Result>call=apiService.createUser(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContact(),
                user.getPassword()
        );

//        call.enqueue(new Callback<Result>() {
//            @Override
//            public void onResponse(Call<Result> call, Response<Result> response) {
//                if (!response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
//                }
//                Log.e("message",response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<Result> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Account created  Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed to create account"+response.errorBody(), Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                t.printStackTrace();
            }
        });

    }


}
