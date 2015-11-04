package com.ecrc.cituofflinemap.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ecrc.cituofflinemap.R;
import com.ecrc.cituofflinemap.models.BuildingPoint;
import com.ecrc.cituofflinemap.models.IntersectionPoint;
import com.ecrc.cituofflinemap.models.PlacePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by ian on 10/23/15.
 */
public class CITMap extends SurfaceView implements  SurfaceHolder.Callback{
    private List<BuildingPoint> buildings;
    private List<IntersectionPoint> intersections;
    private Bitmap backgroundImage;
    private Paint textPaint = new Paint();
    private int textSize = 30;
    private int textColor = Color.WHITE;

    private Paint buildingPaint = new Paint();
    private float buildingPointSize = 10;
    private int buildingColor = Color.WHITE;

    private Paint intersectionPaint = new Paint();
    private float intersectionPointSize = 10;
    private int intersectionColor = Color.RED;

    private Paint pathPaint = new Paint();
    private float pathStrokeWidth = 10;
    private int pathColor = Color.BLUE;

    private PlacePoint recentSearch;


    /**
        Dijktras Algorithm (Shortest path)
        NOTE: DO NOT TOUCH
     **/
    private HashMap<PlacePoint,Double> distance ;
    private List<PlacePoint> settled;
    private List<PlacePoint> unsettled;
    private HashMap<PlacePoint, PlacePoint> predecessors;
    private double max_distance = 100000;


    public CITMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);
        // INITIALIZE PAINT OBJECTS
        textPaint.setTextSize(this.textSize);
        textPaint.setColor(this.textColor);
        buildingPaint.setColor(this.buildingColor);
        intersectionPaint.setColor(this.intersectionColor);
        pathPaint.setStrokeWidth(this.pathStrokeWidth);
        pathPaint.setColor(this.pathColor);
        this.getHolder().addCallback(this);

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

    public void dijkstraAlgorithm(PlacePoint source){
        distance = initializeDistance();
        settled = new ArrayList<>();
        unsettled = new ArrayList<>();
        predecessors = new HashMap<>();
        unsettled.add(source);
        distance.put(source, (double) 0.0f);
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


    private HashMap<PlacePoint, Double> initializeDistance() {
        HashMap<PlacePoint, Double> map = new HashMap<>();
        for (PlacePoint p :
                this.buildings) {
            map.put(p,max_distance);
        }
        for (PlacePoint p :
                this.intersections) {
            map.put(p, max_distance);
        }
        return map;
    }

    public void setConnections(String[] connectionArray){
        for(String connection : connectionArray) {
            String []places = connection.split(" ");
            if(places.length<2)
                continue;
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


    public LinkedList<PlacePoint> getShortestPath(PlacePoint source, PlacePoint destination) {
        if(recentSearch == null || recentSearch!=source) {
            this.dijkstraAlgorithm(source);
            recentSearch = source;
        }
        return this.getPath(destination);
    }

    @OnTouch(R.id.map)
    public boolean touchPoint(MotionEvent event){
        SurfaceHolder holder = this.getHolder();
        Canvas canvas  = holder.lockCanvas();
        this.drawPoints(canvas);
        canvas.drawText("X:" + (int) event.getX(), 0, 30, textPaint);
        canvas.drawText("Y:" + (int) event.getY(), 0, 60, textPaint);
        holder.unlockCanvasAndPost(canvas);
        return true;
    }

    public void drawPoints(Canvas canvas){
        if(backgroundImage==null){
            Bitmap source = BitmapFactory.decodeResource(this.getResources(), R.drawable.backgroundmap);
            backgroundImage = Bitmap.createScaledBitmap(source, this.getMeasuredWidth(), this.getMeasuredHeight(), true);
        }
        canvas.drawBitmap(backgroundImage, 0, 0, buildingPaint);
        for(PlacePoint building : this.buildings){
            canvas.drawCircle(building.getX(),building.getY(),this.buildingPointSize,buildingPaint);
        }
        for(PlacePoint intersect : this.intersections){
            canvas.drawCircle(intersect.getX(),intersect.getY(),this.intersectionPointSize,intersectionPaint);
        }

    }
    public void drawPath(LinkedList<PlacePoint> shortestPath) {
        Canvas canvas =this.getHolder().lockCanvas();
        this.drawPoints(canvas);
        for (int i = 1; i < shortestPath.size(); i++) {
            Point source = new Point(shortestPath.get(i - 1).getX(), shortestPath.get(i - 1).getY());
            Point destination = new Point(shortestPath.get(i).getX(), shortestPath.get(i).getY());
            canvas.drawLine(source.x, source.y, destination.x, destination.y, pathPaint);
        }
        this.getHolder().unlockCanvasAndPost(canvas);
    }
    public void setBackgroundImage(Bitmap source) {
        if(source!=null)
            backgroundImage = Bitmap.createScaledBitmap(source, this.getMeasuredWidth(), this.getMeasuredHeight(), true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        drawPoints(canvas);
        holder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}
