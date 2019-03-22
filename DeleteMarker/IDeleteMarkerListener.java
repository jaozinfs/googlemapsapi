package findsolucoes.com.appcidade.Utils.googlemapsapi.DeleteMarker;

import com.google.android.gms.maps.model.Marker;

public interface IDeleteMarkerListener {
    void onDeleteMarker(Marker marker);
    void onDeleteMarkerError(Exception e);
}
