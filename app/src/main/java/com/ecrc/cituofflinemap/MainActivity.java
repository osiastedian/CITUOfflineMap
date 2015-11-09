package com.ecrc.cituofflinemap;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ecrc.cituofflinemap.CustomViews.CITMap;
import com.ecrc.cituofflinemap.models.BuildingPoint;
import com.ecrc.cituofflinemap.models.IntersectionPoint;
import com.ecrc.cituofflinemap.models.PlacePoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.fromSpinner)Spinner spinnerFrom;
    @Bind(R.id.toSpinner)Spinner spinnerTo;
    @Bind(R.id.map)CITMap map;
    @Bind(R.id.controlLayout)LinearLayout controlLayout;
    @Bind(R.id.findPathButton)Button findPathButton;
    ArrayList<String> recents;
    int recentsCapacity = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            loadBuildings();
            loadIntersections();
            loadConnections();
            recents = new ArrayList<>(this.getResources().getInteger(R.integer.recentsCapacity));
            recentsCapacity = this.getResources().getInteger(R.integer.recentsCapacity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.map.post(new Runnable() {
            @Override
            public void run() {
                loadRecent();
            }
        });
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
                //ViewGroup.LayoutParams surface = map.getLayoutParams();
            }
        });

    }

    public void loadRecent(){
        Bundle extras = this.getIntent().getExtras();
        if(extras!=null){
            String recentString = extras.getString("recent");
            String []places= recentString.split(" ");
            if(places.length==2){
                ArrayAdapter<PlacePoint> from = (ArrayAdapter<PlacePoint>)spinnerFrom.getAdapter();
                ArrayAdapter<PlacePoint> to = (ArrayAdapter<PlacePoint>)spinnerTo.getAdapter();
                boolean notFound = false;

                for(int i=0;i<from.getCount();i++){
                    PlacePoint p = from.getItem(i);
                    if(p.getName().contentEquals(places[0])) {
                        spinnerFrom.setSelection(i);
                        break;
                    }
                    else if((i+1)==from.getCount())
                        notFound = true;
                }
                if(notFound ==false)
                    for(int i=0;i<to.getCount();i++){
                        PlacePoint p = from.getItem(i);
                        if(p.getName().contentEquals(places[1])) {
                            spinnerTo.setSelection(i);
                            break;
                        }
                        else if((i+1)==from.getCount())
                            notFound = true;
                    }
                if(notFound ==false)
                    this.findPathButton_Clicked();


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadRecentsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            writeRecentsToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRecentsFromFile() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+this.getResources().getString(R.string.recentsFileName));
        BufferedReader br = new BufferedReader(new FileReader(file));
        int recentsSize = this.getResources().getInteger(R.integer.recentsCapacity);
        for(int i=0;i<recentsSize;i++){
            String str = br.readLine();
            if(str == null)
                break;
            if(this.recents.size()-1 >= recentsCapacity)
                this.recents.remove(this.recents.size()-1);
            this.recents.add(0,str);
        }
        br.close();
    }
    public void updateRecents(PlacePoint p1, PlacePoint p2) throws IOException {
        if(this.recents.size() >= recentsCapacity)
            this.recents.remove(this.recents.size()-1);
        String str = p1.getName() + " " + p2.getName();
        if(this.recents.size()>0) {
            if (!str.contentEquals(this.recents.get(0)))
                this.recents.add(0, str);
        }
        else
            this.recents.add(0,str);

    }

    public void writeRecentsToFile() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+this.getResources().getString(R.string.recentsFileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(String s: this.recents){
            writer.write(s);
            writer.newLine();
        }
        writer.close();
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


    @OnClick(R.id.findPathButton)
    public void findPathButton_Clicked(){
        // SET LAYOUT
        if(spinnerFrom.getSelectedItem()!=null & spinnerTo.getSelectedItem()!=null ) {
            PlacePoint selectedFrom = (PlacePoint) spinnerFrom.getSelectedItem();
            PlacePoint selectedTo = (PlacePoint) spinnerTo.getSelectedItem();

            LinkedList<PlacePoint> shortestPath = map.getShortestPath(selectedFrom, selectedTo);
            if(shortestPath!=null) {
                map.drawPath(shortestPath);
                try {
                    updateRecents(selectedFrom,selectedTo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
