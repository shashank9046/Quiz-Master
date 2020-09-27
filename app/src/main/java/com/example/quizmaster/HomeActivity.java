package com.example.quizmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private GridView catGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");

        catGrid =findViewById(R.id.catGridView);

        List<String> catList = new ArrayList<>();

        catList.add("C++");
        catList.add("Java");
        catList.add("NodeJs");
        catList.add("Data Structure");
        catList.add("React Native");
        catList.add("A I");
        catList.add("Python");
        catList.add("CCV");
        catList.add("Machine Learning");
        catList.add("JavaScript");

        CatGridAdapter adapter =new CatGridAdapter(catList);
        catGrid.setAdapter(adapter);

    }
}