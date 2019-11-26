package com.example.outdoorreco;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.Common;
import models.User;

public class Login extends AppCompatActivity {
    public static String LOGIN_URL;
    ProgressBar  progressBar;
    TextInputEditText txt_email,txt_password;
    String email,password;
    TextView create_account;
    MaterialButton btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login=findViewById(R.id.btn_login);
        txt_email=findViewById(R.id.email);
        txt_password=findViewById(R.id.password);
        create_account=findViewById(R.id.create_account);
        progressBar=findViewById(R.id.login_progressbar);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validate();
            }
        });
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
    }
    private void Validate(){
        email=txt_email.getText().toString().trim();
        password=txt_password.getText().toString().trim();
//        Log.d("Name",email);
        if (email.isEmpty()){
            txt_email.setError("Email is required");
        }
        else if (password.isEmpty()){
            txt_password.setError("Password is required");
        }

        else {
            LOGIN_URL="http://10.20.140.28/myLawyer/public/api/login/"+email+"/"+password;
            login();
        }
    }

    private void login() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        final JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, LOGIN_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();

                try {
                    JSONArray jsonArray=response.getJSONArray("result");
                    String UserId=jsonArray.get(0).toString();
                    String Name=jsonArray.get(1).toString();
                    String Email=jsonArray.get(2).toString();
                    String Contact=jsonArray.get(3).toString();
                    String Password=jsonArray.get(4).toString();
                    Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),UserDashboardActivity.class);
                    User user=new User(Name,Email,Contact);
                    Common.currentUser=user;
                    startActivity(intent);
                    finish();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Failed to establish connection";
                } else if (error instanceof ServerError) {
                    message = "Server could not be reached..Make sure you have internet connection";
                } else if (error instanceof AuthFailureError) {
                    message = "Server could not be reached.Make sure you have internet connection";
                } else if (error instanceof ParseError) {
                    message = "Username or Password incorrect";
                } else if (error instanceof NoConnectionError) {
                    message = "No connection..Please turn on your internet";
                }
                else {
                    Toast.makeText(Login.this, error.toString(), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            }

        });
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
}
