package com.ecrc.cituofflinemap;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class PreScreenActivity extends AppCompatActivity {
    @Bind(R.id.recentTextView)TextView recentText;
    @Bind(R.id.recentsListView)ListView recentsListView;
    String[] recents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_screen);
        ButterKnife.bind(this);
        recents = new String[this.getResources().getInteger(R.integer.recentsCapacity)];

    }
    @OnClick(R.id.gotomapButton)
    protected void gotoMapActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnItemClick(R.id.recentsListView)
    protected void gotoMapActivityWithPath(int position){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("recent", this.recents[position]);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadRecentsFile();
            displayRecents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadRecentsFile() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+this.getResources().getString(R.string.recentsFileName));
        if(!file.exists())
            file.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(file));
        for(int i=0;i<this.recents.length;i++){
            String str = br.readLine();
            if(str == null)
                break;
            this.recents[i] = str;
        }
        br.close();
    }
    protected void displayRecents(){
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) this.recentsListView.getAdapter();
        if(adapter == null) {
            adapter = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item);
        }
        adapter.clear();
        for(String s:this.recents){
            if(s!=null) {
                String temp[] = s.split(" ");
                String str1 = temp[0];
                String str2 = temp[1];
                str1 = str1.replace('_',' ');
                str2 = str2.replace('_', ' ');
                adapter.add(str1 + " to " + str2);
            }
        }
        this.recentsListView.setAdapter(adapter);
    }

}
