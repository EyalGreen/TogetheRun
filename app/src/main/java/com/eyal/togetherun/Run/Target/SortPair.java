package com.eyal.togetherun.Run.Target;

import com.eyal.togetherun.Run.Pair;

import java.util.Comparator;

public class SortPair implements Comparator<Pair> {
    @Override
    public int compare(Pair o1, Pair o2) {
        return o1.getDistance() > o2.getDistance() ? -1 : 1;
    }
}
