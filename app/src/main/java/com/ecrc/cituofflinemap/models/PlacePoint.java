package com.ecrc.cituofflinemap.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 10/22/15.
 */
public class PlacePoint {

    private int X;
    private int Y;
    private String name;
    private String pictureFilePath;
    private List<PlacePoint> connections;

    public PlacePoint() {
        connections = new ArrayList<>();
    }

    public void addConnection(PlacePoint p){ this.connections.add(p); }
    public List<PlacePoint> getConnections(){ return this.connections;}

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public void parseData(String s) throws Exception {
        String[] split = s.split(" ");
        if(split.length<4)
            throw new Exception("Wrong Format");
        else
        {
            this.setName(split[0]);
            this.setX(Integer.parseInt(split[1]));
            this.setY(Integer.parseInt(split[2]));
            this.setPictureFilePath(split[3]);
        }
    }

    @Override
    public String toString() {
        return this.name.replace('_', ' ');
    }

    @Override
    public boolean equals(Object o) {
        boolean ok = false;
        if(o!=null) {
            if(o instanceof String){
                ok = name.contentEquals(o.toString());
            }
            else if(o instanceof PlacePoint){
                PlacePoint obj = (PlacePoint)o;
                ok=  obj.getName().contentEquals(this.getName());
            }
        }
        return ok;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static double getDistance(PlacePoint point1, PlacePoint point2){
        return  Math.sqrt(Math.pow(point1.getX()-point2.getX(),2) + Math.pow(point1.getY()-point2.getY(),2));

    }
}
