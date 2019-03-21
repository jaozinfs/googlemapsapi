package com.findsolucoes.backpackpro.googlemaps.DrawLineMaps;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public interface DrawMarkerListener {
    void onDrawMaker(ArrayList<Marker>markers, ArrayList<MarkerOptions> markerOptions);
    void onDrawMakerError(Exception e);
}
