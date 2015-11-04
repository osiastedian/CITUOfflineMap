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
import android.view.View;

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
public class CITMap extends SurfaceView implements SurfaceHolder.Callback{
    private List<BuildingPoint> buildings;
    private List<IntersectionPoint> intersections;
    private Paint placePaint;
    private Bitmap backgroundImage;

    public void setBackgroundImage(Bitmap source) {
        if(source!=null)
            backgroundImage = Bitmap.createScaledBitmap(source, this.getMeasuredWidth(), this.getMeasuredHeight(), true);
    }

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

    double max_distance = 100000;
    private HashMap<PlacePoint, Double> initalizeDistance() {
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
    int count = 0;
    @OnTouch(R.id.map)
    public boolean touchPoint(View view,MotionEvent event){
        SurfaceHolder holder = this.getHolder();
        Paint placePaint = new Paint();
        placePaint.setTextSize(30);
        placePaint.setColor(Color.WHITE);
        drawPoints();
        Canvas canvas  = holder.lockCanvas();
        canvas.drawText("X:" + (int) event.getX() + "\nY:" + (int) event.getY(), 0, 30, placePaint);
        count++;
        holder.unlockCanvasAndPost(canvas);
        return true;
    }

    public void drawPoints(){
        if(backgroundImage==null){
            Bitmap source = BitmapFactory.decodeResource(this.getResources(), R.drawable.backgroundmap);
            backgroundImage = Bitmap.createScaledBitmap(source, this.getMeasuredWidth(), this.getMeasuredHeight(), true);
        }

        SurfaceHolder holder = this.getHolder();
        Paint buildingPaint = new Paint();
        buildingPaint.setColor(Color.WHITE);
        Paint intersectionPaint = new Paint();
        intersectionPaint.setColor(Color.RED);
        Canvas canvas  = holder.lockCanvas();
        canvas.drawBitmap(backgroundImage, 0, 0, buildingPaint);
        for(PlacePoint building : this.buildings){
            canvas.drawCircle(building.getX(),building.getY(),10,buildingPaint);
        }
        for(PlacePoint intersect : this.intersections){
            canvas.drawCircle(intersect.getX(),intersect.getY(),10,intersectionPaint);
        }
        holder.unlockCanvasAndPost(canvas);
    }
    public void drawPath(LinkedList<PlacePoint> shortestPath) {
        Canvas canvas =this.getHolder().lockCanvas();
        Paint p = new Paint();
        p.setStrokeWidth(10);
        p.setColor(Color.BLUE);
        for (int i = 1; i < shortestPath.size(); i++) {
            Point source = new Point(shortestPath.get(i - 1).getX(), shortestPath.get(i - 1).getY());
            Point destination = new Point(shortestPath.get(i).getX(), shortestPath.get(i).getY());
            canvas.drawLine(source.x, source.y, destination.x, destination.y, p);
        }
        this.getHolder().unlockCanvasAndPost(canvas);
    }
}
