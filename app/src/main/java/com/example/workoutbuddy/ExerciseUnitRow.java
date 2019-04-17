package com.example.workoutbuddy;

public class ExerciseUnitRow {

    private String comments;
    private int id;
    private int muscleGroup_id;
    private int exercise_id;
    private int reps1;
    private int reps2;
    private int reps3;
    private int reps4;
    private String datetime;

    public ExerciseUnitRow(int id_,int ex_id, int mg_id, int reps1, int reps2, int reps3, int reps4, String comments_, String datetime) {

        this.setComments(comments_);
        this.setId(id_);
        this.setMuscleGroup_id(mg_id);
        this.setExercise_id(ex_id);
        this.setReps1(reps1);
        this.setReps2(reps2);
        this.setReps3(reps3);
        this.setReps4(reps4);
        this.setDatetime(datetime);
    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMuscleGroup_id() {
        return muscleGroup_id;
    }

    public void setMuscleGroup_id(int muscleGroup_id) {
        this.muscleGroup_id = muscleGroup_id;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getReps1() {
        return reps1;
    }

    public void setReps1(int reps1) {
        this.reps1 = reps1;
    }

    public int getReps2() {
        return reps2;
    }

    public void setReps2(int reps2) {
        this.reps2 = reps2;
    }

    public int getReps3() {
        return reps3;
    }

    public void setReps3(int reps3) {
        this.reps3 = reps3;
    }

    public int getReps4() {
        return reps4;
    }

    public void setReps4(int reps4) {
        this.reps4 = reps4;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
