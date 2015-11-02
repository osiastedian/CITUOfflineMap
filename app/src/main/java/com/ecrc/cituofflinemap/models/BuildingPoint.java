package com.ecrc.cituofflinemap.models;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 10/25/15.
 */
public class BuildingPoint extends PlacePoint{
    public static final Rect BIG = new Rect(20,20,20,20);
    public static final Rect MEDIUM = new Rect(20,20,20,20);
    public static final Rect SMALL = new Rect(20,20,20,20);

    private Paint foreColor;
    private Rect shape;

    public Paint getForeColor() {
        return foreColor;
    }

    public void setForeColor(Paint foreColor) {
        this.foreColor = foreColor;
    }

    public Rect getShape() {
        return shape;
    }

    public void setShape(Rect shape) {
        this.shape = shape;
    }

    @Override
    public void parseData(String s) throws Exception {
        super.parseData(s);
        foreColor = new Paint();
        String[] app = s.split(" ");
        foreColor.setColor(Color.parseColor(app[4]));
        if(app[5].equals("BIG"))
            this.shape = BuildingPoint.BIG;
        else if(app[5].equals("MEDIUM"))
            this.shape = BuildingPoint.MEDIUM;
        else if(app[5].equals("SMALL"))
            this.shape = BuildingPoint.SMALL;
        else
            throw new Exception("Size not determined.");
    }


}
