package findsolucoes.com.appcidade.Utils.googlemapsapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

import findsolucoes.com.appcidade.R;
import findsolucoes.com.appcidade.Utils.Utilits;
import findsolucoes.com.appcidade.Utils.googlemapsapi.AddMarkerClickListener.IAddMarkerClickListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.AddMarkerClickListener.IAddMarkerClickListenerWithCustomModal;
import findsolucoes.com.appcidade.Utils.googlemapsapi.DeleteMarker.IDeleteMarkerListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.DrawLineMaps.DrawMarkerListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.CalculateEstimatedTimeListener;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.EstimatedtimeResponse;
import findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime.TravelMode;
import findsolucoes.com.appcidade.Utils.googlemapsapi.GoogleMapsUtils.Utils;
import findsolucoes.com.appcidade.view.controller.transit.model.Line;

/**
 * João Victor
 */
public class GoogleMapsApi {

    //draw WayPoints mode
    public static int DRAW_MARKER_WITH_WAY_POINT = 1;
    public static int DRAW_MARKER_WITHOUT_WAY_POINT = 0;


    //Custom dialog title marker when touch in map
    private static boolean showCustomModalAddMarker = false;
    private static String titleOfshowCustomModalAddMarker;
    private static String msgOfshowCustomModalAddMarker;
    private static  Activity activityTokenModalCreate;

    //Delete marker on click on
    private static IDeleteMarkerListener iDeleteMarkerListener;
    private static Marker markerStateON;

    //Draw polyline when add new marker on click in map
    private static boolean drawPolylineWhenAddNewMarker;
    private static PolylineOptions polylineOptionsWhenAddNewMarker;
    private static Polyline polylineWhenAddNewMarker;

    //
    private static int drawMakerWithWayPointMode = DRAW_MARKER_WITHOUT_WAY_POINT;
    //
    private static boolean zoomMapAfterLoadMap = false;
    //
    private static LatLng zoomPosition;
    //
    private static ArrayList<LatLng> customWayPoints = new ArrayList<>();

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
    private Activity context;
    private static Button dellMarker;

    /**
     * Google api constructor
     * @param apiKey
     */
    public GoogleMapsApi(Activity context, String apiKey, GoogleMap googleMap) {
        this.context = context;
        this.apiKey = apiKey;
        this.googleMap = googleMap;
        createDellButton();
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
     * @return
     */
    public GoogleMapsApi setDrawMarkerWithWayPoint(){
        drawMakerWithWayPointMode = DRAW_MARKER_WITH_WAY_POINT;
        return this;
    }
    /**
     * set draw marker with way point and default title
     * @param defaultTitle
     * @return
     */
    public GoogleMapsApi setDrawMarkerWithWayPoint(String defaultTitle){
        drawMakerWithWayPointMode = DRAW_MARKER_WITH_WAY_POINT;
        defaultTitleWayPointsMarkers = defaultTitle;
        return this;
    }
    /**
     * Set custom waypoints in rout
     * @param latLngs
     * @return
     */
    public GoogleMapsApi setCustomWayPoints(ArrayList<LatLng> latLngs){
        customWayPoints = latLngs;
        return this;
    }
    /**
     *  DRAW LINE FROM ROUT AND CREATE WAYPOINTS
     * @param points
     * @param
     */
    public void drawPolyneOptions(ArrayList<LatLng> points, DrawMarkerListener drawMarkerListener){
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

            //custom waypoints
            ArrayList<MarkerOptions> markersOptions;
            if(customWayPoints.isEmpty()) markersOptions = Utils.getMarkerOptions(points, defaultTitleWayPointsMarkers);
            else markersOptions = Utils.getMarkerOptions(customWayPoints, defaultTitleWayPointsMarkers);


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
    /**
     * Create marker when click in map view
     * @param listener
     */
    public void addMarkerTouchListener(final IAddMarkerClickListener listener){
        //


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(final LatLng latLng) {

                if(showCustomModalAddMarker){
                    showCustomModalAddMarker(new IAddMarkerClickListenerWithCustomModal() {
                        @Override
                        public void onSetText(String title) {
                            MarkerOptions markerOptions = new MarkerOptions().title(title).position(latLng);
                            Marker marker = googleMap.addMarker(markerOptions);
                            if(drawPolylineWhenAddNewMarker){
                                if(polylineWhenAddNewMarker == null){
                                    polylineWhenAddNewMarker = googleMap.addPolyline(polylineOptionsWhenAddNewMarker);
                                    return;
                                }
                                polylineOptionsWhenAddNewMarker.add(marker.getPosition());
                            }
                            synchronized (googleMap) {
                                googleMap.addPolyline(polylineOptionsWhenAddNewMarker);
                                listener.onMarkerAdded(marker, markerOptions);
                            }

                        }
                    });
                }else{
                    MarkerOptions markerOptions = new MarkerOptions().title(defaultTitleWayPointsMarkers).position(latLng);
                    Marker marker = googleMap.addMarker(markerOptions);
                    if(drawPolylineWhenAddNewMarker){
                        polylineOptionsWhenAddNewMarker.add(marker.getPosition());
                    }
                    synchronized (googleMap) {
                        googleMap.addPolyline(polylineOptionsWhenAddNewMarker);
                        listener.onMarkerAdded(marker, markerOptions);
                    }
                }

                if(dellMarker.getVisibility() == View.VISIBLE){
                    dellMarker.setVisibility(View.GONE);
                }
            }
        });
    }
    /**
     * set to show modal to create custom marker when click in map
     * @param context
     * @param positiveButtonTitle
     * @return
     */
    public GoogleMapsApi withOptionsMarker(final Activity context, String positiveButtonTitle, String mesageButtinTitle) {
        showCustomModalAddMarker = true;
        titleOfshowCustomModalAddMarker = positiveButtonTitle;
        activityTokenModalCreate = context;
        msgOfshowCustomModalAddMarker = mesageButtinTitle;
        return this;
    }
    /**
     * Show modal with listener when click in ok button to create custom marker with title
     * @param listener
     */
    public void showCustomModalAddMarker(final IAddMarkerClickListenerWithCustomModal listener){
        //context to create modal
        final Activity context = activityTokenModalCreate;

        //create modal
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText titleMrk = Utils.getCustomAddMarkerWhenTouckMap(context);
        alert.setView(titleMrk);

        alert.setTitle(titleOfshowCustomModalAddMarker);
        alert.setMessage(msgOfshowCustomModalAddMarker);

        //set title of marker
        alert.setPositiveButton(titleOfshowCustomModalAddMarker, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(titleMrk.getText().toString().trim().isEmpty() || titleMrk.getText().toString().equals("")){
                    Toast.makeText(context, "Add title of new marker !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(listener == null){
                    dialog.dismiss();
                }
                listener.onSetText(titleMrk.getText().toString());
                dialog.dismiss();
            }
        });

        //dismiss modal
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
    /**
     * Delete marker when click on
     * @return
     */
    public GoogleMapsApi setDeleteMarker(){
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                dellMarker.setVisibility(View.VISIBLE);
                markerStateON = marker;
                return false;
            }
        });
        return this;
    }
    /**
     * Dell button marker
     */
    public void createDellButton(){
        dellMarker = new Button(context);
        dellMarker.setText("Remove Marker");
        dellMarker.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        dellMarker.setVisibility(View.GONE);
        dellMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markerStateON == null) {
                    return;
                }

                Utils.removeMarkerGMps(googleMap, context, markerStateON, drawPolylineWhenAddNewMarker, polylineOptionsWhenAddNewMarker, polylineWhenAddNewMarker);
                if(iDeleteMarkerListener != null)iDeleteMarkerListener.onDeleteMarker(markerStateON);

                dellMarker.setVisibility(View.GONE);
            }
        });

        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (context.findViewById(android.R.id.content))).getChildAt(0);
        viewGroup.addView(dellMarker);
    }
    /**
     * Listener delete callbakc
     * @param listener
     * @return
     */
    public GoogleMapsApi setDeleteMarkerListener(IDeleteMarkerListener listener){
        iDeleteMarkerListener = listener;
        return this;
    }
    /**
     * Draw polyline when add new marker click in maps
     * @return
     */
    public GoogleMapsApi setDrawPolylineWhenAddMarker(){
        drawPolylineWhenAddNewMarker = true;
        polylineOptionsWhenAddNewMarker = new PolylineOptions();
        return this;
    }

}
