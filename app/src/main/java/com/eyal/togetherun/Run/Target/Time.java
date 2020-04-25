package com.eyal.togetherun.Run.Target;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class Time {
    public static final int MAX_SECONDS = 59;
    private int secondsFromStart;

    public Time() {
        secondsFromStart = 0;
    }

    public Time(int seconds){
        this.secondsFromStart = seconds;
    }
    public Time(int h, int m, int s) {
        secondsFromStart = getSecondsFromTime(h , m , s);
    }

    public Time(Time duration) {
        this.secondsFromStart = duration.getSecondsFromStart();
    }



    public void updateTime(int min, int second){
        secondsFromStart = min * 60 + second;
    }
    public Time(String str){
        int h = Integer.valueOf(str.substring(0, 2));
        int m = Integer.valueOf(str.substring(3, 5));
        int s = Integer.valueOf(str.substring(6, 8));
        secondsFromStart = getSecondsFromTime(h, m , s);
    }
    private int getSecondsFromTime(int h, int m, int s) {
       return h * 3600 + m * 60 + s;
    }
    public int getMinute() {
        return (secondsFromStart/60);
    }

    public int getHour() {
        return (secondsFromStart / 3600);
    }

    public int getSecondsFromStart() {
        return secondsFromStart;
    }
    public void addTime(Time other){
        this.secondsFromStart += other.getSecondsFromStart();
    }

    public int getSecond() {
        return secondsFromStart % 60;
    }

    public void addSecond() {
        secondsFromStart += 1;
    }

    @NonNull
    @Override
    public String toString() {
        return addZero(getHour()) + ":" + addZero(getMinute()) + ":" + addZero(getSecond());
    }

    private String addZero(int n) {
        return n < 10 ? "0" + n : "" + n;
    }

    public boolean after(Time duration) {
        return duration.secondsFromStart <= this.secondsFromStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return secondsFromStart == time.secondsFromStart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secondsFromStart);
    }


    @Exclude
    public int getMinutes() {
        return secondsFromStart / 60;
    }
}

