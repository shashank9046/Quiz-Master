package com.example.quizmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


//importing the category list in this activity which can be used in the adapter
import static com.example.quizmaster.MainActivity.catList;

//This is the category Activity
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


        //catList made in MainActivity is used here
        CatGridAdapter adapter =new CatGridAdapter(catList);
        catGrid.setAdapter(adapter);

    }
}