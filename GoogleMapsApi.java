package com.findsolucoes.backpackpro.googlemaps;

import android.content.Context;
import android.util.Log;

import com.findsolucoes.backpackpro.googlemaps.DrawLineMaps.DrawMarkerListener;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.CalculateEstimatedTimeListener;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.EstimatedtimeResponse;
import com.findsolucoes.backpackpro.googlemaps.EstimatedTime.TravelMode;
import com.findsolucoes.backpackpro.googlemaps.GoogleMapsUtils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


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

    //draw WayPoints mode
    public static int DRAW_MARKER_WITH_WAY_POINT = 1;


    public static int DRAW_MARKER_WITHOUT_WAY_POINT = 0;



    private static int drawMakerWithWayPointMode;
    private static boolean zoomMapAfterLoadMap = false;
    private static LatLng zoomPosition;

    private static String defaultTitleWayPointsMarkers = "Parada";

    private static final String TAG = "GoogleMapsApi";
    /**
     * Travel mode select
     */
    private TravelMode travelMode;

    /**
     * Estimate time travel origin to destine
     */
    private CalculateEstimatedTimeListener calculateEstimatedTimeListener;


    /**
     * Key google maps to make something and is needed
     */
    private String apiKey;
    private GoogleMap googleMap;
    private Context context;

    /**
     * Google api constructor
     * @param apiKey
     */
    public GoogleMapsApi(Context context, String apiKey, GoogleMap googleMap) {
        this.context = context;
        this.apiKey = apiKey;
        this.googleMap = googleMap;
    }
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
    public void calculeteEstimatedTime(ArrayList<LatLng> points, Requester mRequester, final CalculateEstimatedTimeListener calculateEstimatedTimeListener){
        //security verifys
        this.calculateEstimatedTimeListener = calculateEstimatedTimeListener;

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

                                    calculateEstimatedTimeListener.onEstimatedTimeResult(new EstimatedtimeResponse(timeOb.getString("text"), distOb.getString("text")));
                                }catch (Exception e){
                                    Log.d(TAG, "ERROR ON GET OBJECT: "+e.getMessage());
                                    calculateEstimatedTimeListener.onEstimatedTimeError(e);
                                }
                            }else{
                                calculateEstimatedTimeListener.onEstimatedTimeError(null);
                            }
                        }
                    }).request();
        }
    }


    /**
     * SET ZOOM MODE
     * @param position
     * @return
     */
    public GoogleMapsApi setZoomMapAfterLoadMap(LatLng position) {
        zoomMapAfterLoadMap = true;
        zoomPosition = position;
        return this;
    }

    /**
     * set Draw Marker with way point
     * @param wayPointDraw
     * @return
     */
    public GoogleMapsApi setDrawMarkerWithWayPoint(int wayPointDraw){
        if(wayPointDraw == DRAW_MARKER_WITH_WAY_POINT)drawMakerWithWayPointMode = DRAW_MARKER_WITH_WAY_POINT;
        else drawMakerWithWayPointMode = DRAW_MARKER_WITHOUT_WAY_POINT;
        return this;
    }

    /**
     * set draw marker with way point and default title
     * @param wayPointDraw
     * @param defaultTitle
     * @return
     */
    public GoogleMapsApi setDrawMarkerWithWayPoint(int wayPointDraw, String defaultTitle){
        if(wayPointDraw == DRAW_MARKER_WITH_WAY_POINT){
            drawMakerWithWayPointMode = DRAW_MARKER_WITH_WAY_POINT;
            defaultTitleWayPointsMarkers = defaultTitle;
        }
        else drawMakerWithWayPointMode = DRAW_MARKER_WITHOUT_WAY_POINT;
        return this;
    }

    /**
     *  DRAW LINE FROM ROUT AND CREATE WAYPOINTS
     * @param points
     * @param googleMap
     */
    public void drawPolyneOptions(ArrayList<LatLng> points, GoogleMap googleMap, DrawMarkerListener drawMarkerListener){
        if(googleMap == null){
            drawMarkerListener.onDrawMakerError(new Exception("Error on draw in maps, GoogleMaps viwe is null"));
            return;
        }
        if(points.isEmpty()){
            drawMarkerListener.onDrawMakerError(new Exception("Error on draw in maps, GoogleMaps rout is empty"));
            return;
        }
        if(zoomMapAfterLoadMap){
            if(zoomPosition == null){
                drawMarkerListener.onDrawMakerError(new Exception("Error on draw in maps, GoogleMaps zoom position is null"));
                return;
            }
        }
        if(drawMakerWithWayPointMode == 1)
            if(defaultTitleWayPointsMarkers.equals("") || defaultTitleWayPointsMarkers.trim().isEmpty())
                drawMarkerListener.onDrawMakerError(new Exception("Error on draw in maps, GoogleMaps waypoint default name is empty"));


        //draw line in map
        googleMap.addPolyline(Utils.getRoutLine(points));

        if(drawMakerWithWayPointMode == 1){
            ArrayList<MarkerOptions> markersOptions = Utils.getMarkerOptions(points, defaultTitleWayPointsMarkers);
            synchronized(googleMap) {

                ArrayList<Marker> markers = Utils.getMarkerFromOptions(markersOptions, googleMap);

                googleMap.notifyAll();

                if(zoomMapAfterLoadMap){
                    googleMap.animateCamera(Utils.getCameraUpdate(context, zoomPosition));
                }

                drawMarkerListener.onDrawMaker(markers, markersOptions);
            }
        }
    }

}
