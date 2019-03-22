package findsolucoes.com.appcidade.Utils.googlemapsapi.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.freetime.mob.FreetimeActivity;
import org.freetime.mob.webservices.Requester;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import findsolucoes.com.appcidade.R;
import findsolucoes.com.appcidade.Utils.googlemapsapi.AddMarkerClickListener.IAddMarkerClickListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.DeleteMarker.IDeleteMarkerListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.DrawLineMaps.DrawMarkerListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.CalculateEstimatedTimeListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.EstimatedtimeResponse;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.TravelMode;
import findsolucoes.com.appcidade.Utils.googlemapsapi.GoogleMapsApi;

public class GetEstimatedTimeActivity extends FreetimeActivity implements
        OnMapReadyCallback {


    GoogleMap googleMap;
    @BindView(R.id.sample_bt)
    Button bt;

    GoogleMapsApi googleMapsApi;
    Requester requester;

    private static String TAG = "GETESTIMATEDTIMEACTIVITY";

    //GOOGLE MAPS API CURRENT POSITION
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_layout);
        setupFreetime(savedInstanceState);
        ButterKnife.bind(this);

        requester = new Requester(this, getSharedServicesManager());

        //Check permission for create maps and get location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap gm) {
        googleMap = gm;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMapsApi = new GoogleMapsApi(GetEstimatedTimeActivity.this, "AIzaSyCStQ5MqPfq8LgNEUb5b0W4TbwxfF51zAQ", googleMap);

    }

    public void getEstimatedTime(View view){

        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-20.123799, -44.909886));
        latLngs.add(new LatLng(-20.124165, -44.910165));
        latLngs.add(new LatLng(-20.124445, -44.910463));

        googleMapsApi.setTravelMode(TravelMode.WALKING).calculeteEstimatedTime(latLngs, requester,
                new CalculateEstimatedTimeListener() {
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
    public void drawRoutInMapWITHWAYPOINTS(View view){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-20.123799, -44.909886));
        latLngs.add(new LatLng(-20.124165, -44.910165));
        latLngs.add(new LatLng(-20.124445, -44.910463));
        latLngs.add(new LatLng(-20.124445, -45.800463));
        latLngs.add(new LatLng(-20.124445, -45.910463));

        ArrayList<LatLng> customWeyPoints = new ArrayList<>();
        customWeyPoints.add(new LatLng(-20.124799, -44.909886));
        customWeyPoints.add(new LatLng(-20.125165, -44.910165));
        customWeyPoints.add(new LatLng(-20.126445, -44.910463));
        customWeyPoints.add(new LatLng(-20.127445, -45.800463));
        customWeyPoints.add(new LatLng(-20.128445, -45.910463));

        googleMapsApi.setDrawMarkerWithWayPoint("STOP")
                .setCustomWayPoints(customWeyPoints)
                .setZoomMapAfterLoadMap(customWeyPoints.get(3))
                .drawPolyneOptions(latLngs, new DrawMarkerListener() {
                    @Override
                    public void onDrawMaker(ArrayList<Marker> markers, ArrayList<MarkerOptions> markerOptions) {
                        //listener when map update rout with latlangs
                    }

                    @Override
                    public void onDrawMakerError(Exception e) {
                        if(e != null){
                            Log.d(TAG, "Error: "+e.getMessage());
                            return;
                        }
                        Log.d(TAG, "Error");
                    }
                });
    }
    public void drawRoutInMapWITHOUTWAYPOINTS(View view){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-20.123799, -44.909886));
        latLngs.add(new LatLng(-20.124165, -44.910165));
        latLngs.add(new LatLng(-20.124445, -44.910463));
        latLngs.add(new LatLng(-20.124445, -45.800463));
        latLngs.add(new LatLng(-20.124445, -45.910463));

        googleMapsApi.drawPolyneOptions(latLngs, new DrawMarkerListener() {
            @Override
            public void onDrawMaker(ArrayList<Marker> markers, ArrayList<MarkerOptions> markerOptions) {
                //listener when map update rout with latlangs

            }

            @Override
            public void onDrawMakerError(Exception e) {
                if(e != null){
                    Log.d(TAG, "Error: "+e.getMessage());
                    return;
                }
                Log.d(TAG, "Error");
            }
        });
    }
    public void drawRoutInMapWITHZOOM(View view){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-20.123799, -44.909886));
        latLngs.add(new LatLng(-20.124165, -44.910165));
        latLngs.add(new LatLng(-20.124445, -44.910463));
        latLngs.add(new LatLng(-20.124445, -45.800463));
        latLngs.add(new LatLng(-20.124445, -45.910463));

        googleMapsApi.setZoomMapAfterLoadMap(latLngs.get(2))
                .drawPolyneOptions(latLngs, new DrawMarkerListener() {
                    @Override
                    public void onDrawMaker(ArrayList<Marker> markers, ArrayList<MarkerOptions> markerOptions) {
                        //listener when map update rout with latlangs
                    }

                    @Override
                    public void onDrawMakerError(Exception e) {
                        if(e != null){
                            Log.d(TAG, "Error: "+e.getMessage());
                            return;
                        }
                        Log.d(TAG, "Error");
                    }
                });
    }

    /**
     * Check permission to acess gps location
     * @return
     */
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void addmarkerWhenClick(View view){
        googleMapsApi
                .setDeleteMarkerListener(new IDeleteMarkerListener() {
                    @Override
                    public void onDeleteMarker(Marker marker) {
                        Log.d(TAG, "Marker Deleted: "+marker.getPosition());
                    }

                    @Override
                    public void onDeleteMarkerError(Exception e) {

                    }
                }).setDeleteMarker()
                .setDrawPolylineWhenAddMarker()
                .withOptionsMarker(GetEstimatedTimeActivity.this,"Add", "Title of new marker ?")
                .addMarkerTouchListener(new IAddMarkerClickListener() {
                    @Override
                    public void onMarkerAdded(Marker marker, MarkerOptions markerOptions) {
                        Log.d(TAG, "Marker: "+marker.getPosition());
                        Log.d(TAG, "MarkerOptions: "+markerOptions.getPosition());
                    }

                    @Override
                    public void onErrorAddMarker(Exception e) { }
                });
    }
}
