package com.findsolucoes.backpackpro.googlemaps.EstimatedTime;

public class EstimatedtimeResponse {

    String estimatedTime;
    String estimatedDistance;

    public EstimatedtimeResponse(String estimatedTime, String estimatedDistance) {
        this.estimatedTime = estimatedTime;
        this.estimatedDistance = estimatedDistance;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public String getEstimatedDistance() {
        return estimatedDistance;
    }
}
