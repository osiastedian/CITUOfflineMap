package com.ecrc.cituofflinemap.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ecrc.cituofflinemap.models.BuildingPoint;
import com.ecrc.cituofflinemap.models.IntersectionPoint;
import com.ecrc.cituofflinemap.models.PlacePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ian on 10/23/15.
 */
public class CITMap extends SurfaceView implements SurfaceHolder.Callback{
    private List<BuildingPoint> buildings;
    private List<IntersectionPoint> intersections;
    private Paint placePaint;
    private Bitmap backgroundImage;

    public CITMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);
        super.getHolder().addCallback(this);
    }

    public List<BuildingPoint> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingPoint> buildings) {
        this.buildings = buildings;
    }

    public List<IntersectionPoint> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<IntersectionPoint> intersections) {
        this.intersections = intersections;
    }

    HashMap<PlacePoint,Double> distance ;
    List<PlacePoint> settled;
    List<PlacePoint> unsettled;
    HashMap<PlacePoint, PlacePoint> predecessors;
    public void dijkstraAlgorithm(PlacePoint source){
        distance = initalizeDistance();
        settled = new ArrayList<>();
        unsettled = new ArrayList<>();
        predecessors = new HashMap<>();
        unsettled.add(source);
        distance.put(source, Double.valueOf(0.0f));
        while(!unsettled.isEmpty()){
            PlacePoint evaluationPoint = getPointWithLowestDistance(unsettled);
            unsettled.remove(evaluationPoint);
            settled.add(evaluationPoint);
            evaluateNeighbors(evaluationPoint);
        }

    }

    private void evaluateNeighbors(PlacePoint evaluationPoint) {
        for(PlacePoint destination: evaluationPoint.getConnections()){
            if(!settled.contains(destination)){
                double edgeDistance = PlacePoint.getDistance(evaluationPoint,destination);
                double newDistance = distance.get(evaluationPoint) + edgeDistance;
                if(distance.get(destination) > newDistance){
                    distance.put(destination,newDistance);
                    predecessors.put(destination, evaluationPoint);
                    unsettled.add(destination);
                }
            }
        }
    }

    public LinkedList<PlacePoint> getPath(PlacePoint target) {
        LinkedList<PlacePoint> path = new LinkedList<>();
        PlacePoint step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    private PlacePoint getPointWithLowestDistance(List<PlacePoint> unsettled) {
        PlacePoint current = unsettled.get(0);
        for (PlacePoint p :
                unsettled) {
            if(distance.get(current)>distance.get(p))
                current = p;
        }
        return current;
    }

    double max_distance = 100000;
    private HashMap<PlacePoint, Double> initalizeDistance() {
        HashMap<PlacePoint, Double> map = new HashMap<>();
        for (PlacePoint p :
                this.buildings) {
            map.put(p,max_distance);
        }
        for (PlacePoint p :
                this.intersections) {
            map.put(p,max_distance);
        }
        return map;
    }

    public void setConnections(String[] connectionArray){
        for(String connection : connectionArray) {
            String []places = connection.split(" ");
            if(places.length<2)
                continue;;
            addConnection(places[0],places[1]);
            addConnection(places[1], places[0]);
        }
    }

    public void addConnection(String pointName1,String pointName2){
        PlacePoint point1 = getPlacePointByName(pointName1);
        PlacePoint point2 = getPlacePointByName(pointName2);
        if(point1!=null && point2!=null) {
            if(point1 instanceof BuildingPoint)
                point1.addConnection(point2);
            if(point1 instanceof IntersectionPoint)
                point1.addConnection(point2);
        }
    }

    public PlacePoint getPlacePointByName(String s){
        PlacePoint object = findBuildingPoint(s);
        if(object==null)
            object = findIntersectionPoint(s);
        return object;
    }

    private IntersectionPoint findIntersectionPoint(String s) {
        for(IntersectionPoint point: intersections)
            if(point.equals(s))
                return point;
        return null;
    }

    private BuildingPoint findBuildingPoint(String s){
        for(BuildingPoint point: buildings)
            if(point.equals(s))
                return point;
        return null;
    }

    // SurfaceHolder.Callback Methods
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    PlacePoint recentSearch;
    public LinkedList<PlacePoint> getShortestPath(PlacePoint source, PlacePoint destination) {
        if(recentSearch == null || recentSearch!=source) {
            this.dijkstraAlgorithm(source);
            recentSearch = source;
        }
        return this.getPath(destination);
    }
}
