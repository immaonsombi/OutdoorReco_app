package com.example.outdoorreco;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import models.Place;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG=HomeFragment.class.getSimpleName();
    private static final String URL="http://10.20.140.28/myLawyer/public/api/display_lawyers";
    private RecyclerView recyclerView;
    private List<Place> placeList;
    private PlaceAdapter placeAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newIstance(String param1,String param2){
        HomeFragment homeFragment=new HomeFragment();
        Bundle args=new Bundle();
        homeFragment.setArguments(args);
        return homeFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler);
        placeList=new ArrayList<>();
        placeAdapter=new PlaceAdapter(getActivity(),placeList);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(8),true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        fetchLawyerItems();
        return view;
    }

    private int dpToPx(int dp) {
        Resources r=getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }

    private void fetchLawyerItems() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Gson gson=new Gson();
                Type listType=new TypeToken<List<Place>>(){}.getType();
                try {
//                    JSONArray jsonArray = response.getJSONArray("lawyers");
                    List<Place> lawyers =new ArrayList<>();
                    lawyers=gson.fromJson(response.getJSONArray("lawyers").toString(),listType);
                    if (lawyers!=null && !lawyers.isEmpty()){
                        placeList.clear();
                        placeList.addAll(lawyers);
                        placeAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String message=null;
                if (error instanceof NetworkError){
                    message="Could not load Lawyers..Check your connection";
                }
                if (error instanceof ServerError){
                    message="Server could not be reached..Make sure you have internet connection..";
                }
                if (error instanceof AuthFailureError){
                    message="Could not load Lawyers..Check your connection";
                }
                if (error instanceof NoConnectionError){
                    message="Could not connect to the internet";
                }


                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyPlaces.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position=parent.getChildAdapterPosition(view);
            int column=position % spanCount; //item
            if (includeEdge){
                outRect.left=spacing-column*spacing/spanCount;
                outRect.right=(column+1) *spacing / spanCount;

                if (position<spanCount){
                    outRect.top=spacing;
                }
                outRect.bottom=spacing;
            }
            else {
                outRect.left=column*spacing/spanCount;
                outRect.right=spacing-(column+1)*spacing /spanCount;
                if (position>=spanCount){
                    outRect.top=spacing;
                }
            }
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {
        private Context context;
        private List<Place>lawyerList;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView lawyer_name,phone,specialization,working_days,location;
            public ImageView thumbnail;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                lawyer_name=itemView.findViewById(R.id.name);
                phone=itemView.findViewById(R.id.phone);
                specialization=itemView.findViewById(R.id.specialization);
                working_days=itemView.findViewById(R.id.working_days);
                location=itemView.findViewById(R.id.location);
                thumbnail=itemView.findViewById(R.id.thumbnail);

            }
        }
        public PlaceAdapter(Context context, List<Place> lawyerList) {
            this.context=context;
            this.lawyerList=lawyerList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {

            final Place place=lawyerList.get(position);
//          Log.d("Lawyer id:",String.format("value = %d", lawyer.getId()));
            holder.lawyer_name.setText("Lawyer Name: " +place.getName());
            holder.phone.setText("Contact: " +place.getPhone());
            holder.specialization.setText("Specialization: " +place.getSpecialization());
            holder.working_days.setText(("Work Days :"+place.getWorkingDays()));
            holder.location.setText(("Location: " +place.getLocation()));
            Log.d("image",place.getProfileImg());
            Glide.with(context).load(place.getProfileImg()).into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return lawyerList.size();
        }
    }
}
