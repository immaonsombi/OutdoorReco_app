package com.example.outdoorreco;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import api.APIService;
import api.APIUrl;
import common.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendationFragment extends Fragment {

    public static final String ADD_URL = "http://10.20.140.28/myLawyer/public/api/add_case_details";
    public static final String KEY_EMAIL = "client_email";
    public static final String KEY_CASE_TYPE = "case_type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_OTHER = "other";
    Common common = new Common();
    private TextInputEditText txt_casetype, txt_description, txt_location, txt_other;
    String case_type, description, location, other;

    Button recommend;


    public RecommendationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);
        txt_casetype = view.findViewById(R.id.case_type);
        txt_description = view.findViewById(R.id.description);
        txt_location = view.findViewById(R.id.location);
        txt_other = view.findViewById(R.id.other);

        Common common = new Common();
        final int client_id = 1;
        Log.d("First id","is"+client_id);
        recommend = view.findViewById(R.id.recommend);

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        return view;

    }

    private void validate() {
        case_type = txt_casetype.getText().toString();
        if (case_type.isEmpty()) {
            txt_casetype.setError("Case Type is required");
        } else if (case_type.isEmpty()) {
            description = txt_description.getText().toString();
            txt_casetype.setError("Description is required");
        } else if (case_type.isEmpty()) {
            location = txt_location.getText().toString();
            txt_casetype.setError("Location Type is required");
        } else {
            addCaseDetail();
        }

    }

    private void addCaseDetail() {
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Adding your details..");
        progressDialog.show();
        final String case_type,description,location,other;
        final String  client_email=common.currentUser.getEmail();
        Log.d("id","Email is:"+client_email);
        case_type=txt_casetype.getText().toString();
        description=txt_description.getText().toString();
        location=txt_location.getText().toString();
        other=txt_other.getText().toString();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, ADD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Your details have been received.Please wait..", Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put(KEY_EMAIL,client_email);
                params.put(KEY_CASE_TYPE,case_type);
                params.put(KEY_DESCRIPTION,description);
                params.put(KEY_LOCATION,location);
                params.put(KEY_OTHER,other);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue=Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }


}
