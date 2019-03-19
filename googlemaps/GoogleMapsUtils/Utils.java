package com.findsolucoes.backpackpro.googlemaps.GoogleMapsUtils;

import android.util.Log;

import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.TravelMode;
import com.google.android.gms.maps.model.LatLng;

public class Utils {
    private static String serviceGoogleMapsRout = "https://maps.googleapis.com/maps/api/directions/json?";
    private static String TAG = "UtilsGoogleMaps";

    public static String createUrlDirectionApi(TravelMode travelMode, String apiKey, LatLng origin, LatLng destination){
        String url =  serviceGoogleMapsRout+"origin="+
                origin.latitude+","+origin.longitude+ "&destination="+
                destination.latitude+","+destination.longitude+"&sensor=true&mode="+travelMode.toString().toLowerCase()+"&units=metric"+
                "&key="
                +apiKey;
        Log.d(TAG, "Travel mode: "+travelMode);
        Log.d(TAG, "Create url google maps diractions: "+url);
        return url;
    }
}
