package com.example.workoutbuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DataManagerActivity extends AppCompatActivity {

    private Button mngRoutines, mngExercises, mngMuscleGroups;
    private Intent intent;
    private Context yo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_manager);
    yo = this;
        mngRoutines = (Button)findViewById(R.id.btnManageRoutines);
        mngExercises = (Button)findViewById(R.id.btnManageExercises);
        mngMuscleGroups = (Button)findViewById(R.id.btnManageMuscleGroups);



        mngExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(yo, ManageExerciseActivity.class);
                startActivity(intent);
            }
        });
        mngMuscleGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(yo, MuscleGroupManagerActivity.class);
                startActivity(intent);
            }
        });

    }
}
