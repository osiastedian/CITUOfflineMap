package com.ecrc.cituofflinemap.models;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ian on 10/25/15.
 */
public class IntersectionPoint extends PlacePoint {
    public static int DEFAULT_COLOR = Color.GREEN;
    public static float DEFAULT_SIZE_HEIGHT = 10.0f;
    public static float DEFAULT_SIZE_WIDTH = 10.0f;
    private Paint foreColor;
    private Shape shape;


    public IntersectionPoint(){
        foreColor = new Paint();
        foreColor.setColor(DEFAULT_COLOR);
        shape = new OvalShape();
        shape.resize(DEFAULT_SIZE_WIDTH,DEFAULT_SIZE_HEIGHT);

    }

   public Paint getForeColor() {
        return foreColor;
    }

    public void setForeColor(Paint foreColor) {
        this.foreColor = foreColor;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
