package com.example.workoutbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class StoreExerciseActivity extends AppCompatActivity {

    DataHelper dh;
    Spinner spinnerExercise, spinnerMuscleGroup;
    Button btnSaveWorkedOut, btnDeleteWorkedOut;
    NumberPicker repsPicker1, repsPicker2, repsPicker3, repsPicker4;
    Context context;
    EditText editTextComments;
    ImageView mExerciseImageView;
    int currentSelectedExUnitId=-1;

    private String mCurrentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_exercise);

        dh = new DataHelper(this);
        context = this;
        spinnerExercise = (Spinner)findViewById(R.id.spinnerExercises);
        spinnerMuscleGroup = (Spinner)findViewById(R.id.spinnerMuscleGroups);


        populateMuscleGroupSpinner();
        populateExerciseSpinner();

        repsPicker1 = findViewById(R.id.Picker1);
        repsPicker2 = findViewById(R.id.Picker2);
        repsPicker3 = findViewById(R.id.Picker3);
        repsPicker4 = findViewById(R.id.Picker4);

        mExerciseImageView = (ImageView)findViewById(R.id.mExercisePicture);

        editTextComments = (EditText)findViewById(R.id.editTextComments);

        btnSaveWorkedOut=(Button)findViewById(R.id.btnSaveWorkedOut);
        btnSaveWorkedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // solicitar confirmación
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //insert exercise unit
                                if (spinnerExercise.getCount()>0) {
                                    ExerciseRow er = (ExerciseRow) spinnerExercise.getSelectedItem();
                                    // todo: insert proper routine exercise
                                    dh.insertExerciseUnit(er.getId(), repsPicker1.getValue(), repsPicker2.getValue(), repsPicker3.getValue(), repsPicker4.getValue(), editTextComments.getText().toString(), 0, er.getMuscleGroup_id());
<<<<<<< HEAD
=======
                                    Utilities.longToast(context, "Unit was saved. Going to next exercise...");
>>>>>>> next_Exercise
                                    spinnerExercise.setSelection((spinnerExercise.getSelectedItemPosition()+1)%spinnerExercise.getCount());

                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;
                        }
                    }




                };
                ExerciseRow er = (ExerciseRow) spinnerExercise.getSelectedItem();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                if (dh.selectLastExerciseUnit(er.getId())!=null) {

                    builder2.setMessage("Save this exercise unit?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                } else {
                    builder2.setMessage("This exercise was already done today. Still want to save this exercise unit?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }

            }
        });

        btnDeleteWorkedOut = (Button)findViewById(R.id.btnDeleteWorkedOut);
        btnDeleteWorkedOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // solicitar confirmación
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // get exercise unit row id, and delete it
                                dh.deleteExerciseUnit(currentSelectedExUnitId);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Utilities.shortToast(context, "Phew!!");
                                break;
                        }
                    }




                };
                if (currentSelectedExUnitId==-1) {
                    Utilities.longToast(context, "No exercise unit selected.");
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setMessage("Really, delete this exercise unit?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });

        if (spinnerExercise.getCount()==0){
            btnSaveWorkedOut.setEnabled(false);
        }

        // upon selection of an exercise
        spinnerExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerExercise.getSelectedItem().toString().equals("")){
                    // clear the picture area
                    mExerciseImageView.setImageResource(android.R.color.black);
                } else {
                    // set picture
                    int ex_id = ((ExerciseRow)spinnerExercise.getSelectedItem()).getId();
                    ExerciseRow er = (ExerciseRow)spinnerExercise.getSelectedItem();
                    mCurrentPhotoPath = dh.getExercisePicturePath(ex_id, context);
                    if (!mCurrentPhotoPath.equals(""))
                        setPic();
                    ExerciseUnitRow exerUnitRow = dh.selectLastExerciseUnit(ex_id);
                    if (exerUnitRow!=null) {
                        repsPicker1.setValue(exerUnitRow.getReps1());
                        repsPicker2.setValue(exerUnitRow.getReps2());
                        repsPicker3.setValue(exerUnitRow.getReps3());
                        repsPicker4.setValue(exerUnitRow.getReps4());
                        editTextComments.setText(exerUnitRow.getComments());
                        currentSelectedExUnitId = exerUnitRow.getId();
                    } else {
                        currentSelectedExUnitId = -1;
                        editTextComments.setText(er.getComments());
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMuscleGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateExerciseSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // if an exercise has been done for the day, add a check mark next to it
    private void populateExerciseSpinner() {
        // obtain id of selected muscle group
        MuscleGroupRow mgr = (MuscleGroupRow) spinnerMuscleGroup.getSelectedItem();
        List<ExerciseRow> mg = dh.selectAllExercises(mgr.getID());
        ArrayAdapter<ExerciseRow> adapter = new ArrayAdapter<ExerciseRow>(this, android.R.layout.simple_spinner_dropdown_item,mg);
        spinnerExercise.setAdapter(adapter);
    }

    private void populateMuscleGroupSpinner() {
        List<MuscleGroupRow> mg = dh.selectAllMuscleGroups();
        ArrayAdapter<MuscleGroupRow> adapter = new ArrayAdapter<MuscleGroupRow>(this, android.R.layout.simple_spinner_dropdown_item,mg);
        spinnerMuscleGroup.setAdapter(adapter);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mExerciseImageView.getWidth();
        int targetH = mExerciseImageView.getHeight();
        //System.out.println("photo path:"+mCurrentPhotoPath);
        //System.out.println("imageview w:"+targetW+",imageview h:"+targetW);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        //System.out.println("photo w:"+photoW+",photo h:"+photoH);
        // Determine how much to scale down the image
        //System.out.println("photoW / targetW es "+photoW/targetW);
        //System.out.println("photoH  es "+photoH);
        //System.out.println("targetH  es "+targetH);
        //System.out.println("photoH / targetH es "+photoH/targetH);
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        //System.out.println("El scale factor calculado es "+scaleFactor);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (mExerciseImageView==null){
            System.out.println("Soy un pedazo de mierda que debería lanzar una excepción.");
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            System.out.println("ancho y alto del bitmap fichero:"+bitmap.getWidth()+","+bitmap.getHeight());
            Bitmap resizedbitmap1=Bitmap.createBitmap(bitmap, (bitmap.getWidth()-mExerciseImageView.getHeight())/2,0,mExerciseImageView.getWidth(), mExerciseImageView.getHeight());
            System.out.println("Estoy pasando a createBitmap los siguientes parametros:0,"+(bitmap.getWidth()-mExerciseImageView.getHeight())/2+","+mExerciseImageView.getWidth()+","+mExerciseImageView.getHeight());
            //System.out.println("Tamaño del bitmap final:"+resizedbitmap1.getWidth()+","+resizedbitmap1.getHeight());
            mExerciseImageView.setImageBitmap(resizedbitmap1);
            Matrix matrix = new Matrix();
            mExerciseImageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
            matrix.postRotate((float) 90, mExerciseImageView.getPivotX(), mExerciseImageView.getPivotY());
            mExerciseImageView.setImageMatrix(matrix);
        }

    }


}
