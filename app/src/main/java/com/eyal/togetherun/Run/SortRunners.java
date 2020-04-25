package com.eyal.togetherun.Run;

import java.util.Comparator;

public class SortRunners implements Comparator<Runner> {

    public static final double FACTOR = 0.1; //200 meters different

    @Override
    public int compare(Runner o1, Runner o2) {
        if (Math.abs(o1.getDistance() - o2.getDistance()) > FACTOR)
            return (o1.getDistance() < (o2.getDistance())) ? -1 : 1;

        return (!o1.getTime().after(o2.getTime())) ? 1 : -1;
    }

}
