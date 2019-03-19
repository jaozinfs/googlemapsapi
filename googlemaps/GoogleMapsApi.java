package com.findsolucoes.backpackpro.googlemaps;

import android.util.Log;

import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.CalculateEstimatedTime;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.EstimatedtimeResponse;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.TravelMode;
import com.findsolucoes.backpackpro.googlemaps.GoogleMapsUtils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.freetime.mob.utils.GeneralUtils;
import org.freetime.mob.webservices.RequestConfig;
import org.freetime.mob.webservices.RequestHeader;
import org.freetime.mob.webservices.RequestResult;
import org.freetime.mob.webservices.Requester;
import org.freetime.mob.webservices.Treaty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * João Victor
 */
public class GoogleMapsApi {

    private static final String TAG = "GoogleMapsApi";
    /**
     * Travel mode select
     */
    private TravelMode travelMode;

    /**
     * Estimate time travel origin to destine
     */
    private CalculateEstimatedTime calculateEstimatedTime;


    /**
     * Key google maps to make something and is needed
     */
    private String apiKey;

    /**
     * Google api constructor
     * @param apiKey
     */
    public GoogleMapsApi(String apiKey){ this.apiKey = apiKey; }


    /**
     * Set Travel mode
     * @param travelMode
     * @return
     */
    public GoogleMapsApi setTravelMode(TravelMode travelMode){
        this.travelMode = travelMode;
        return this;
    }


    /**
     * Calcule estimated time from origin point to end point
     * @param points
     */
    public void calculeteEstimatedTime(ArrayList<LatLng> points, Requester mRequester, final CalculateEstimatedTime calculateEstimatedTime){
        //security verifys
        this.calculateEstimatedTime = calculateEstimatedTime;

        if(mRequester != null && points != null && !points.isEmpty()){
            //Url to get estimated time

            String url = Utils.createUrlDirectionApi(travelMode, apiKey, points.get(0), points.get(points.size() -1));

            mRequester.prepare(Requester.RequestType.GET)
                    .setMode(Requester.RequestMode.FUTURE)
                    .setHeader(RequestHeader.newJsonHeader())
                    .setTreaty(new Treaty(false, false, false))
                    .setUrl(url)
                    .setConfig(RequestConfig.newGetConfig(5000))
                    .setOnResultListener(new GeneralUtils.OnResultListener<RequestResult>() {
                        @Override
                        public void onResult(RequestResult result) {
                            if(result.isSuccessfullyResponse()){
                                try {
                                    JSONObject json = new JSONObject(result.getResponse());

                                    JSONArray routeArray = json.getJSONArray("routes");
                                    JSONObject routes = routeArray.getJSONObject(0);

                                    JSONArray legsArray = routes.getJSONArray("legs");
                                    JSONObject newDisTimeOb = legsArray.getJSONObject(0);

                                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                                    Log.d(TAG, "distOb: "+distOb);
                                    Log.d(TAG, "timeOb: "+timeOb);

                                    calculateEstimatedTime.onEstimatedTimeResult(new EstimatedtimeResponse(timeOb.getString("text"), distOb.getString("text")));
                                }catch (Exception e){
                                    Log.d(TAG, "ERROR ON GET OBJECT: "+e.getMessage());
                                    calculateEstimatedTime.onEstimatedTimeError(e);
                                }
                            }else{
                                calculateEstimatedTime.onEstimatedTimeError(null);
                            }
                        }
                    }).request();
        }
    }

    /**
     *
     * @param points
     * @param googleMap
     */
    public void drawPolyneOptions(ArrayList<LatLng> points, GoogleMap googleMap){ }
}
