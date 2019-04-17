package com.example.workoutbuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnDataManager, btnStartTraining, btnCheckProgress;
    Intent intentDataManager, intentStartTraining;
    DataHelper dh;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dh = new DataHelper(this);
        context = this;
        btnDataManager = (Button)findViewById(R.id.btnDataManager);
        intentDataManager = new Intent(this, DataManagerActivity.class);
        btnDataManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentDataManager);
            }
        });

        btnStartTraining = (Button)findViewById(R.id.btnStartTraining);
        intentStartTraining = new Intent(this, StoreExerciseActivity.class);
        btnStartTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentStartTraining);
            }
        });

        btnCheckProgress = (Button)findViewById(R.id.btnCheckProgress);
        btnCheckProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ExerciseUnitRow> euRows= dh.selectAllExerciseUnits();
                for (ExerciseUnitRow e : euRows) {
                    //Toast toast = Toast.makeText(context, "[id:" + e.getId() + "],[exercise id:" + e.getExercise_id()+ "],[mg id:" + e.getMuscleGroup_id() + "],[reps1:" + e.getReps1() + "],[reps2:" + e.getReps2() + "],[reps3:" + e.getReps3() + "],[reps4:" + e.getReps4()
                    //  + "],[datetime:" + e.getDatetime()+"],[comments:" + e.getComments()+"]" ,Toast.LENGTH_SHORT);
                    Toast toast = Toast.makeText(context, "[datetime:" + e.getDatetime()+"],[comments:" + e.getComments()+"]" ,Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
