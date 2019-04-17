package com.example.workoutbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ManageExerciseActivity extends AppCompatActivity {

    DataHelper dh;
    Spinner spinnerMuscleGroup, spinnerExercise;
    Button btnSaveExercise, btnDeleteExercise;
    EditText edTxtExerciseName, editComments;
    ImageView mImageView;

    private final static String NEW_EXERCISE_STRING = "New exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_exercise);

        // interface elements
        spinnerMuscleGroup = (Spinner)findViewById(R.id.spinnerMuscleGroup);
        spinnerExercise = (Spinner)findViewById(R.id.spinnerExercise);
        btnSaveExercise = (Button)findViewById(R.id.btnSaveExercise);
        btnDeleteExercise = (Button)findViewById(R.id.btnDeleteExercise);
        edTxtExerciseName = (EditText)findViewById(R.id.edTxtExerciseName);
        editComments = (EditText)findViewById(R.id.editComments);
        mImageView = (ImageView)findViewById(R.id.imageView);

        // get context so we can use Toast inside listeners
        final Context context = this;

        btnSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if selected item in selectExercise spinner is new then validate fields then insert
                if (spinnerExercise.getSelectedItem().toString().equals(NEW_EXERCISE_STRING)) {
                    if (fieldsValid()) {
                        // obtener id del grupo muscular
                        int mg_id = ((MuscleGroupRow)spinnerMuscleGroup.getSelectedItem()).getID();
                        // llamar a insert
                        String exName = edTxtExerciseName.getText().toString();
                        String comments = editComments.getText().toString();
                        dh.insertExercise(exName, mg_id, mCurrentPhotoPath,comments);
                        Toast toast = Toast.makeText(context, "Exercise was saved: "+edTxtExerciseName.getText(), Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, "Por favor introduce un nombre para el ejercicio y foto.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else { // otherwise validate fields and update
                    if (fieldsValid()) {
                        // lanzar update
                        dh.updateExercise(
                                ((ExerciseRow)spinnerExercise.getSelectedItem()).getId(),
                                edTxtExerciseName.getText().toString(),
                                ((MuscleGroupRow)spinnerMuscleGroup.getSelectedItem()).getID(),
                                mCurrentPhotoPath,
                                editComments.getText().toString());
                    }
                }


            }
        });

        // ELIMINAR UN EJERCICIO

        btnDeleteExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for confirmation: sure you wanna delete?
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Delete from database, update UI elements
                                ExerciseRow r = (ExerciseRow)spinnerExercise.getSelectedItem();
                                dh.deleteExercise(r.getId());
                                populateExerciseSpinner();
                                mImageView.setImageResource(android.R.color.black);
                                edTxtExerciseName.setText("Please type a name for the exercise");
                                // todo: borrar el fichero de la imagen!

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Really, delete exercise \"" + spinnerExercise.getSelectedItem().toString() +"\"?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        dh = new DataHelper(this);


        // populate spinners
        populateMuscleGroupSpinner();
        populateExerciseSpinner();

        // work in progress
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // upon selection of an exercise
        spinnerExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerExercise.getSelectedItem().toString().equals(NEW_EXERCISE_STRING)){
                    // clear the picture area
                    mImageView.setImageResource(android.R.color.black);
                    edTxtExerciseName.setEnabled(true);
                    // cannot delete what is not yet created
                    btnDeleteExercise.setEnabled(false);
                    btnSaveExercise.setText("SAVE NEW EXERCISE");
                    mCurrentPhotoPath = "";
                } else {
                    ExerciseRow er =  (ExerciseRow)spinnerExercise.getSelectedItem();
                    // exercise name
                    edTxtExerciseName.setEnabled(true);
                    edTxtExerciseName.setText(er.getName());
                    // let user delete exercise
                    btnDeleteExercise.setEnabled(true);
                    // we obtain
                     MuscleGroupRow mgrow = (MuscleGroupRow)spinnerMuscleGroup.getSelectedItem();
                     int count = 0;
                     while (mgrow.getID() != er.getMuscleGroup_id() && count < spinnerMuscleGroup.getCount()) {
                         spinnerMuscleGroup.setSelection((spinnerMuscleGroup.getSelectedItemPosition() + 1)%spinnerMuscleGroup.getCount());
                         mgrow = (MuscleGroupRow)spinnerMuscleGroup.getSelectedItem();
                         count++;
                     }

                    // set picture
                    mCurrentPhotoPath = dh.getExercisePicturePath(er.getId(), context);
                    btnSaveExercise.setText("UPDATE EXERCISE DATA");
                    if (!mCurrentPhotoPath.equals(""))
                        setPic();
                    editComments.setText(er.getComments());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void populateMuscleGroupSpinner() {
        List<MuscleGroupRow> mg = dh.selectAllMuscleGroups();
        ArrayAdapter<MuscleGroupRow> adapter = new ArrayAdapter<MuscleGroupRow>(this, android.R.layout.simple_spinner_dropdown_item, mg);
        spinnerMuscleGroup.setAdapter(adapter);
    }

    private void populateExerciseSpinner() {
        List<ExerciseRow> mg = dh.selectAllExercises();
        mg.add(new ExerciseRow(0,"New exercise",0,"",""));
        ArrayAdapter<ExerciseRow> adapter = new ArrayAdapter<ExerciseRow>(this, android.R.layout.simple_spinner_dropdown_item,mg);
        spinnerExercise.setAdapter(adapter);
    }

    private boolean fieldsValid() {
        // name field at least 3 chars long
        return edTxtExerciseName.getText().toString().length() >0 && !mCurrentPhotoPath.equals("");
    }


    // get the code
    static final int REQUEST_TAKE_PHOTO = 1;

    private String mCurrentPhotoPath="";

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.roymcclure.workoutbuddy.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
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

        if (mImageView==null )
            System.out.println("Soy un pedazo de mierda que debería lanzar una excepción.");

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if (bitmap != null) {
            System.out.println("ancho y alto del bitmap fichero:"+bitmap.getWidth()+","+bitmap.getHeight());
            Bitmap resizedbitmap1=Bitmap.createBitmap(bitmap, (bitmap.getWidth()-mImageView.getHeight())/2,0,mImageView.getWidth(), mImageView.getHeight());
            System.out.println("Estoy pasando a createBitmap los siguientes parametros:0,"+(bitmap.getWidth()-mImageView.getHeight())/2+","+mImageView.getWidth()+","+mImageView.getHeight());
            //System.out.println("Tamaño del bitmap final:"+resizedbitmap1.getWidth()+","+resizedbitmap1.getHeight());
            mImageView.setImageBitmap(resizedbitmap1);
            Matrix matrix = new Matrix();
            mImageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
            matrix.postRotate((float) 90, mImageView.getPivotX(), mImageView.getPivotY());
            mImageView.setImageMatrix(matrix);
        }

    }
}
