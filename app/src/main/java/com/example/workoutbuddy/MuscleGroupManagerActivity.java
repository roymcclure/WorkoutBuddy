package com.example.workoutbuddy;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class MuscleGroupManagerActivity extends AppCompatActivity {

    private Button btnSaveMuscleGroup, btnDeleteMuscleGroup;
    private DataHelper dh;
    private Spinner muscleSelect;
    private String m_inputText = "";
    private boolean m_wasInput=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_group_manager);

        btnDeleteMuscleGroup = (Button)findViewById(R.id.btnDeleteMuscleGroup);
        btnSaveMuscleGroup = (Button)findViewById(R.id.btnSaveMuscleGroup);
        muscleSelect = (Spinner)findViewById(R.id.spinnerMuscle);


        dh = new DataHelper(this);

        btnSaveMuscleGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMuscleGroupName();
            }
        });

        btnDeleteMuscleGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get element text selected in spinner
                if (String.valueOf(muscleSelect.getSelectedItem())!="") {
                    dh.deleteMuscleGroup(String.valueOf(muscleSelect.getSelectedItem()));
                    List<MuscleGroupRow> mg = dh.selectAllMuscleGroups();
                    populateSpinner();
                    if (mg.size()==0)
                        btnDeleteMuscleGroup.setEnabled(false);
                }
            }
        });

        // get list of muscle groups from db
        // if list is empty, then disable spinner, enable ADD NEW MUSCLE GROUP button, disable DELETE
        List<MuscleGroupRow> mg = dh.selectAllMuscleGroups();
        if (mg.size()==0) {
            muscleSelect.setEnabled(false);
        } else {
            muscleSelect.setEnabled(true);
            // populate
            populateSpinner();
            btnDeleteMuscleGroup.setEnabled(true);

        }
        // else select first element in spinner, enable ADD NEW MUSCLE GROUP, enable DELETE


    }

    private void populateSpinner() {
        List<MuscleGroupRow> mg = dh.selectAllMuscleGroups();
        ArrayAdapter<MuscleGroupRow> adapter = new ArrayAdapter<MuscleGroupRow>(this, android.R.layout.simple_spinner_dropdown_item,mg);
        muscleSelect.setAdapter(adapter);
    }

    private void getMuscleGroupName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type new muscle group");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_inputText = input.getText().toString();
                dh.insertMuscleGroup(m_inputText);
                populateSpinner();
                if (!muscleSelect.isEnabled())
                    muscleSelect.setEnabled(true);
                if (!btnDeleteMuscleGroup.isEnabled())
                    btnDeleteMuscleGroup.setEnabled(true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



}
