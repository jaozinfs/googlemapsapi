package com.findsolucoes.backpackpro.googlemaps.sample;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.findsolucoes.backpackpro.R;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.CalculateEstimatedTime;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.EstimatedtimeResponse;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.TravelMode;
import com.findsolucoes.backpackpro.googlemaps.GoogleMapsApi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.freetime.mob.FreetimeActivity;
import org.freetime.mob.webservices.RequestResult;
import org.freetime.mob.webservices.Requester;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetEstimatedTimeActivity extends FreetimeActivity {

    @BindView(R.id.activity_teste_json_tv)
    TextView mTv;

    GoogleMapsApi googleMapsApi;
    Requester requester;

    private static String TAG = "GETESTIMATEDTIMEACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_json);
        setupFreetime(savedInstanceState);
        ButterKnife.bind(this);

        requester = new Requester(this, getSharedServicesManager());
        googleMapsApi = new GoogleMapsApi("AIzaSyCStQ5MqPfq8LgNEUb5b0W4TbwxfF51zAQ");


    }

    public void getEstimatedTime(View view){

        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-20.123799, -44.909886));
        latLngs.add(new LatLng(-20.124165, -44.910165));
        latLngs.add(new LatLng(-20.124445, -44.910463));

        googleMapsApi.setTravelMode(TravelMode.WALKING).calculeteEstimatedTime(latLngs, requester,
                new CalculateEstimatedTime() {
                    @Override
                    public void onEstimatedTimeResult(EstimatedtimeResponse estimatedtimeResponse) {
                        Log.d(TAG, "estimatedtime: "+estimatedtimeResponse.getEstimatedTime());
                        Log.d(TAG, "estimateddistance: "+estimatedtimeResponse.getEstimatedDistance());
                    }

                    @Override
                    public void onEstimatedTimeError(Exception e) {
                        if(e != null){
                            Log.d(TAG, "Error: "+e.getMessage());
                            return;
                        }
                        Log.d(TAG, "Error");
                    }
        });
    }
}
