package com.ecrc.cituofflinemap.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.ecrc.cituofflinemap.R;
import com.ecrc.cituofflinemap.models.BuildingPoint;
import com.ecrc.cituofflinemap.models.IntersectionPoint;
import com.ecrc.cituofflinemap.models.Path;
import com.ecrc.cituofflinemap.models.PlacePoint;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

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

    public Path getShortestPath(String from,String to){
        PlacePoint pointFrom = getPlacePointByName(from);
        PlacePoint pointTo = getPlacePointByName(to);
        return getShortestPath(pointFrom,pointTo);
    }
    public Path getShortestPath(PlacePoint from,PlacePoint to){
        Path start =  new Path(0);
        start.getPlaces().add(from);
        Path p = from.findPathTo(to,start);
        if(p.reached(to))
            return p;
        else return null;
    }

    public void setConnections(String[] connectionArray){
        for(String connection : connectionArray) {
            String []places = connection.split(" ");
            if(places.length<2)
                continue;;
            addConnection(places[0],places[1]);
            addConnection(places[1],places[0]);
        }
    }

    public void addConnection(String pointName1,String pointName2){
        PlacePoint point1 = getPlacePointByName(pointName1);
        PlacePoint point2 = getPlacePointByName(pointName2);
        if(point1!=null && point2!=null) {
            if(point1 instanceof BuildingPoint)
                ((BuildingPoint) point1).addConnection(point2);
            if(point1 instanceof IntersectionPoint)
                ((IntersectionPoint) point1).addConnection(point2);
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
}
