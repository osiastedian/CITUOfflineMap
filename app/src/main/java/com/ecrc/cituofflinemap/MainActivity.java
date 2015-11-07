package com.ecrc.cituofflinemap;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.ecrc.cituofflinemap.CustomViews.CITMap;
import com.ecrc.cituofflinemap.models.BuildingPoint;
import com.ecrc.cituofflinemap.models.IntersectionPoint;
import com.ecrc.cituofflinemap.models.PlacePoint;

import org.xml.sax.ContentHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.fromSpinner)Spinner spinnerFrom;
    @Bind(R.id.toSpinner)Spinner spinnerTo;
    @Bind(R.id.map)CITMap map;
    @Bind(R.id.controlLayout)LinearLayout controlLayout;
    @Bind(R.id.findPathButton)Button findPathButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            loadBuildings();
            loadIntersections();
            loadConnections();

        } catch (Exception e) {
            e.printStackTrace();
        }

        findPathButton.post(new Runnable() {
            @Override
            public void run() {
                int width = controlLayout.getWidth();
                int spinnerWidth= (int)(width * .40);
                Spinner.LayoutParams param = spinnerFrom.getLayoutParams();
                param.width = spinnerWidth;
                spinnerFrom.setLayoutParams(param);
                spinnerTo.setLayoutParams(param);
                findPathButton.getLayoutParams().width = width - 2*spinnerWidth;
                ViewGroup.LayoutParams surface = map.getLayoutParams();

            }
        });

    }



    private void loadConnections() {
        String[] temp = this.getResources().getStringArray(R.array.connections);
        map.setConnections(temp);

    }

    private void loadIntersections() throws Exception {
        List<IntersectionPoint> array =  new ArrayList<>();
        for(PlacePoint p : getPoints(R.array.intersections,IntersectionPoint.class))
            array.add((IntersectionPoint)p);
        if(array.size()>0)
            map.setIntersections(array);
    }

    private void loadBuildings() throws Exception {
        ArrayAdapter<PlacePoint> buildings = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item);
        List<BuildingPoint> array = new ArrayList<>();
        for(PlacePoint p : getPoints(R.array.places,BuildingPoint.class))
            array.add((BuildingPoint)p);
        if(array.size()>0)
            buildings.addAll(array);
        spinnerFrom.setAdapter(buildings);
        spinnerTo.setAdapter(buildings);
        map.setBuildings(array);
    }



    public void findPathButton_Clicked(View view){
        // SET LAYOUT
        if(spinnerFrom.getSelectedItem()!=null & spinnerTo.getSelectedItem()!=null ) {
            PlacePoint selectedFrom = (PlacePoint) spinnerFrom.getSelectedItem();
            PlacePoint selectedTo = (PlacePoint) spinnerTo.getSelectedItem();

            LinkedList<PlacePoint> shortestPath = map.getShortestPath(selectedFrom, selectedTo);
            if(shortestPath!=null) {
                map.drawPath(shortestPath);
            }
        }
    }

    private PlacePoint[] getPoints(int id, Class convert) throws Exception {

        ArrayList<PlacePoint> points = new ArrayList<>();
        String[] temp = this.getResources().getStringArray(id);
        PlacePoint p = new PlacePoint();
        for(String s : temp){
            if(convert == BuildingPoint.class)
                p = new BuildingPoint();
            else if(convert == IntersectionPoint.class)
                p = new IntersectionPoint();
            p.parseData(s);
            points.add(p);
        }
        if(convert == BuildingPoint.class) {
            return points.toArray(new BuildingPoint[points.size()]);
        }
        else if(convert == IntersectionPoint.class)
            return points.toArray(new IntersectionPoint[points.size()]);
        else
            return points.toArray(new PlacePoint[points.size()]);
    }

}
