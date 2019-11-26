package com.example.outdoorreco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import adapters.PlaceAdapter;
import api.APIService;
import api.APIUrl;
import models.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HomeActivity extends AppCompatActivity {
    private PlaceAdapter lawyersAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<Place> LawyerModel=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent=getIntent();
//        intent.getStringExtra("Name");
//        TextView txt_name=findViewById(R.id.name);
//        txt_name.setText("Welcome"+txt_name);
        setContentView(R.layout.activity_home);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView=findViewById(R.id.recycler_view);
        fetchJson();


    }

    private void fetchJson() {

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIService apiService=retrofit.create(APIService.class);
        Call<String> call=apiService.getString();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if (response.body() !=null){
                        String jsonResponse= response.body();
                        writeRecycler(jsonResponse);
                    }
                    else {
                        Log.i("Empty Response","Nothing returned");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void writeRecycler(String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);
            if (jsonObject.optString("success").equals(true)){
                ArrayList<Place> lawyerRecycler=new ArrayList<>();
                JSONArray dataArray=jsonObject.getJSONArray("lawyers");
                for(int i=0;i<dataArray.length();i++){
                    Place lawyer=new Place();
                    JSONObject object=dataArray.getJSONObject(i);
                    lawyer.setProfileImg(object.getString("profile_img"));
                    lawyer.setName(object.getString("name"));
                    lawyer.setLocation(object.getString("location"));
                    lawyer.setPhone(object.getString("phone"));
                    lawyer.setSpecialization(object.getString("specialization"));
                    lawyer.setWorkingDays(object.getString("working_days"));
                    lawyerRecycler.add(lawyer);
                }

                /*lawyersAdapter=new PlaceAdapter(this,lawyerRecycler);
                LinearLayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(lawyersAdapter);
 */           }
            else {
                Toast.makeText(this, "message"+jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
