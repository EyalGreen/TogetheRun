package com.eyal.togetherun.Run;

import androidx.annotation.Nullable;

import com.eyal.togetherun.Pace;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.Run.Target.Time;
import com.eyal.togetherun.User;
import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.Objects;

public class Runner {
    private double distance = 0; //km
    private Pace pace = new Pace();
    private Time time = new Time();
    private User user;

    private boolean isFinish = false;

    @Exclude
    public boolean isFinish() {
        return isFinish;
    }

    @Exclude
    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    private boolean ready = false;


    public Runner(Runner runner) {
        if (runner == null) return;
        this.distance = runner.distance;
        if (runner.pace != null) {
            this.pace = new Pace(runner.pace);
        }

        if (runner.time != null) {
            this.time = new Time(runner.time);
        }
        this.user = new User(runner.user);
        this.ready = runner.ready;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Runner(User user) {
        this.user = user;

    }

    public Runner() {
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = new Time(time);
    }

    public Runner(double distance, Pace pace, Time time, User user) {
        this.distance = distance;
        this.pace = pace;
        this.time = time;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Runner(double distance, Pace pace, Time time) {
        this.distance = distance;
        this.pace = pace;
        this.time = time;

    }

    public double getDistance() {
        return distance;
    }

    public String formatDistance() {
        return String.format("%.2f", distance);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Pace getPace() {
        return pace;
    }

    public void setPace(Pace pace) {
        this.pace = pace;
    }

    public String getAvePace() {
        return user.getAveragePace().toString();
    }

    private double calculateSpeed(double distance, double time) {
        distance /= time;
        double calculatedSpeed = distance; //meters / 1 second
        calculatedSpeed *= 3.6; // km / 1h
        if (calculatedSpeed != 0)
            calculatedSpeed = 60 / calculatedSpeed; // min/1km
        return calculatedSpeed;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null) {
            Runner runner = (Runner)obj;
            return runner.user.getUsername().equals(user.getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, pace, time, user, ready);
    }

    public Double generatePlace(Run run) {
        TargetOfRun targetOfRun = run.getTarget();

        double distanceTarget = run.getTarget().getDistance();

            if (!isFinish) {
                //loser
                return 0.0 - (double)run.getRunners().size() + run.finishersCounter - 1.0;
            } else {
                //finisher
                if (targetOfRun.isByDistance()){
                    return distanceTarget + (double)run.getRunners().size() - (double)run.finishersCounter + 1.0;
                }
                return distance;
            }
    }

    public void calculatePace(double distance, Time time) {
        pace = calculateAndReturnPace(distance, time);
    }

    @Exclude
    public static Pace calculateAndReturnPace(double distance, Time time ){
       return calculateAndReturnPace(distance, time.getSecondsFromStart());
    }
    @Exclude
    public static Pace calculateAndReturnPace(double distance, int seconds ){
        if (distance == 0)
            return new Pace();
        else{
            Time newTime = new Time(Math.round(((float)(seconds) / (float)(distance))));
            return new Pace(newTime);
        }
    }


    public void updateUserTotals() {
        this.user.addDistance(distance);
        this.user.addTime(time);
    }
}
