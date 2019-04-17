package com.example.workoutbuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataHelper {

    // Versión de la base de datos, indicada por el programador. En el
    // caso de que introduzcamos algún cambio deberíamos modificar este
    // número de versión, con lo que el MiOpenHelper determinará qué hacer
    private static final int DATABASE_VERSION = 3;

    
    private static final String[] exercise_units_column_names = App.getContext().getResources().getStringArray(R.array.exercise_units_column_names);
    private static final String[] exercises_column_names = App.getContext().getResources().getStringArray(R.array.exercises_column_names);
    
    // Queries to create tables
    private static final String CREATE_MUSCLE_GROUPS_TABLE = 
            "CREATE TABLE " + App.getContext().getString(R.string.table_name_muscle_groups) +
                    "(" + App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names)[0]+" INTEGER PRIMARY KEY, "
                        + App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names)[1]+" TEXT);";

    // Interestingly, no AUTO_INCREMENT is needed in SQLite.
    // when an insert is done on a table that has a INT PRIMARY KEY
    // if no value is given then it is autofilled with a suitable one
    private static final String INSERT_MUSCLE_GROUP = "INSERT INTO " + App.getContext().getString(R.string.table_name_muscle_groups) + "("+App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names)[1]+") values (?);";


    private static final String CREATE_EXERCISES_TABLE = "CREATE TABLE " +
            App.getContext().getString(R.string.table_name_exercises) +
            "("+
            exercises_column_names[0]+" INTEGER PRIMARY KEY, " + // id
            exercises_column_names[1]+" TEXT, " +                // descripcion
            exercises_column_names[2] + " INTEGER, " +           // muscle group id
            exercises_column_names[3] +" TEXT, " +               // picture
            exercises_column_names[4]+" TEXT," +                 // comments
            "FOREIGN KEY("+
            exercises_column_names[2]+
            ") REFERENCES " +
            App.getContext().getString(R.string.table_name_muscle_groups)+
            "("+
            App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names)[0]+
            ")) ";

    // Interestingly, no AUTO_INCREMENT is needed in SQLite.
    // when an insert is done on a table that has a INT PRIMARY KEY
    // if no value is given then it is autofilled with a suitable one
    private static final String INSERT_EXERCISE = "insert into " +
            App.getContext().getString(R.string.table_name_exercises) +
            "(" +
            exercises_column_names[1]+","+
            exercises_column_names[2]+","+
            exercises_column_names[3]+","+
            exercises_column_names[4]+
            ") values (?,?,?,?);";


    private static final String UPDATE_EXERCISE = "update " +
            App.getContext().getString(R.string.table_name_exercises)+
            " SET " +
            exercises_column_names[1]+"=?,"+
            exercises_column_names[2]+"=?,"+
            exercises_column_names[3]+"=?,"+
            exercises_column_names[4]+"=? "+
            " WHERE ID=?" +";";


    private static final String CREATE_EXERCISE_UNIT_TABLE = "CREATE TABLE " +
            App.getContext().getString(R.string.table_name_exercise_units) +
            "("+
            exercise_units_column_names[0]+" INTEGER PRIMARY KEY, " + //id
            exercise_units_column_names[1]+" INTEGER, " + //id ejercicio
            exercise_units_column_names[2] + " INTEGER, " + // primera rep
            exercise_units_column_names[3] + " INTEGER, " + // segunda rep
            exercise_units_column_names[4] + " INTEGER, " + // tercera rep
            exercise_units_column_names[5] + " INTEGER, " + // cuarta rep
            exercise_units_column_names[6] +" TEXT, " + // comments
            exercise_units_column_names[7]+" TEXT," + // datetime
            exercise_units_column_names[8]+" INTEGER," + // id routina
            exercise_units_column_names[9]+" INTEGER," + // id grupo muscular
            "FOREIGN KEY("+ exercise_units_column_names[1]+") REFERENCES " + App.getContext().getString(R.string.table_name_exercises)+"("+ exercises_column_names[0]+"),"+
            "FOREIGN KEY("+ exercise_units_column_names[9]+") REFERENCES " + App.getContext().getString(R.string.table_name_muscle_groups)+"("+ App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names)[0]+
            ")) ";

    private static final String INSERT_EXERCISE_UNIT = "insert into " +
            App.getContext().getString(R.string.table_name_exercise_units) +
            "(" +
            exercise_units_column_names[1]+","+ // id exercise
            exercise_units_column_names[2]+","+ // first rep
            exercise_units_column_names[3]+","+ // second rep
            exercise_units_column_names[4]+","+ // third rep
            exercise_units_column_names[5]+","+ // fourth rep
            exercise_units_column_names[6]+","+ // comments
            exercise_units_column_names[7]+","+ // date
            exercise_units_column_names[8]+","+ // id routine
            exercise_units_column_names[9]+     // id muscle group
            ") values (?,?,?,?,?,?,?,?,?);";

    // El contexto de la aplicación
    private Context context;

    // La instancia de la base de datos que nos
    // proporcionará el Helper (ya sea abriendo una base de
    // datos ya existente, creándola si no existe, o actualizándola
    // en el caso de algún cambio de versión)
    private SQLiteDatabase db;

    // Este atributo se utilizará durante la inserción
    private SQLiteStatement insertStatement, updateStatement;

    public DataHelper(Context context) {
        this.context = context;
        // Obtenemos un puntero a una base de datos sobre la que poder
        // escribir mediante la clase MiOpenHelper, que es una clase
        // privada definida dentro de DataHelper
        MiOpenHelper openHelper = new MiOpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();



    }

    public long updateExercise(int id, String name, int muscle_group, String path_picture, String comments) {

        this.updateStatement = this.db.compileStatement(UPDATE_EXERCISE);
        this.updateStatement.bindString(1,name);
        this.updateStatement.bindLong(2, muscle_group);
        this.updateStatement.bindString(3,path_picture);
        this.updateStatement.bindString(4,comments);
        this.updateStatement.bindLong(5, id);
        return this.updateStatement.executeUpdateDelete();
    }

    public long insertExercise(String name, int muscle_group, String path_picture, String comments) {

        // La inserción se realizará mediante lo que se conoce mediante
        // una sentencia SQL compilada. Asociamos al objeto insertStatement
        // el código SQL definido en la constante INSERT. Obsérvese que
        // este código SQL se trata de una sentencia SQL genérica, parametrizada
        // mediante el símbolo ?

        // Damos valor a los dos elementos genéricos (indicados por el símbolo ?)
        // de la sentencia de inserción compilada mediante bind
        this.insertStatement = this.db.compileStatement(INSERT_EXERCISE);
        this.insertStatement.bindString(1, name);
        this.insertStatement.bindLong(2, muscle_group);
        this.insertStatement.bindString(3, path_picture);
        this.insertStatement.bindString(4, comments);

        // Y llevamos a cabo la inserción
        return this.insertStatement.executeInsert();
    }

    public long insertExerciseUnit(int exerciseID, int reps1, int reps2, int reps3, int reps4, String comments, int routineID, int muscle_groupID) {

        // La inserción se realizará mediante lo que se conoce mediante
        // una sentencia SQL compilada. Asociamos al objeto insertStatement
        // el código SQL definido en la constante INSERT. Obsérvese que
        // este código SQL se trata de una sentencia SQL genérica, parametrizada
        // mediante el símbolo ?

        // Damos valor a los dos elementos genéricos (indicados por el símbolo ?)
        // de la sentencia de inserción compilada mediante bind
        this.insertStatement = this.db.compileStatement(INSERT_EXERCISE_UNIT);
        this.insertStatement.bindLong(1, exerciseID);
        this.insertStatement.bindLong(2, reps1);
        this.insertStatement.bindLong(3, reps2);
        this.insertStatement.bindLong(4, reps3);
        this.insertStatement.bindLong(5, reps4);
        this.insertStatement.bindString(6, comments);
        // generate Date
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(date);

        this.insertStatement.bindString(7, strDate);
        this.insertStatement.bindLong(8, routineID);
        this.insertStatement.bindLong(9, muscle_groupID);

        // Y llevamos a cabo la inserción
        return this.insertStatement.executeInsert();
    }

    public long insertMuscleGroup(String name) {
        this.insertStatement = this.db.compileStatement(INSERT_MUSCLE_GROUP);
        this.insertStatement.bindString(1, name);
        return this.insertStatement.executeInsert();
    }

    public int deleteAll() {

        // En este caso hacemos uso de un método de la instancia de la base
        // de datos para realizar el borrado. Existen también métodos
        // para hacer queries y otras operaciones con la base de  datos
        int total = 0;
        total +=db.delete(App.getContext().getString(R.string.table_name_exercises), null, null);
        total +=db.delete(App.getContext().getString(R.string.table_name_muscle_groups), null, null);
        total +=db.delete(App.getContext().getString(R.string.table_name_exercise_units), null, null);
        return total;
    }

    public int deleteExercise(int id) {
        String[] ids = { String.valueOf(id) };
        return db.delete(App.getContext().getString(R.string.table_name_exercises), "id=?", ids);
    }

    public String getExercisePicturePath(int id, Context context) {
        String[] columns = {"picture"};
        String[] ids = { String.valueOf(id)};
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_exercises), columns,
                "id=?", ids, null, null, "ID DESC");
        String path ="";
        if (cursor.moveToFirst()) {

            do {
                // Añadimos a la lista que devolveremos como salida
                // del método el nombre de la ciudad en la posición actual

                path = cursor.getString(cursor.getColumnIndex("picture"));
                // El método moveToNext devolverá false en el caso de que se
                // haya llegado al final
            } while (cursor.moveToNext());

        }
        if (cursor!=null)
            cursor.close();
        /*Toast toast = Toast.makeText(context, "devolviendo:" + path,Toast.LENGTH_LONG);
        toast.show();*/
        return path;
    }

    public int deleteMuscleGroup(String name) {
        String[] names = { name };
        return db.delete(App.getContext().getString(R.string.table_name_muscle_groups), "name=?", names);
    }

    public int deleteExerciseUnit(int id) {
        String[] ids = { String.valueOf(id) };
        return db.delete(App.getContext().getString(R.string.table_name_exercise_units), "id=?", ids);
    }

    public List<MuscleGroupRow> selectAllMuscleGroups() {
        List<MuscleGroupRow> list = new ArrayList<MuscleGroupRow>();
        // La siguiente instrucción almacena en un cursor todos los valores
        // de las columnas indicadas en COLUMNAS de la tabla TABLE_NAME
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_muscle_groups), App.getContext().getResources().getStringArray(R.array.muscle_groups_column_names),
                null, null, null, null, "ID DESC");
        // El cursor es un iterador que nos permite ir recorriendo
        // los resultados devueltos secuencialmente
        if (cursor.moveToFirst()) {
            do {
            // Añadimos a la lista que devolveremos como salida
            // del método el nombre de la ciudad en la posición actual

                list.add(new MuscleGroupRow(cursor.getInt(0),cursor.getString(1)));
                // El método moveToNext devolverá false en el caso de que se
                // haya llegado al final
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public List<ExerciseRow> selectAllExercises() {
        List<ExerciseRow> list = new ArrayList<ExerciseRow>();
        // La siguiente instrucción almacena en un cursor todos los valores
        // de las columnas indicadas en COLUMNAS de la tabla TABLE_NAME
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_exercises), exercises_column_names,
                null, null, null, null, "ID DESC");
        // El cursor es un iterador que nos permite ir recorriendo
        // los resultados devueltos secuencialmente
        if (cursor.moveToFirst()) {
            do {
                // Añadimos a la lista que devolveremos como salida
                // del método el nombre de la ciudad en la posición actual

                list.add(new ExerciseRow(
                        cursor.getInt(0), // id ejercicio
                        cursor.getString(1),// nombre ej
                        cursor.getInt(2), // id muscleGroup
                        cursor.getString(3),//picture_path
                        cursor.getString(4)//comments
                        ));
                // El método moveToNext devolverá false en el caso de que se
                // haya llegado al final
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    // select all exercise units for a muscle group
    public List<ExerciseRow> selectAllExercises(int muscleGroup_id) {
        List<ExerciseRow> list = new ArrayList<ExerciseRow>();
        // La siguiente instrucción almacena en un cursor todos los valores
        // de las columnas indicadas en COLUMNAS de la tabla TABLE_NAME
        String[] ids = { String.valueOf(muscleGroup_id) };
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_exercises), exercises_column_names,
                exercises_column_names[2]+"=?", ids, null, null, "ID DESC");
        // El cursor es un iterador que nos permite ir recorriendo
        // los resultados devueltos secuencialmente
        if (cursor.moveToFirst()) {
            do {
                // Añadimos a la lista que devolveremos como salida
                // del método el nombre de la ciudad en la posición actual

                list.add(new ExerciseRow(
                        cursor.getInt(0), // id ejercicio
                        cursor.getString(1),// nombre ej
                        cursor.getInt(2), // id muscleGroup
                        cursor.getString(3),//picture file path
                        cursor.getString(4)//comments
                ));
                // El método moveToNext devolverá false en el caso de que se
                // haya llegado al final
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public ExerciseUnitRow selectLastExerciseUnit(int id) {
        String[] ids = { String.valueOf(id)};
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_exercise_units), exercise_units_column_names,
                exercise_units_column_names[1]+"=?", ids, null, null, "DATE DESC");
        ExerciseUnitRow eu = null;
        if (cursor.moveToFirst()) {
            eu = new ExerciseUnitRow(
                    cursor.getInt(0), // id unidad
                    cursor.getInt(1),// id ejercicio
                    cursor.getInt(9), // grupo muscular
                    cursor.getInt(2), // first rep
                    cursor.getInt(3), // second rep
                    cursor.getInt(4), // third rep
                    cursor.getInt(5), // fourth rep
                    cursor.getString(6),//comments
                    cursor.getString(7)//datetime

            );
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return eu;
    }

    public List<ExerciseUnitRow> selectAllExerciseUnits() {
        // La siguiente instrucción almacena en un cursor todos los valores
        // de las columnas indicadas en COLUMNAS de la tabla TABLE_NAME
        List<ExerciseUnitRow> list = new ArrayList<ExerciseUnitRow>();
        Cursor cursor = db.query(App.getContext().getString(R.string.table_name_exercise_units), exercise_units_column_names,
                null, null, null, null, "ID DESC");
        if (cursor.moveToFirst()) {
            do {
                // Añadimos a la lista que devolveremos como salida
                // del método el nombre de la ciudad en la posición actual
                list.add(new ExerciseUnitRow(
                        cursor.getInt(0), // id unidad
                        cursor.getInt(1),// id ejercicio
                        cursor.getInt(9), // grupo muscular
                        cursor.getInt(2), // first rep
                        cursor.getInt(3), // second rep
                        cursor.getInt(4), // third rep
                        cursor.getInt(5), // fourth rep
                        cursor.getString(6),//comments
                        cursor.getString(7)//datetime

                ));
                // El método moveToNext devolverá false en el caso de que se
                // haya llegado al final
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;

    }

    // Esta clase privada del DataHelper se encarga de proporcionar una instancia
    // de base de datos a DataHelper sobre la que poder trabajar.
    private static class MiOpenHelper extends SQLiteOpenHelper {
        private static Context context;
        MiOpenHelper(Context context) {
            super(context, App.getContext().getString(R.string.db_filename), null, DATABASE_VERSION);
            this.context = context;
        }

        // Este método se utilizará en el caso en el que la base de datos no  existiera
        @Override
        public void onCreate(SQLiteDatabase db) {
            Toast toast = Toast.makeText(this.context, CREATE_EXERCISES_TABLE, Toast.LENGTH_LONG);
            toast.show();
            toast = Toast.makeText(this.context, CREATE_MUSCLE_GROUPS_TABLE, Toast.LENGTH_LONG);
            toast.show();
            db.execSQL(CREATE_EXERCISES_TABLE);
            db.execSQL(CREATE_MUSCLE_GROUPS_TABLE);
            db.execSQL(CREATE_EXERCISE_UNIT_TABLE);
        }

        // Este método se ejecutará en el caso en el que se cambie el valor
        // de la constante DATABASE_VERSION. En este caso se borra la base
        // de datos anterior antes de crear una nueva, pero lo ideal sería
        // transferir los datos desde la versión anterior a la nueva
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int
                newVersion) {
            Log.w("SQL","onUpgrade: eliminando tabla si existe y creándola de nuevo");
                    db.execSQL("DROP TABLE IF EXISTS " + App.getContext().getString(R.string.table_name_exercises));
            db.execSQL("DROP TABLE IF EXISTS " + App.getContext().getString(R.string.table_name_muscle_groups));
            db.execSQL("DROP TABLE IF EXISTS " + App.getContext().getString(R.string.table_name_exercise_units));
            onCreate(db);
        }
    }
}