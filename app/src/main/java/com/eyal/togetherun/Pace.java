package com.eyal.togetherun;

import com.eyal.togetherun.Run.Target.Time;

public class Pace {
    private Time timeToCompleteKm;


    public Pace() {
        timeToCompleteKm = new Time();
    }
    public Pace(double time){
        timeToCompleteKm = new Time();
        updateTime(time);
    }
    public Pace(Time time){
        timeToCompleteKm = new Time(time);
    }

    public Pace(Pace other) {
        this.timeToCompleteKm = new Time(other.timeToCompleteKm);
    }

    @Override
    public String toString() {
        return timeToCompleteKm.getMinute() +"'"+timeToCompleteKm.getSecond() + "\"";
    }


    public Time getTimeToCompleteKm() {
        return timeToCompleteKm;
    }

    public void setTimeToCompleteKm(Time timeToCompleteKm) {
        this.timeToCompleteKm = timeToCompleteKm;
    }


    /**
     * gets pace
     * @param time
     */
    public void updateTime(double time) {
        int minute = (int) time;
        double secondsDecimal = time - (int)time;
        int seconds = (int)(secondsDecimal * 60);
        timeToCompleteKm.updateTime(minute, seconds);
    }
}
