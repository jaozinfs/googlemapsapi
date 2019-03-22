package findsolucoes.com.appcidade.Utils.googlemapsapi.AddMarkerClickListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public interface IAddMarkerClickListener {
    void onMarkerAdded(Marker marker, MarkerOptions markerOptions);
    void onErrorAddMarker(Exception e);
}

