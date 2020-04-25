package com.eyal.togetherun.Run;

public class Pair {
    private String username;
    private double distance;

    public Pair(String username, double distance) {
        this.username = username;
        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
