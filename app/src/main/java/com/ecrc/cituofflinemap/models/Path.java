package com.ecrc.cituofflinemap.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by ian on 10/25/15.
 */
public class Path {
    private double distance;
    private LinkedList<PlacePoint> places;

    public Path(double startDistance) {
        this.distance = startDistance;
        places = new LinkedList<>();
    }

    public Path(Path copyPath) {
        this.distance = copyPath.getDistance();
        this.places = copyPath.getPlaces();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LinkedList<PlacePoint> getPlaces() {
        return places;
    }

    public void setPlaces(LinkedList<PlacePoint> places) {
        this.places = places;
    }

    public boolean contains(PlacePoint connection) {
        for(PlacePoint con: this.getPlaces())
            if(con.equals(connection))
                return true;
        return false;
    }

    public boolean reached(PlacePoint destination) {
        return places.getLast().equals(destination);
    }
}
