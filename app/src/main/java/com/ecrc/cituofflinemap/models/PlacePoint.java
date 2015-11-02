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

    public Path findPathTo(PlacePoint destination,Path currentPath){
        Path toReturn = currentPath;
        List<Path> reachedPaths = new ArrayList<>();
        List<PlacePoint> remainingConnections = new ArrayList<>();
        for(PlacePoint connection: this.connections){
            if(!currentPath.contains(connection))
                remainingConnections.add(connection);
        }
        if(remainingConnections.size()>0)
        {
            for (PlacePoint connection : remainingConnections)
            {
                Path tempPath = new Path(currentPath);
                tempPath.setDistance(tempPath.getDistance() + PlacePoint.getDistance(this,connection));
                tempPath.getPlaces().add(tempPath.getPlaces().size(),connection); // ADD TO LAST
                if (destination.equals(connection.getName())) {
                    reachedPaths.add(tempPath);
                }
                else
                {
                    Path p = connection.findPathTo(destination,tempPath);
                    if (p.reached(destination))
                        reachedPaths.add(reachedPaths.size(), p);
                }
            }
            if(reachedPaths.size() > 0)
            {
                Path minPath = reachedPaths.get(0);
                for(Path path: reachedPaths) {
                    if (path.getDistance() < minPath.getDistance())
                        minPath = path;
                }
                return minPath;
            }
        }
        return toReturn;
    } // Shortest Path not working

    public static double getDistance(PlacePoint point1, PlacePoint point2){
        return  Math.sqrt(Math.pow(point1.getX()-point2.getX(),2) + Math.pow(point1.getY()-point2.getY(),2));

    }
}
