# googlemapsapi

# Public API to help the developer with Google Maps tools

  - Create routes from an array of points
  - Create waypoints in rout an array of points
  - Simple create Marker an click touch in map
  - Get estimated time in travel points 

### Development

Want to contribute? Great!

Make a change in your file and instantanously see your updates!

Open your favorite Terminal and run these commands.

# Usage Exemple
### First need start api when map is ready

```@Override
    public void onMapReady(GoogleMap gm) {
        googleMapsApi = new GoogleMapsApi(GetEstimatedTimeActivity.this, "tokenGoogleMaps", gm);
    } 
```
    
### Here we calculate the estimated travel from an array of points
```
googleMapsApi.setTravelMode(TravelMode.WALKING).calculeteEstimatedTime(latLngs, requester,
                new CalculateEstimatedTimeListener() {
                    @Override
                    public void onEstimatedTimeResult(EstimatedtimeResponse estimatedtimeResponse) {}
                    @Override
                    public void onEstimatedTimeError(Exception e) {}
                });
 ```
                
### Here we add markers when we click on the map, and we can choose the option to delete a marker, draw the plotted route and among other options
```
 googleMapsApi.setDeleteMarkerListener(new IDeleteMarkerListener() {
                    @Override
                    public void onDeleteMarker(Marker marker) {}
                    @Override
                    public void onDeleteMarkerError(Exception e) {}
                }).setDeleteMarker()
                .setDrawPolylineWhenAddMarker()
                .withOptionsMarker("Add", "Title of new marker ?")
                .addMarkerTouchListener(new IAddMarkerClickListener() {
                    @Override
                    public void onMarkerAdded(Marker marker, MarkerOptions markerOptions) {}
                    @Override
                    public void onErrorAddMarker(Exception e) { }
                });
 ```
 
