package com.eyal.togetherun.Run.Target;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import static com.eyal.togetherun.MainActivity.DISTANCE_STR;
import static com.eyal.togetherun.MainActivity.DURATION_STR;

public class TargetOfRun {
    public static final double MIN_DISTANCE = 0.5;
    protected double distance;
    protected Time duration;
    protected boolean isByDistance = false;
    public boolean isFinish(double distance, Time duration){
        if (isByDistance){
            return distance >= this.distance;
        }else{
            return duration.after(this.duration);
        }
    }
//    public void handleIsByDistance(){
//        if (duration == null)
//            isByDistance = true;
//        else if (duration.equals(new Time()))
//            isByDistance = true;
//        else
//            isByDistance = false;
//    }
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public TargetOfRun() {
//        handleIsByDistance();
    }

    public TargetOfRun(Time duration) {
        this.duration = new Time(duration);
        this.distance = MIN_DISTANCE;
        isByDistance = false;
    }
    public TargetOfRun(TargetOfRun other){
        this.distance = other.distance;
        this.duration = new Time(other.duration);
        isByDistance = other.isByDistance;
    }
    public TargetOfRun(double distance) {
        this.distance = distance;
        this.isByDistance = true;
        this.duration = new Time(0, 0, 0);
    }

    @NonNull
    @Override
    public String toString() {
        if (isByDistance) {
            return distance + "km";
        }
        return "in time: " + duration.toString();
    }

    @Exclude
    public String getSelectedMode() {
        if (!isByDistance)
            return DURATION_STR;
        return DISTANCE_STR;
    }

    @Exclude
    public String getValue(){
        String selectedMode = getSelectedMode();
        if (selectedMode.equals(DISTANCE_STR)) {
          return distance + "";

        } else if (selectedMode.equals(DURATION_STR)) {
            return duration.toString();
        }
        return null;
    }


    public boolean isByDistance() {
        return isByDistance;
    }

    public void setByDistance(boolean byDistance) {
        isByDistance = byDistance;
    }
//    public Bundle getValue(){
//        Bundle intent = new Bundle();
//
//        String selectedMode = getSelectedMode();
//        if (selectedMode.equals(DISTANCE_STR)) {
//            intent.putString("targetDistance", distance + "");
//
//        } else if (selectedMode.equals(DURATION_STR)) {
//            intent.putString("targetDuration", duration.toString());
//        }
//        return intent;
//    }
}
