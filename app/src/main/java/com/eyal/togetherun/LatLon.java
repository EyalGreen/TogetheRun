package com.eyal.togetherun;

public class LatLon {
    private double lat;
    private double lon;

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ") ";
    }

    public LatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
